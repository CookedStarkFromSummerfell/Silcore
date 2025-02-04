package pl.kalishak.silcore.api.world.material.gas.crafting.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.impl.world.material.gas.crafting.ingredient.SilcoreGasIngredientTypes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * GasIngredient that matches if all child ingredients match
 */
public final class IntersectionGasIngredient extends GasIngredient {
    public static final MapCodec<IntersectionGasIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GasIngredient.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("children").forGetter(IntersectionGasIngredient::children)
    ).apply(instance, IntersectionGasIngredient::new));

    private final List<GasIngredient> children;

    public IntersectionGasIngredient(List<GasIngredient> children) {
        if (children.isEmpty()) {
            throw new IllegalArgumentException("Cannot create an IntersectionGasIngredient with no children, use GasIngredient.of() to create an empty ingredient");
        }

        this.children = children;
    }

    /**
     * Gets an intersection gas ingredient
     *
     * @param ingredients List of fluid ingredients to match
     * @return FluidIngredient that only matches if all the passed ingredients match
     */
    public static GasIngredient of(GasIngredient... ingredients) {
        if (ingredients.length == 0)
            throw new IllegalArgumentException("Cannot create an IntersectionGasIngredient with no children, use GasIngredient.of() to create an empty ingredient");
        if (ingredients.length == 1)
            return ingredients[0];

        return new IntersectionGasIngredient(Arrays.asList(ingredients));
    }

    @Override
    public boolean test(GasStack stack) {
        for (GasIngredient child : this.children) {
            if (!child.test(stack))
                return false;
        }
        return true;
    }

    @Override
    protected Stream<Holder<Gas>> generateGases() {
        return this.children.stream()
                .flatMap(child -> child.gases().stream())
                .filter(gas -> test(new GasStack(gas, 1.0F, 1000)));
    }

    @Override
    public boolean isSimple() {
        for (GasIngredient child : this.children) {
            if (!child.isSimple())
                return false;
        }

        return true;
    }

    @Override
    public GasIngredientType<IntersectionGasIngredient> getType() {
        return SilcoreGasIngredientTypes.INTERSECTION_GAS_INGREDIENT.get();
    }

    public List<GasIngredient> children() {
        return this.children;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.children);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof IntersectionGasIngredient other && other.children.equals(this.children);
    }
}
