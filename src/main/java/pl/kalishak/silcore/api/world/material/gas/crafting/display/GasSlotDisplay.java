package pl.kalishak.silcore.api.world.material.gas.crafting.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;

import java.util.stream.Stream;

/**
 * Slot display for a single gas holder.
 * <p>
 * Note that information on amount and data of the displayed gas stack depends on the provided factory!
 *
 * @param gas The gas to be displayed.
 */
public record GasSlotDisplay(Holder<Gas> gas) implements SlotDisplay {
    public static final MapCodec<GasSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryFixedCodec.create(SilcoreRegistries.Keys.GAS).fieldOf("gas").forGetter(GasSlotDisplay::gas)
    ).apply(instance, GasSlotDisplay::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, GasSlotDisplay> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(SilcoreRegistries.Keys.GAS), GasSlotDisplay::gas,
            GasSlotDisplay::new
    );

    @Override
    public Type<GasSlotDisplay> type() {
        return SilcoreSlotDisplays.GAS_SLOT_DISPLAY.get();
    }

    @Override
    public <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> factory) {
        return switch (factory) {
            case ForGasStacks<T> gases -> Stream.of(gases.forStack(gas()));
            default -> Stream.empty();
        };
    }
}
