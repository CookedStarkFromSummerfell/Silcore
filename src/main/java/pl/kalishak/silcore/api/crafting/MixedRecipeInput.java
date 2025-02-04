package pl.kalishak.silcore.api.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;
import pl.kalishak.silcore.api.world.material.fluid.crafting.recipe.FluidInput;
import pl.kalishak.silcore.api.world.material.fluid.crafting.recipe.FluidRecipeWrapper;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.api.world.material.gas.crafting.recipe.GasInput;
import pl.kalishak.silcore.api.world.material.gas.crafting.recipe.GasRecipeWrapper;

import java.util.Optional;

/**
 * A multiple inputs of different types for a recipe.
 * @see RecipeInput
 * @see FluidInput
 * @see GasInput
 */
public class MixedRecipeInput implements RecipeInput, FluidInput, GasInput {
    private final @Nullable RecipeWrapper itemWrapper;
    private final @Nullable FluidRecipeWrapper fluidWrapper;
    private final @Nullable GasRecipeWrapper gasWrapper;

    public MixedRecipeInput(@Nullable RecipeWrapper itemWrapper, @Nullable FluidRecipeWrapper fluidWrapper, @Nullable GasRecipeWrapper gasWrapper) {
        this.itemWrapper = itemWrapper;
        this.fluidWrapper = fluidWrapper;
        this.gasWrapper = gasWrapper;
    }

    public static MixedRecipeInput itemFluid(RecipeWrapper itemWrapper, FluidRecipeWrapper fluidWrapper) {
        return new MixedRecipeInput(itemWrapper, fluidWrapper, null);
    }

    public static MixedRecipeInput itemGas(RecipeWrapper itemWrapper, GasRecipeWrapper gasWrapper) {
        return new MixedRecipeInput(itemWrapper, null, gasWrapper);
    }

    public static MixedRecipeInput fluidGas(FluidRecipeWrapper fluidWrapper, GasRecipeWrapper gasWrapper) {
        return new MixedRecipeInput(null, fluidWrapper, gasWrapper);
    }

    public Optional<ItemStack> getItemStack(int index) {
        return Optional.ofNullable(this.itemWrapper).map(inventory -> inventory.getItem(index));
    }

    public Optional<FluidStack> getFluidStack(int index) {
        return Optional.ofNullable(this.fluidWrapper).map(tanks -> tanks.getFluid(index));
    }

    public Optional<GasStack> getGasStack(int index) {
        return Optional.ofNullable(this.gasWrapper).map(tank -> tank.getGas(index));
    }

    @Override
    public ItemStack getItem(int index) {
        return getItemStack(index).orElse(ItemStack.EMPTY);
    }

    @Override
    public FluidStack getFluid(int index) {
        return getFluidStack(index).orElse(FluidStack.EMPTY);
    }

    @Override
    public GasStack getGas(int index) {
        return getGasStack(index).orElse(GasStack.EMPTY);
    }

    @Override
    public int size() {
        int i = 0;

        if (this.itemWrapper != null) {
            i += this.itemWrapper.size();
        }

        if (this.fluidWrapper != null) {
            i += this.fluidWrapper.size();
        }

        if (this.gasWrapper != null) {
            i += this.gasWrapper.size();
        }

        return i;
    }

    @Override
    public boolean isEmpty() {
        if (this.itemWrapper == null && this.fluidWrapper == null && this.gasWrapper == null) {
            return true;
        }

        if (this.itemWrapper != null && !this.itemWrapper.isEmpty()) {
            return false;
        } else if (this.fluidWrapper != null && !this.fluidWrapper.isEmpty()) {
            return false;
        } else return this.gasWrapper == null || this.gasWrapper.isEmpty();
    }
}
