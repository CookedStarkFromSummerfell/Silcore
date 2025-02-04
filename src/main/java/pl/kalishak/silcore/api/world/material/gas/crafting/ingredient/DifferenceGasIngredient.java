package pl.kalishak.silcore.api.world.material.gas.crafting.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.fluids.crafting.DifferenceFluidIngredient;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.impl.world.material.gas.crafting.ingredient.SilcoreGasIngredientTypes;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Gas ingredient that matches the difference of two provided gas ingredients, i.e.
 * anything contained in {@code base} that is not in {@code subtracted}.
 *
 * @see DifferenceIngredient DifferenceIngredient, its item equivalent
 * @see DifferenceFluidIngredient DifferenceFluidIngredient, its fluid equivalent
 */
public final class DifferenceGasIngredient extends GasIngredient {
    public static final MapCodec<DifferenceGasIngredient> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(
                            GasIngredient.CODEC.fieldOf("base").forGetter(DifferenceGasIngredient::base),
                            GasIngredient.CODEC.fieldOf("subtracted").forGetter(DifferenceGasIngredient::subtracted))
                    .apply(builder, DifferenceGasIngredient::new));
    private final GasIngredient base;
    private final GasIngredient subtracted;

    public DifferenceGasIngredient(GasIngredient base, GasIngredient subtracted) {
        this.base = base;
        this.subtracted = subtracted;
    }

    @Override
    protected Stream<Holder<Gas>> generateGases() {
        return this.base.gases().stream().filter(e -> !subtracted.gases().contains(e));
    }

    @Override
    public boolean test(GasStack gasStack) {
        return this.base.test(gasStack) && !this.subtracted.test(gasStack);
    }

    @Override
    public boolean isSimple() {
        return this.base.isSimple() && this.subtracted.isSimple();
    }

    @Override
    public GasIngredientType<DifferenceGasIngredient> getType() {
        return SilcoreGasIngredientTypes.DIFFERENCE_GAS_INGREDIENT.get();
    }

    public GasIngredient base() {
        return this.base;
    }

    public GasIngredient subtracted() {
        return this.subtracted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.base, this.subtracted);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof DifferenceGasIngredient other &&
                other.base.equals(this.base) && other.subtracted.equals(this.subtracted);
    }

    public static GasIngredient of(GasIngredient base, GasIngredient subtracted) {
        return new DifferenceGasIngredient(base, subtracted);
    }
}
