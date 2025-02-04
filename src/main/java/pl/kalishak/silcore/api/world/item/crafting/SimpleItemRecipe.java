package pl.kalishak.silcore.api.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import pl.kalishak.silcore.api.crafting.CustomPlacementInfo;
import pl.kalishak.silcore.api.world.item.crafting.recipe.ItemRecipeWrapper;

public abstract class SimpleItemRecipe<R extends ItemRecipeWrapper> implements Recipe<R> {
    private final String group;
    private final ItemStack result;
    private @Nullable CustomPlacementInfo<?, ?> placementInfo;

    protected SimpleItemRecipe(String group, ItemStack result) {
        this.group = group;
        this.result = result;
    }

    @Override
    public abstract RecipeSerializer<? extends SimpleItemRecipe<R>> getSerializer();

    @Override
    public abstract RecipeType<? extends SimpleItemRecipe<R>> getType();

    @Override
    public String group() {
        return this.group;
    }

    protected ItemStack result() {
        return this.result;
    }
}
