package pl.kalishak.silcore.api.crafting;

import net.minecraft.world.item.crafting.Recipe;

public interface MachineRecipe<R extends MixedRecipeInput> extends Recipe<R> {
    int energyCost();

    int processTime();

    default float experience() {
        return 0.0F;
    }
}
