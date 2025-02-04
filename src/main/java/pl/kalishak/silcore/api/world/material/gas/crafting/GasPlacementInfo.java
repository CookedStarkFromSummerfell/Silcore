package pl.kalishak.silcore.api.world.material.gas.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import pl.kalishak.silcore.api.crafting.CustomPlacementInfo;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.api.world.material.gas.crafting.ingredient.GasIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A {@linkplain net.minecraft.world.item.crafting.PlacementInfo PlacementInfo} implementation for {@link GasIngredient}
 */
public record GasPlacementInfo(List<GasIngredient> ingredients, IntList idToIngredientIndex) implements CustomPlacementInfo<GasStack, GasIngredient> {
    public static final int EMPTY_TANK = -1;
    public static final GasPlacementInfo NOT_FILLABLE = new GasPlacementInfo(List.of(), IntList.of());

    public static GasPlacementInfo create(GasIngredient ingredient) {
        return ingredient.gases().isEmpty() ? NOT_FILLABLE : new GasPlacementInfo(List.of(ingredient), IntList.of());
    }

    public static GasPlacementInfo createOptional(List<Optional<GasIngredient>> optionalIngredients) {
        int size = optionalIngredients.size();
        List<GasIngredient> ingredients = new ArrayList<>(size);
        IntList tanksToIngredientIndex = new IntArrayList(size);

        int index = 0;

        for (var optional : optionalIngredients) {
            if (optional.isPresent()) {
                GasIngredient ingredient = optional.get();
                if (ingredient.gases().isEmpty()) {
                    return NOT_FILLABLE;
                }

                ingredients.add(ingredient);
                tanksToIngredientIndex.add(index++);
            } else {
                tanksToIngredientIndex.add(EMPTY_TANK);
            }
        }

        return new GasPlacementInfo(ingredients, tanksToIngredientIndex);
    }

    public static GasPlacementInfo create(List<GasIngredient> ingredients) {
        int i = ingredients.size();
        IntList intlist = new IntArrayList(i);

        for (int j = 0; j < i; j++) {
            GasIngredient ingredient = ingredients.get(j);
            if (ingredient.gases().isEmpty()) {
                return NOT_FILLABLE;
            }

            intlist.add(j);
        }

        return new GasPlacementInfo(ingredients, intlist);
    }
}
