package pl.kalishak.silcore.api.world.material.fluid.crafting.recipe;

import net.neoforged.neoforge.fluids.FluidStack;
import pl.kalishak.silcore.api.crafting.CustomRecipeInput;

public interface FluidInput extends CustomRecipeInput {
    FluidStack getFluid(int tank);

    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            if (!getFluid(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
