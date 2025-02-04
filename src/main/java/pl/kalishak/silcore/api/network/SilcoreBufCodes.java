package pl.kalishak.silcore.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SilcoreBufCodes {
    public static <E extends Enum<E>> StreamCodec<RegistryFriendlyByteBuf, E> enumStreamCodec(Class<E> clazz) {
        return StreamCodec.of(FriendlyByteBuf::writeEnum, buffer -> buffer.readEnum(clazz));
    }
}
