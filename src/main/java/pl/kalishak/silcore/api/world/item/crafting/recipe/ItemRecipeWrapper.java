package pl.kalishak.silcore.api.world.item.crafting.recipe;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import pl.kalishak.silcore.api.crafting.CustomRecipeInput;

/**
 * Wrapper for custom recipes
 */
public class ItemRecipeWrapper extends RecipeWrapper implements CustomRecipeInput {
    public ItemRecipeWrapper(IItemHandler inv) {
        super(inv);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }
}
