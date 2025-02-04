package pl.kalishak.silcore.impl.world.material.gas.crafting;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import pl.kalishak.silcore.api.serialization.SerializableEnum;
import pl.kalishak.silcore.api.world.item.crafting.RecipeBookCategory;

public enum GasRecipeBookCategory implements RecipeBookCategory {
    WATER("water", 0),
    PROCESSING("processing", 1),
    MISC("misc", 2);

    public static final Codec<GasRecipeBookCategory> CODEC = SerializableEnum.codec(GasRecipeBookCategory.class);
    public static final StreamCodec<ByteBuf, GasRecipeBookCategory> STREAM_CODEC = SerializableEnum.streamCodec(GasRecipeBookCategory.class);
    private final String name;
    private final int id;

    GasRecipeBookCategory(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public int id() {
        return this.id;
    }
}
