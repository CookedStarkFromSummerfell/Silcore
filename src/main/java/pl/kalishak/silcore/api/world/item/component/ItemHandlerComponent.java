package pl.kalishak.silcore.api.world.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ItemHandlerComponent(NonNullList<ItemStack> items) {
    public static final Codec<ItemHandlerComponent> CODEC = NonNullList.codecOf(ItemStack.OPTIONAL_CODEC)
            .xmap(ItemHandlerComponent::new, ItemHandlerComponent::items);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemHandlerComponent> STREAM_CODEC = ByteBufCodecs
            .collection(size -> NonNullList.withSize(size, ItemStack.EMPTY), ItemStack.STREAM_CODEC)
            .map(ItemHandlerComponent::new, ItemHandlerComponent::items);

    public static ItemHandlerComponent empty(int size) {
        return new ItemHandlerComponent(NonNullList.withSize(size, ItemStack.EMPTY));
    }
}
