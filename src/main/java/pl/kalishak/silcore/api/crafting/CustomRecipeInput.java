package pl.kalishak.silcore.api.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public interface CustomRecipeInput extends RecipeInput {
    @Override
    default ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }
}