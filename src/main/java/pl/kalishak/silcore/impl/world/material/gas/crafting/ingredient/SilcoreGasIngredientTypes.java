package pl.kalishak.silcore.impl.world.material.gas.crafting.ingredient;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import pl.kalishak.silcore.SilcoreMod;
import pl.kalishak.silcore.api.registry.DeferredIngredientTypeRegister;
import pl.kalishak.silcore.api.world.material.gas.crafting.ingredient.*;

public final class SilcoreGasIngredientTypes {
    private static final DeferredIngredientTypeRegister.Gas GAS_INGREDIENT_TYPES = DeferredIngredientTypeRegister.createGasIngredient(SilcoreMod.MOD_ID);

    public static final DeferredHolder<GasIngredientType<?>, GasIngredientType<SimpleGasIngredient>> SIMPLE_GAS_INGREDIENT_TYPE = GAS_INGREDIENT_TYPES.register(
            "simple", GasIngredientCodecs::simpleType
    );
    public static final DeferredHolder<GasIngredientType<?>, GasIngredientType<CompoundGasIngredient>> COMPOUND_GAS_INGREDIENT_TYPE = GAS_INGREDIENT_TYPES.registerGasIngredientType(
            "compound", CompoundGasIngredient.CODEC
    );
    public static final DeferredHolder<GasIngredientType<?>, GasIngredientType<DataComponentGasIngredient>> DATA_COMPONENT_GAS_INGREDIENT = GAS_INGREDIENT_TYPES.registerGasIngredientType(
            "data_component", DataComponentGasIngredient.CODEC
    );
    public static final DeferredHolder<GasIngredientType<?>, GasIngredientType<DifferenceGasIngredient>> DIFFERENCE_GAS_INGREDIENT = GAS_INGREDIENT_TYPES.registerGasIngredientType(
            "difference", DifferenceGasIngredient.CODEC
    );
    public static final DeferredHolder<GasIngredientType<?>, GasIngredientType<IntersectionGasIngredient>> INTERSECTION_GAS_INGREDIENT = GAS_INGREDIENT_TYPES.registerGasIngredientType(
            "intersection", IntersectionGasIngredient.CODEC
    );
    public static final DeferredHolder<GasIngredientType<?>, GasIngredientType<CustomDisplayGasIngredient>> CUSTOM_DISPLAY_GAS_INGREDIENT = GAS_INGREDIENT_TYPES.registerGasIngredientType(
            "custom_display", CustomDisplayGasIngredient.CODEC, CustomDisplayGasIngredient.STREAM_CODEC
    );

    public static void init(IEventBus bus) {
        GAS_INGREDIENT_TYPES.register(bus);
    }
}
