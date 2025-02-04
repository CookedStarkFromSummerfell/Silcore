package pl.kalishak.silcore.api.world.material.fluid.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import pl.kalishak.silcore.api.world.material.fluid.crafting.FluidPlacementInfo;

public interface FluidRecipe<T extends FluidInput> extends Recipe<T> {
    @Override
    default ItemStack assemble(T input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    default PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    RecipeSerializer<? extends FluidRecipe<T>> getSerializer();

    @Override
    RecipeType<? extends FluidRecipe<T>> getType();

    FluidStack assembleFluid(T input, HolderLookup.Provider registries);

    FluidPlacementInfo fluidPlacementInfo();
}
