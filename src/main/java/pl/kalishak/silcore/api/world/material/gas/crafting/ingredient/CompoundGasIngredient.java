package pl.kalishak.silcore.api.world.material.gas.crafting.ingredient;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.fluids.crafting.CompoundFluidIngredient;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.impl.world.material.gas.crafting.ingredient.SilcoreGasIngredientTypes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Gas ingredient that matches if any of the child ingredients match.
 * This type additionally represents the array notation used in
 * {@linkplain GasIngredient#CODEC} internally.
 *
 * @see CompoundIngredient CompoundIngredient, its item equivalent
 * @see CompoundFluidIngredient CompoundFluidIngredient, its fluid equivalent
 */
public final class CompoundGasIngredient extends GasIngredient {
    public static final MapCodec<CompoundGasIngredient> CODEC = NeoForgeExtraCodecs.aliasedFieldOf(GasIngredient.CODEC.listOf(1, Integer.MAX_VALUE), "children", "ingredients")
            .xmap(CompoundGasIngredient::new, CompoundGasIngredient::children);

    private final List<GasIngredient> children;

    public CompoundGasIngredient(List<? extends GasIngredient> children) {
        if (children.isEmpty()) {
            throw new IllegalArgumentException("Compound gas ingredient must have at least one child");
        }
        this.children = List.copyOf(children);
    }

    public static GasIngredient of(GasIngredient... children) {
        if (children.length == 1) {
            return children[0];
        }

        return new CompoundGasIngredient(List.of(children));
    }

    public static GasIngredient of(List<GasIngredient> children) {
        if (children.size() == 1) {
            return children.getFirst();
        }

        return new CompoundGasIngredient(children);
    }

    @Override
    protected Stream<Holder<Gas>> generateGases() {
        return this.children.stream().flatMap(GasIngredient::generateGases);
    }

    @Override
    public boolean test(GasStack gasStack) {
        for (GasIngredient child : this.children) {
            if (child.test(gasStack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isSimple() {
        for (GasIngredient child : this.children) {
            if (!child.isSimple()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public GasIngredientType<CompoundGasIngredient> getType() {
        return SilcoreGasIngredientTypes.COMPOUND_GAS_INGREDIENT_TYPE.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.children);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        return obj instanceof CompoundGasIngredient other && other.children.equals(this.children);
    }

    public List<GasIngredient> children() {
        return this.children;
    }
}
