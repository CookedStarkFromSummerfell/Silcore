package pl.kalishak.silcore.api.world.material.gas.crafting.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.api.world.material.gas.crafting.display.GasStackSlotDisplay;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;
import pl.kalishak.silcore.impl.world.material.gas.crafting.ingredient.SilcoreGasIngredientTypes;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Gas ingredient that matches the given set of gases, additionally performing either a
 * {@link DataComponentGasIngredient#isStrict() strict} or partial test on the GasStack's components.
 * <p>
 * Strict ingredients will only match gas stacks that have <b>exactly</b> the provided components, while partial ones will
 * match if the stack's components contain all required components for the {@linkplain #components input predicate}.
 *
 * @see DataComponentIngredient DataComponentIngredient, its item equivalent
 * @see DataComponentGasIngredient DataComponentGasIngredient, its gas equivalent
 */
public class DataComponentGasIngredient extends GasIngredient {
    public static final MapCodec<DataComponentGasIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HolderSetCodec.create(SilcoreRegistries.Keys.GAS, SilcoreRegistries.GAS.holderByNameCodec(), false).fieldOf("gases").forGetter(DataComponentGasIngredient::gasSet),
            DataComponentPredicate.CODEC.fieldOf("components").forGetter(DataComponentGasIngredient::components),
            Codec.BOOL.optionalFieldOf("strict", false).forGetter(DataComponentGasIngredient::isStrict)
    ).apply(instance, DataComponentGasIngredient::new));
    
    private final HolderSet<Gas> gases;
    private final DataComponentPredicate components;
    private final boolean strict;
    private final GasStack[] stacks;
    
    public DataComponentGasIngredient(HolderSet<Gas> gases, DataComponentPredicate components, boolean strict) {
        this.gases = gases;
        this.components = components;
        this.strict = strict;
        this.stacks = gases.stream()
                .map(gas -> new GasStack(gas, 1.0F, 1000, components.asPatch()))
                .toArray(GasStack[]::new);
    }

    @Override
    public boolean test(GasStack gasStack) {
        if (this.strict) {
            for (GasStack stack : this.stacks) {
                if (GasStack.isSameGasComponents(gasStack, stack)) return true;
            }
            
            return false;
        }
        
        return this.gases.contains(gasStack.getGasHolder()) && this.components.test(gasStack);
    }

    @Override
    protected Stream<Holder<Gas>> generateGases() {
        return this.gases.stream();
    }

    @Override
    public SlotDisplay display() {
        return new SlotDisplay.Composite(Stream.of(this.stacks)
                .map(stack -> (SlotDisplay) new GasStackSlotDisplay(stack))
                .toList()
        );
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public GasIngredientType<DataComponentGasIngredient> getType() {
        return SilcoreGasIngredientTypes.DATA_COMPONENT_GAS_INGREDIENT.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.gases, this.components, this.strict);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DataComponentGasIngredient other)) return false;
        
        return other.gases.equals(this.gases) && other.components.equals(this.components) && other.strict == this.strict;
    }

    public HolderSet<Gas> gasSet() {
        return this.gases;
    }

    public DataComponentPredicate components() {
        return this.components;
    }

    public boolean isStrict() {
        return this.strict;
    }

    /**
     * Creates a new ingredient matching the given gas, containing the given components
     */
    public static GasIngredient of(boolean strict, GasStack stack) {
        return of(strict, stack.getComponents(), stack.getGas());
    }

    /**
     * Creates a new ingredient matching any gas from the list, containing the given components
     */
    public static <T> GasIngredient of(boolean strict, DataComponentType<? super T> type, T value, Gas... gases) {
        return of(strict, DataComponentPredicate.builder().expect(type, value).build(), gases);
    }

    /**
     * Creates a new ingredient matching any gas from the list, containing the given components
     */
    public static <T> GasIngredient of(boolean strict, Supplier<? extends DataComponentType<? super T>> type, T value, Gas... gases) {
        return of(strict, type.get(), value, gases);
    }

    /**
     * Creates a new ingredient matching any gas from the list, containing the given components
     */
    public static GasIngredient of(boolean strict, DataComponentMap map, Gas... gases) {
        return of(strict, DataComponentPredicate.allOf(map), gases);
    }

    /**
     * Creates a new ingredient matching any gas from the list, containing the given components
     */
    @SafeVarargs
    public static GasIngredient of(boolean strict, DataComponentMap map, Holder<Gas>... gases) {
        return of(strict, DataComponentPredicate.allOf(map), gases);
    }

    /**
     * Creates a new ingredient matching any gas from the list, containing the given components
     */
    public static GasIngredient of(boolean strict, DataComponentMap map, HolderSet<Gas> gases) {
        return of(strict, DataComponentPredicate.allOf(map), gases);
    }

    /**
     * Creates a new ingredient matching any gas from the list, containing the given components
     */
    @SafeVarargs
    public static GasIngredient of(boolean strict, DataComponentPredicate predicate, Holder<Gas>... gases) {
        return of(strict, predicate, HolderSet.direct(gases));
    }

    /**
     * Creates a new ingredient matching any gas from the list, containing the given components
     */
    public static GasIngredient of(boolean strict, DataComponentPredicate predicate, Gas... gases) {
        return of(strict, predicate, HolderSet.direct(Arrays.stream(gases).map(Gas::builtInRegistryHolder).toList()));
    }

    /**
     * Creates a new ingredient matching any gas from the list, containing the given components
     */
    public static GasIngredient of(boolean strict, DataComponentPredicate predicate, HolderSet<Gas> gases) {
        return new DataComponentGasIngredient(gases, predicate, strict);
    }
}
