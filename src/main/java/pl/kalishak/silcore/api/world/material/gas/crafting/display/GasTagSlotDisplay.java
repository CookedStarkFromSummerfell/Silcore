package pl.kalishak.silcore.api.world.material.gas.crafting.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;

import java.util.stream.Stream;

public record GasTagSlotDisplay(TagKey<Gas> tag) implements SlotDisplay {
    public static final MapCodec<GasTagSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.codec(SilcoreRegistries.Keys.GAS).fieldOf("tag").forGetter(GasTagSlotDisplay::tag)
    ).apply(instance, GasTagSlotDisplay::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, GasTagSlotDisplay> STREAM_CODEC = StreamCodec.composite(
            TagKey.streamCodec(SilcoreRegistries.Keys.GAS), GasTagSlotDisplay::tag,
            GasTagSlotDisplay::new
    );

    @Override
    public <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> factory) {
        if (factory instanceof ForGasStacks<T> gases) {
            HolderLookup.Provider registries = context.getOptional(SlotDisplayContext.REGISTRIES);

            if (registries != null) {
                return registries.lookupOrThrow(SilcoreRegistries.Keys.GAS)
                        .get(this.tag)
                        .map(gas -> gas.stream().map(gases::forStack))
                        .stream()
                        .flatMap($ -> $);
            }
        }

        return Stream.empty();
    }

    @Override
    public Type<? extends SlotDisplay> type() {
        return SilcoreSlotDisplays.GAS_TAG_SLOT_DISPLAY.get();
    }
}
