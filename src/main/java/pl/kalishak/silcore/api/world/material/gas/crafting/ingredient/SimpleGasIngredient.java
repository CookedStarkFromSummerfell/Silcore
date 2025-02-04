package pl.kalishak.silcore.api.world.material.gas.crafting.ingredient;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.api.world.material.gas.SilcoreGases;
import pl.kalishak.silcore.api.world.material.gas.crafting.display.GasTagSlotDisplay;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;
import pl.kalishak.silcore.impl.world.material.gas.crafting.ingredient.SilcoreGasIngredientTypes;

import java.util.stream.Stream;

/**
 * Gas ingredient that matches the gases specified by the given {@link HolderSet}.
 * Most commonly, this will either be a list of gases or a gas tag.
 * <p>
 * Unlike with ingredients, this is technically an explicit "type" of gas ingredient,
 * though in JSON, it is still written <b>without</b> a type field, see {@link GasIngredientCodecs#codec()}
 */
public class SimpleGasIngredient extends GasIngredient {
    private static final Codec<HolderSet<Gas>> HOLDER_SET_NO_EMPTY_GAS = HolderSetCodec.create(
            SilcoreRegistries.Keys.GAS, GasStack.NON_EMPTY_GAS_CODEC, false
    );
    static final Codec<SimpleGasIngredient> CODEC = ExtraCodecs.nonEmptyHolderSet(HOLDER_SET_NO_EMPTY_GAS)
            .xmap(SimpleGasIngredient::new, SimpleGasIngredient::gasSet);
    static final StreamCodec<RegistryFriendlyByteBuf, SimpleGasIngredient> CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(SilcoreRegistries.Keys.GAS)
            .map(SimpleGasIngredient::new, SimpleGasIngredient::gasSet);

    private final HolderSet<Gas> values;

    public SimpleGasIngredient(HolderSet<Gas> values) {
        values.unwrap().ifRight(list -> {
            if (list.isEmpty()) {
                throw new UnsupportedOperationException("Gas ingredients can't be empty!");
            } else if (list.contains(SilcoreGases.EMPTY)) {
                throw new UnsupportedOperationException("Gas ingredients can't contain the empty gas.");
            }
        });

        this.values = values;
    }

    @Override
    public boolean test(GasStack gasStack) {
        return this.values.contains(gasStack.getGasHolder());
    }

    @Override
    protected Stream<Holder<Gas>> generateGases() {
        return this.values.stream();
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public GasIngredientType<?> getType() {
        return SilcoreGasIngredientTypes.SIMPLE_GAS_INGREDIENT_TYPE.get();
    }

    @Override
    public SlotDisplay display() {
        return this.values.unwrapKey()
                .<SlotDisplay>map(GasTagSlotDisplay::new)
                .orElseGet(super::display);
    }

    @Override
    public int hashCode() {
        return this.values.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        return obj instanceof SimpleGasIngredient other && other.values.equals(this.values);
    }

    public HolderSet<Gas> gasSet() {
        return this.values;
    }
}
