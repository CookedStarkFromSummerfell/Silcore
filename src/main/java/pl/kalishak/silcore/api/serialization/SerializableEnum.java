package pl.kalishak.silcore.api.serialization;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public interface SerializableEnum extends StringRepresentable {
    int id();

    static <E extends SerializableEnum> Codec<E> codec(Class<E> clazz) {
        if (!clazz.isEnum()) {
            throw new IllegalArgumentException("Not an enum: " + clazz);
        }

        return StringRepresentable.fromValues(clazz::getEnumConstants);
    }

    static <E extends SerializableEnum> StreamCodec<ByteBuf, E> streamCodec(Class<E> clazz) {
        if (!clazz.isEnum()) {
            throw new IllegalArgumentException("Not an enum: " + clazz);
        }

        IntFunction<E> byId = ByIdMap.continuous(E::id, clazz.getEnumConstants(), ByIdMap.OutOfBoundsStrategy.ZERO);
        return ByteBufCodecs.idMapper(byId, E::id);
    }
}
