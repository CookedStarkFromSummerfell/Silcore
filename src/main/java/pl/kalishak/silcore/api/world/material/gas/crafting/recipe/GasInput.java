package pl.kalishak.silcore.api.world.material.gas.crafting.recipe;

import pl.kalishak.silcore.api.crafting.CustomRecipeInput;
import pl.kalishak.silcore.api.world.material.gas.GasStack;

public interface GasInput extends CustomRecipeInput {
    GasStack getGas(int tank);

    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            if (!getGas(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
