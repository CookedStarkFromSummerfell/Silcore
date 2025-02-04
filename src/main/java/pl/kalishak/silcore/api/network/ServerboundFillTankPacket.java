package pl.kalishak.silcore.api.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import pl.kalishak.silcore.SilcoreMod;

public record ServerboundFillTankPacket(int containerId, short slotId, ClickType clickType, ItemStack carried) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundFillTankPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SilcoreMod.MOD_ID, "fill_tank"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundFillTankPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.CONTAINER_ID, ServerboundFillTankPacket::containerId,
            ByteBufCodecs.SHORT, ServerboundFillTankPacket::slotId,
            SilcoreBufCodes.enumStreamCodec(ClickType.class), ServerboundFillTankPacket::clickType,
            ItemStack.STREAM_CODEC, ServerboundFillTankPacket::carried,
            ServerboundFillTankPacket::new
    );

    @Override
    public Type<ServerboundFillTankPacket> type() {
        return TYPE;
    }
}
