package pl.kalishak.silcore.api.crafting;

import it.unimi.dsi.fastutil.ints.IntList;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

import java.util.List;
import java.util.function.Predicate;

public interface CustomPlacementInfo<S extends MutableDataComponentHolder, I extends Predicate<S>> {
    List<I> ingredients();
    IntList idToIngredientIndex();

    default boolean isImpossibleToFill() {
        return idToIngredientIndex().isEmpty();
    }
}
