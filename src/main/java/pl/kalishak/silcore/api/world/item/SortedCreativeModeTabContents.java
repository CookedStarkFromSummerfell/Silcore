package pl.kalishak.silcore.api.world.item;

import net.minecraft.core.Holder;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import pl.kalishak.silcore.api.registry.DeferredCreativeModeTabRegister;

import java.util.*;

/**
 * Sorting utility for {@link CreativeModeTab}
 */
public class SortedCreativeModeTabContents implements CreativeModeTab.Output {
    private final CreativeModeTab.ItemDisplayParameters itemDisplayParameters;
    private final List<SortedTabEntry> tabContents = new ArrayList<>();
    private final Map<FeatureFlag, SortedCreativeModeTabContents> conditionalLookups = new LinkedHashMap<>();

    /**
     * New instance is being created at {@link pl.kalishak.silcore.api.registry.DeferredCreativeModeTabRegister#registerSortedCreativeModeTab}
     * @param itemDisplayParameters display Parameters, useful with adding and sorting a {@link FeatureFlag} content
     */
    public SortedCreativeModeTabContents(CreativeModeTab.ItemDisplayParameters itemDisplayParameters) {
        this.itemDisplayParameters = itemDisplayParameters;
    }

    @Override
    public void accept(ItemStack itemStack, CreativeModeTab.TabVisibility tabVisibility) {
        add(new SortedTabEntry(itemStack.getItemHolder(), tabVisibility, null));
    }

    /**
     * Adds entry to contents
     * @param entry Entry with defined Holder, DisplayParameter and optional Priority
     */
    public void add(SortedTabEntry entry) {
        this.tabContents.add(entry);
    }

    /**
     * Adds entry with defined Holder and Priority
     * @param holder Item's holder
     * @param category Entry's priority
     */
    public void add(Holder<Item> holder, SortedTabEntry.Category category) {
        this.tabContents.add(new SortedTabEntry(holder, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, category));
    }

    /**
     * Adds Holder as Entry
     * @param holder Item's holder
     */
    public void add(Holder<Item> holder) {
        this.tabContents.add(new SortedTabEntry(holder));
    }

    /**
     * Conditionally adds given Holder with Priority when FeatureFlag is present in-game
     * @param holder Item's holder
     * @param category Entry's priority
     * @param featureFlag Feature that enables provided item
     */
    public void addConditionally(Holder<Item> holder, SortedTabEntry.Category category, FeatureFlag featureFlag) {
        if (this.itemDisplayParameters.enabledFeatures().contains(featureFlag)) {
            getConditionalLookup(featureFlag).add(holder, category);
        }

    }

    /**
     * Conditionally adds given Holder when FeatureFlag is present in-game
     * @param holder Item's holder
     * @param featureFlag Feature that enables provided item
     */
    public void addConditionally(Holder<Item> holder, FeatureFlag featureFlag) {
        if (this.itemDisplayParameters.enabledFeatures().contains(featureFlag)) {
            getConditionalLookup(featureFlag).add(holder);
        }
    }

    /**
     * Creates or gets child content of given FeatureFlag
     * @param featureFlag Content FeatureFlag
     * @return Child content
     */
    private SortedCreativeModeTabContents getConditionalLookup(FeatureFlag featureFlag) {
        SortedCreativeModeTabContents sortedCreativeModeTabContents = this.conditionalLookups.get(featureFlag);

        if (sortedCreativeModeTabContents == null) {
            sortedCreativeModeTabContents = new SortedCreativeModeTabContents(this.itemDisplayParameters);
            this.conditionalLookups.put(featureFlag, sortedCreativeModeTabContents);
        }

        return sortedCreativeModeTabContents;
    }

    /**
     * @return Set containing all used FeatureFlags
     */
    public Set<FeatureFlag> getOptionalFeatures() {
        return this.conditionalLookups.keySet();
    }

    /**
     * Eventbound method, fires once CreativeModeTab registered via {@link DeferredCreativeModeTabRegister} has any FeatureFlags.
     * <br>
     * Adds conditional entries and then sorts them all within already existing content
     * @see net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
     * @param sortingPolicy A method of sorting
     * @param output CreativeModeTab output
     * @param featureFlags Used FeatureFlags
     */
    public void sortedInEvent(SortingPolicy sortingPolicy, CreativeModeTab.Output output, FeatureFlag... featureFlags) {
        Arrays.stream(featureFlags).filter(this.conditionalLookups::containsKey).forEach(featureFlag -> this.tabContents.addAll(this.conditionalLookups.get(featureFlag).tabContents));

        if (this.tabContents.isEmpty()) {
            throw new IllegalStateException("Items are either already sorted nor items were provided");
        }

        sort(sortingPolicy, output);
    }

    /**
     * Sorts content
     * @param sortingPolicy A method of sorting
     * @param output CreativeModeTab output
     */
    public void sort(SortingPolicy sortingPolicy, CreativeModeTab.Output output) {
        if (this.tabContents.isEmpty()) {
            throw new IllegalStateException("Items are either already sorted nor items were provided");
        }

        Comparator<SortedTabEntry> comparator = switch (sortingPolicy) {
            case BY_NAME -> Comparator.comparing(SortedTabEntry::getKey);
            case BY_CATEGORY -> Comparator.comparing(sorted -> sorted.getPriority().map(SortedTabEntry.Category::getPriority).orElseThrow(() -> new IllegalStateException("Cannot sort by priority when it's null!")));
            default -> Comparator.<SortedTabEntry>comparingInt(sorted -> sorted.getPriority()
                            .map(SortedTabEntry.Category::getPriority)
                            .orElseThrow(() -> new IllegalStateException("Cannot sort by priority when it's null!")))
                    .thenComparing(SortedTabEntry::getKey);
        };

        this.tabContents.sort(comparator);
        this.tabContents.forEach(sortedTabEntry -> output.accept(sortedTabEntry.getHolder().value(), sortedTabEntry.tabVisibility()));
        this.tabContents.clear();
    }

    /**
     * Sorting policy marks how entries are sorted
     */
    public enum SortingPolicy {
        /**
         * Sorts by alphabetical order
         */
        BY_NAME,
        /**
         * Sorts by numbers
         */
        BY_CATEGORY,
        /**
         * Sorts in previous orders respectfully
         */
        MIXED;
    }
}
