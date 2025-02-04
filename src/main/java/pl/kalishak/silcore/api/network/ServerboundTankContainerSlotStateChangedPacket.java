package pl.kalishak.silcore.api.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import pl.kalishak.silcore.SilcoreMod;

public record ServerboundTankContainerSlotStateChangedPacket(int slotId, int containerId, boolean newState) implements CustomPacketPayload {
    public static final Type<ServerboundTankContainerSlotStateChangedPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SilcoreMod.MOD_ID, "tank_container_slot_state_changed"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTankContainerSlotStateChangedPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundTankContainerSlotStateChangedPacket::slotId,
            ByteBufCodecs.CONTAINER_ID, ServerboundTankContainerSlotStateChangedPacket::containerId,
            ByteBufCodecs.BOOL, ServerboundTankContainerSlotStateChangedPacket::newState,
            ServerboundTankContainerSlotStateChangedPacket::new
    );

    @Override
    public Type<ServerboundTankContainerSlotStateChangedPacket> type() {
        return TYPE;
    }
}
