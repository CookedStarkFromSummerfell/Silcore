package pl.kalishak.silcore.api.world.material.gas.crafting.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.impl.world.material.gas.crafting.ingredient.SilcoreGasIngredientTypes;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * GasIngredient that wraps another gas ingredient to override its {@link SlotDisplay}.
 */
public final class CustomDisplayGasIngredient extends GasIngredient {
    public static final MapCodec<CustomDisplayGasIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GasIngredient.CODEC.fieldOf("base").forGetter(CustomDisplayGasIngredient::base),
            SlotDisplay.CODEC.fieldOf("display").forGetter(CustomDisplayGasIngredient::display)
    ).apply(instance, CustomDisplayGasIngredient::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, CustomDisplayGasIngredient> STREAM_CODEC = StreamCodec.composite(
            GasIngredient.STREAM_CODEC, CustomDisplayGasIngredient::base,
            SlotDisplay.STREAM_CODEC, CustomDisplayGasIngredient::display,
            CustomDisplayGasIngredient::new
    );

    private final GasIngredient base;
    private final SlotDisplay display;

    public CustomDisplayGasIngredient(GasIngredient base, SlotDisplay display) {
        this.base = base;
        this.display = display;
    }

    public static CustomDisplayGasIngredient of(GasIngredient base, SlotDisplay display) {
        return new CustomDisplayGasIngredient(base, display);
    }

    @Override
    public boolean test(GasStack gasStack) {
        return this.base.test(gasStack);
    }

    @Override
    protected Stream<Holder<Gas>> generateGases() {
        return this.base.generateGases();
    }

    @Override
    public boolean isSimple() {
        return this.base.isSimple();
    }

    @Override
    public GasIngredientType<?> getType() {
        return SilcoreGasIngredientTypes.CUSTOM_DISPLAY_GAS_INGREDIENT.get();
    }

    public GasIngredient base() {
        return this.base;
    }

    @Override
    public SlotDisplay display() {
        return this.display;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.base, this.display);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CustomDisplayGasIngredient other && Objects.equals(this.base, other.base) && Objects.equals(this.display, other.display);
    }

    @Override
    public String toString() {
        return "CustomDisplayGasIngredient[base=" + this.base + ", display=" + this.display + ']';
    }
}
