package pl.kalishak.silcore.api.world.material.gas.crafting.ingredient;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.ApiStatus;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;

import java.util.stream.Stream;

@ApiStatus.Internal
public class GasIngredientCodecs {
    static Codec<GasIngredient> codec() {
        return Codec.xor(SilcoreRegistries.GAS_INGREDIENT_TYPES.byNameCodec().<GasIngredient>dispatch("silcore:gas_ingredient_type", GasIngredient::getType, GasIngredientType::codec),
                Codec.lazyInitialized(() -> SimpleGasIngredient.CODEC))
                .xmap(either -> either.map(l -> l, r -> r), ingredient -> switch (ingredient) {
                    case SimpleGasIngredient simple -> Either.right(simple);
                    default -> Either.left(ingredient);
                });
    }

    static StreamCodec<RegistryFriendlyByteBuf, GasIngredient> streamCodec() {
        return ByteBufCodecs.registry(SilcoreRegistries.Keys.GAS_INGREDIENT_TYPES)
                .dispatch(GasIngredient::getType, GasIngredientType::streamCodec);
    }

    public static GasIngredientType<SimpleGasIngredient> simpleType() {
        MapCodec<SimpleGasIngredient> erroringMapCodec = new MapCodec<SimpleGasIngredient>() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            }

            @Override
            public <T> DataResult<SimpleGasIngredient> decode(DynamicOps<T> ops, MapLike<T> input) {
                return DataResult.error(() -> "Simple gas ingredients cannot be decoded using map syntax!");
            }

            @Override
            public <T> RecordBuilder<T> encode(SimpleGasIngredient input, DynamicOps<T> ops, RecordBuilder<T> builder) {
                return builder.withErrorsFrom(DataResult.error(() -> "Simple gas ingredients cannot be encoded using map syntax! Please use vanilla syntax (namespaced:item or #tag) instead!"));
            }
        };

        return new GasIngredientType<SimpleGasIngredient>(erroringMapCodec, SimpleGasIngredient.CONTENTS_STREAM_CODEC);
    }
}
