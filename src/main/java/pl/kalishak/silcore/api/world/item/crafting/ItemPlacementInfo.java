package pl.kalishak.silcore.api.world.item.crafting;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import pl.kalishak.silcore.api.crafting.CustomPlacementInfo;

import java.util.List;

public class ItemPlacementInfo extends PlacementInfo implements CustomPlacementInfo<ItemStack, Ingredient> {
    public static final ItemPlacementInfo EMPTY = new ItemPlacementInfo(List.of(), IntList.of());

    private ItemPlacementInfo(List<Ingredient> ingredients, IntList slotToIngredientIndex) {
        super(ingredients, slotToIngredientIndex);
    }

    public static ItemPlacementInfo createCustom(Ingredient ingredient) {
        return ingredient.isEmpty() ? EMPTY : new ItemPlacementInfo(List.of(ingredient), IntList.of(0));
    }

    @Override
    public IntList idToIngredientIndex() {
        return slotsToIngredientIndex();
    }
}
