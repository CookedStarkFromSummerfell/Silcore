package pl.kalishak.silcore.api.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import pl.kalishak.silcore.SilcoreMod;

public record ServerboundDrainTankPacket(int containerId, short slotId, ItemStack carried) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundDrainTankPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SilcoreMod.MOD_ID, "fill_tank"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundDrainTankPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.CONTAINER_ID, ServerboundDrainTankPacket::containerId,
            ByteBufCodecs.SHORT, ServerboundDrainTankPacket::slotId,
            ItemStack.STREAM_CODEC, ServerboundDrainTankPacket::carried,
            ServerboundDrainTankPacket::new
    );

    @Override
    public CustomPacketPayload.Type<ServerboundDrainTankPacket> type() {
        return TYPE;
    }
}
