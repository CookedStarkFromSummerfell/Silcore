package pl.kalishak.silcore.api.world.item.crafting;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;

import java.util.function.IntFunction;

public interface RecipeBookCategory extends ExtendedRecipeBookCategory, StringRepresentable {
    int id();

    static <R extends RecipeBookCategory> Codec<R> codec(Class<R> clazz) {
        if (!clazz.isEnum()) {
            throw new IllegalArgumentException("Cannot create Codec for " + clazz + " because it is not an enum!");
        }

        return StringRepresentable.fromValues(clazz::getEnumConstants);
    }

    static <R extends RecipeBookCategory> StreamCodec<ByteBuf, R> streamCodec(Class<R> clazz) {
        if (!clazz.isEnum()) {
            throw new IllegalArgumentException("Cannot create StreamCodec for " + clazz + " because it is not an enum!");
        }

        IntFunction<R> map = ByIdMap.continuous(R::id, clazz.getEnumConstants(), ByIdMap.OutOfBoundsStrategy.ZERO);
        return ByteBufCodecs.idMapper(map, R::id);
    }
}
