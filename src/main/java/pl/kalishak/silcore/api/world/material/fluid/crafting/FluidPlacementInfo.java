package pl.kalishak.silcore.api.world.material.fluid.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import pl.kalishak.silcore.api.crafting.CustomPlacementInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A {@linkplain net.minecraft.world.item.crafting.PlacementInfo PlacementInfo} implementation for {@link FluidIngredient}
 */
public record FluidPlacementInfo(List<FluidIngredient> ingredients, IntList idToIngredientIndex) implements CustomPlacementInfo<FluidStack, FluidIngredient> {
    public static final int EMPTY_TANK = -1;
    public static final FluidPlacementInfo NOT_FILLABLE = new FluidPlacementInfo(List.of(), IntList.of());

    public static FluidPlacementInfo create(FluidIngredient ingredient) {
        return ingredient.fluids().isEmpty() ? NOT_FILLABLE : new FluidPlacementInfo(List.of(ingredient), IntList.of());
    }

    public static FluidPlacementInfo createOptional(List<Optional<FluidIngredient>> optionalIngredients) {
        int size = optionalIngredients.size();
        List<FluidIngredient> ingredients = new ArrayList<>(size);
        IntList tanksToIngredientIndex = new IntArrayList(size);

        int index = 0;

        for (var optional : optionalIngredients) {
            if (optional.isPresent()) {
                FluidIngredient ingredient = optional.get();
                if (ingredient.fluids().isEmpty()) {
                    return NOT_FILLABLE;
                }

                ingredients.add(ingredient);
                tanksToIngredientIndex.add(index++);
            } else {
                tanksToIngredientIndex.add(EMPTY_TANK);
            }
        }

        return new FluidPlacementInfo(ingredients, tanksToIngredientIndex);
    }

    public static FluidPlacementInfo create(List<FluidIngredient> ingredients) {
        int i = ingredients.size();
        IntList intlist = new IntArrayList(i);

        for (int j = 0; j < i; j++) {
            FluidIngredient ingredient = ingredients.get(j);
            if (ingredient.fluids().isEmpty()) {
                return NOT_FILLABLE;
            }

            intlist.add(j);
        }

        return new FluidPlacementInfo(ingredients, intlist);
    }
}
