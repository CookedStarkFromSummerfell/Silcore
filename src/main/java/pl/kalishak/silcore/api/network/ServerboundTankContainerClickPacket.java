package pl.kalishak.silcore.api.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import pl.kalishak.silcore.SilcoreMod;

public record ServerboundTankContainerClickPacket(int containerId, int stateId, short slotId, byte mouseButton, ClickType clickType, ItemStack carried) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundTankContainerClickPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SilcoreMod.MOD_ID, "tank_container_click"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTankContainerClickPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.CONTAINER_ID, ServerboundTankContainerClickPacket::containerId,
            ByteBufCodecs.VAR_INT, ServerboundTankContainerClickPacket::stateId,
            ByteBufCodecs.SHORT, ServerboundTankContainerClickPacket::slotId,
            ByteBufCodecs.BYTE, ServerboundTankContainerClickPacket::mouseButton,
            SilcoreBufCodes.enumStreamCodec(ClickType.class), ServerboundTankContainerClickPacket::clickType,
            ItemStack.STREAM_CODEC, ServerboundTankContainerClickPacket::carried,
            ServerboundTankContainerClickPacket::new
    );

    @Override
    public Type<ServerboundTankContainerClickPacket> type() {
        return TYPE;
    }
}
