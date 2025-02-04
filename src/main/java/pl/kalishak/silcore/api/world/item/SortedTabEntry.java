package pl.kalishak.silcore.api.world.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Sorted entry for CreativeModeTab
 * @param holder Holder of item to sort
 * @param tabVisibility Item visibility
 * @param category Optional variable used in sorting by category
 */
public record SortedTabEntry(Holder<Item> holder, CreativeModeTab.TabVisibility tabVisibility, @Nullable SortedTabEntry.Category category) {
    public SortedTabEntry(Holder<Item> holder) {
        this(holder, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, null);
    }

    public Holder<Item> getHolder() {
        return this.holder;
    }

    public String getKey() {
        return this.holder.unwrapKey().orElseThrow().location().getPath();
    }

    public Optional<Category> getPriority() {
        return Optional.ofNullable(this.category);
    }

    public enum Category {
        BUILDING_BLOCKS(0),
        FUNCTIONAL_BLOCKS(1),
        DECORATIONS(2),
        REDSTONE(3),
        COMBAT(0),
        TOOLS(1),
        FOOD(2),
        POTIONS(3),
        TRANSPORTATION(4),
        MISC(5);

        private final int priority;

        Category(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return this.priority;
        }
    }
}
