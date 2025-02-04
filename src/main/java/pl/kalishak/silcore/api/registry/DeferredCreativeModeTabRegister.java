package pl.kalishak.silcore.api.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import pl.kalishak.silcore.api.world.item.SortedCreativeModeTabContents;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Specialized DeferredRegister for {@link CreativeModeTab CreatieveModTabs} that uses utilities for sorting {@link net.minecraft.world.item.Item} in registered CreativeModeTab
 */
public class DeferredCreativeModeTabRegister extends DeferredRegister<CreativeModeTab> {
    private final Map<ResourceKey<CreativeModeTab>, SortedCreativeModeTabContents> featuresAppender = new ConcurrentHashMap<>();
    
    protected DeferredCreativeModeTabRegister(String namespace) {
        super(Registries.CREATIVE_MODE_TAB, namespace);
    }
    
    public static DeferredCreativeModeTabRegister createCreativeModeTabs(String namespace) {
        return new DeferredCreativeModeTabRegister(namespace);
    }

    /**
     * Adds a new CreativeModeTab to the list of entries to be registered and returns a {@link DeferredHolder} that will be populated with the created creative mode tab automatically.
     * @param name The new CreativeModeTab's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param builder Builder of new CreativeModeTab
     * @return A {@link DeferredHolder} that will track updates from the registry for this CreativeModeTab.
     */
    public DeferredHolder<CreativeModeTab, CreativeModeTab> registerCreativeModeTab(String name, CreativeModeTab.Builder builder) {
        return register(name, builder::build);
    }

    /**
     * Adds a new CreativeModeTab to the list of entries to be registered and returns a {@link DeferredHolder} that will be populated with the created creative mode tab automatically.
     * @param name The new CreativeModeTab's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param builder Builder of new creative mode tab, make sure to not call {@link CreativeModeTab.Builder#buildContents}, since it may clash with {@linkplain SortedItemAppender appender}.
     * @param appender Adds sorted entries similarity to default NeoForge method and makes possible to conditionally add items to tab.
     * @param optionalFeature Optional {@link FeatureFlag} providing content, may be null when if there's no need for any conditional Items.
     * @return A {@link DeferredHolder} that will track updates from the registry for this CreativeModeTab.
     */
    public DeferredHolder<CreativeModeTab, CreativeModeTab> registerSortedCreativeModeTab(String name, CreativeModeTab.Builder builder, SortedItemAppender appender, @Nullable FeatureFlag optionalFeature) {
        return registerCreativeModeTab(name, builder.displayItems(((parameters, output) -> {
            SortedCreativeModeTabContents sortedCreativeModeTabContents = new SortedCreativeModeTabContents(parameters);
            appender.accept(parameters, output, sortedCreativeModeTabContents, optionalFeature);

            this.featuresAppender.put(ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(getNamespace(), name)), sortedCreativeModeTabContents);
        })));
    }

    /**
     * Registers CreativeModeTabs and a listener to modify registered {@link CreativeModeTab} once enabled {@link FeatureFlag} may change.
     * @param bus A mod's unique bus.
     * @param sortingPolicy Method of sorting.
     * @see #register(IEventBus)
     */
    public void register(IEventBus bus, SortedCreativeModeTabContents.SortingPolicy sortingPolicy) {
        super.register(bus);
        bus.addListener(BuildCreativeModeTabContentsEvent.class, event -> buildOptionalContents(event, this.featuresAppender, sortingPolicy));
    }

    private static void buildOptionalContents(BuildCreativeModeTabContentsEvent event, Map<ResourceKey<CreativeModeTab>, SortedCreativeModeTabContents> map, SortedCreativeModeTabContents.SortingPolicy sortingPolicy) {
        SortedCreativeModeTabContents sortedCreativeModeTabContents = map.get(event.getTabKey());

        if (sortedCreativeModeTabContents != null) {
            sortedCreativeModeTabContents.getOptionalFeatures()
                    .stream()
                    .filter(event.getParameters().enabledFeatures()::contains)
                    .forEach(feature -> sortedCreativeModeTabContents.sortedInEvent(sortingPolicy, event, feature));
        }
    }

    /**
     * Utility interface to help sort items in registered {@link CreativeModeTab}
     */
    @FunctionalInterface
    public interface SortedItemAppender {
        /**
         * @param itemDisplayParameters Parameters needed in checking enabled {@link net.minecraft.world.flag.FeatureFlagSet}.
         * @param output Items accepter, marks which {@link net.minecraft.world.item.Item} should append in registered {@link CreativeModeTab}.
         * @param sortedCreativeModeTabContents Content sorter, use it instead of given {@linkplain CreativeModeTab.Output output} for item sorting.
         * @param requiredFeature A {@link FeatureFlag} marking optional content which shouldn't be sorted nor showed once given {@link FeatureFlag} is disabled.
         */
        void accept(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output, SortedCreativeModeTabContents sortedCreativeModeTabContents, @Nullable FeatureFlag requiredFeature);
    }
}
