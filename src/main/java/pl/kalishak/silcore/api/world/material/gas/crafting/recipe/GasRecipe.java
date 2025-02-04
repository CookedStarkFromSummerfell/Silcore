package pl.kalishak.silcore.api.world.material.gas.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.api.world.material.gas.crafting.GasPlacementInfo;

public interface GasRecipe<T extends GasInput> extends Recipe<T> {
    @Override
    default ItemStack assemble(T input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    default PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    RecipeSerializer<? extends GasRecipe<T>> getSerializer();

    @Override
    RecipeType<? extends GasRecipe<T>> getType();

    GasStack assembleGas(T input, HolderLookup.Provider registries);

    GasPlacementInfo gasPlacementInfo();
}
