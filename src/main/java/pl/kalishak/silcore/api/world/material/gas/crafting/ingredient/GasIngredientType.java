package pl.kalishak.silcore.api.world.material.gas.crafting.ingredient;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.crafting.IngredientType;

/**
 * This represents the "type" of a {@link GasIngredient}, providing means of serializing
 * and deserializing the ingredient over both JSON and network, using the {@link #codec}
 * and {@link #streamCodec}, respectively.
 * <p>
 * Note that the {@link #streamCodec()} is only used if {@link GasIngredient#isSimple()} returns {@code false},
 * as otherwise its contents are synchronized directly to the network.
 *
 * @param <T> The type of gas ingredient
 * @see IngredientType IngredientType, a similar class for custom item ingredients
 */
public record GasIngredientType<T extends GasIngredient>(MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
    public GasIngredientType(MapCodec<T> mapCodec) {
        this(mapCodec, ByteBufCodecs.fromCodecWithRegistries(mapCodec.codec()));
    }
}
