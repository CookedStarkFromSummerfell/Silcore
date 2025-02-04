package pl.kalishak.silcore.api.world.level.block.entity.properties;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import pl.kalishak.silcore.api.serialization.SerializableEnum;

public enum RedstoneActivationMode implements SerializableEnum {
    ALWAYS_ON("always_on", true, 0),
    ALWAYS_OFF("always_off", false, 1),
    SIGNAL_ON("signal_on", true, 2),
    SIGNAL_OFF("signal_off", false, 3);

    public static final Codec<RedstoneActivationMode> CODEC = SerializableEnum.codec(RedstoneActivationMode.class);
    public static final StreamCodec<ByteBuf, RedstoneActivationMode> STREAM_CODEC = SerializableEnum.streamCodec(RedstoneActivationMode.class);
    private final String name;
    private final boolean signal;
    private final int id;

    RedstoneActivationMode(String name, boolean signal, int id) {
        this.name = name;
        this.signal = signal;
        this.id = id;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public int getSignal() {
        return this.signal ? 13 : 0;
    }

    public boolean needsSignal() {
        return this == SIGNAL_ON;
    }

    @Override
    public int id() {
        return this.id;
    }
}
