package pl.kalishak.silcore.api.world.level.block.entity.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;
import pl.kalishak.silcore.impl.world.level.block.entity.properties.SilcoreIoTypes;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class MachineIO {
    public static final Codec<MachineIO> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.strictUnboundedMap(Direction.CODEC, SilcoreRegistries.IO_TYPE.byNameCodec()).fieldOf("faces").forGetter(MachineIO::faces)
    ).apply(instance, MachineIO::new)));
    public static final StreamCodec<RegistryFriendlyByteBuf, MachineIO> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, Direction.STREAM_CODEC, ByteBufCodecs.fromCodecWithRegistries(SilcoreRegistries.IO_TYPE.byNameCodec())), MachineIO::faces,
            MachineIO::new
    );

    protected final Map<Direction, IOType<?>> facesIO;

    private MachineIO(Map<Direction, IOType<?>> faces) {
        this.facesIO = faces;
    }

    public MachineIO() {
        this.facesIO = initEmptyFaces();
    }

    private EnumMap<Direction, IOType<?>> initEmptyFaces() {
        EnumMap<Direction, IOType<?>> faces = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            faces.put(direction, SilcoreIoTypes.EMPTY.get());
        }

        return faces;
    }

    public boolean setFace(Direction direction, IOType<?> type) {
        return type != this.facesIO.put(direction, type);
    }

    public @NotNull IOType<?> getType(Direction direction) {
        IOType<?> type = this.facesIO.get(direction);

        if (type == null) {
            type = SilcoreIoTypes.EMPTY.get();
        }

        return type;
    }

    public boolean hasCustomIO() {
        return false;
    }

    private Map<Direction, IOType<?>> faces() {
        return this.facesIO;
    }
}
