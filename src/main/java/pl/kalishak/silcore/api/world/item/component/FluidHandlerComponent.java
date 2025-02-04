package pl.kalishak.silcore.api.world.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidHandlerComponent(NonNullList<FluidStack> fluids) {
    public static final Codec<FluidHandlerComponent> CODEC = NonNullList.codecOf(FluidStack.OPTIONAL_CODEC)
            .xmap(FluidHandlerComponent::new, FluidHandlerComponent::fluids);
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidHandlerComponent> STREAM_CODEC = ByteBufCodecs
            .collection(size -> NonNullList.withSize(size, FluidStack.EMPTY), FluidStack.STREAM_CODEC)
            .map(FluidHandlerComponent::new, FluidHandlerComponent::fluids);

    public static FluidHandlerComponent empty(int size) {
        return new FluidHandlerComponent(NonNullList.withSize(size, FluidStack.EMPTY));
    }
}
