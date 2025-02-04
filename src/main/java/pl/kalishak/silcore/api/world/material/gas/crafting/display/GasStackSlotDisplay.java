package pl.kalishak.silcore.api.world.material.gas.crafting.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import pl.kalishak.silcore.api.world.material.gas.GasStack;

import java.util.stream.Stream;

public record GasStackSlotDisplay(GasStack stack) implements SlotDisplay {
    public static final MapCodec<GasStackSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GasStack.CODEC.fieldOf("gas").forGetter(GasStackSlotDisplay::stack)
    ).apply(instance, GasStackSlotDisplay::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, GasStackSlotDisplay> STREAM_CODEC = StreamCodec.composite(
            GasStack.STREAM_CODEC, GasStackSlotDisplay::stack,
            GasStackSlotDisplay::new
    );

    @Override
    public Type<GasStackSlotDisplay> type() {
        return SilcoreSlotDisplays.GAS_STACK_SLOT_DISPLAY.get();
    }

    @Override
    public <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> factory) {
        return switch (factory) {
            case ForGasStacks<T> gases -> Stream.of(gases.forStack(this.stack));
            default -> Stream.empty();
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        return obj instanceof GasStackSlotDisplay gasStackSlotDisplay && GasStack.matches(this.stack, gasStackSlotDisplay.stack);
    }
}
