package pl.kalishak.silcore.api.world.level.block.entity.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import pl.kalishak.silcore.api.serialization.SerializableEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PrivacyProperties {
    public static final Codec<PrivacyProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("owner_id", "").forGetter(PrivacyProperties::getOwnerId),
            Type.CODEC.fieldOf("type").forGetter(PrivacyProperties::getType),
            UUIDUtil.AUTHLIB_CODEC.listOf().optionalFieldOf("shared_access", List.of()).forGetter(PrivacyProperties::getSharedAccess)
    ).apply(instance, PrivacyProperties::new));
    public static final StreamCodec<ByteBuf, PrivacyProperties> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PrivacyProperties::getOwnerId,
            Type.STREAM_CODEC, PrivacyProperties::getType,
            ByteBufCodecs.fromCodec(UUIDUtil.AUTHLIB_CODEC.listOf()), PrivacyProperties::getSharedAccess,
            PrivacyProperties::new
    );

    private String ownerId = "";
    private Type type = Type.PUBLIC;
    private List<UUID> sharedAccess = new ArrayList<>();

    private PrivacyProperties(String ownerId, Type type, List<UUID> sharedAccess) {
        this.ownerId = ownerId;
        this.type = type;
        this.sharedAccess = sharedAccess;
    }

    public PrivacyProperties(String ownerId) {
        this.ownerId = ownerId;
    }

    public PrivacyProperties(Player player) {
        this(player.getUUID().toString());
    }

    boolean setOwnerId(String ownerId) {
        if (this.type == Type.PUBLIC) {
            this.ownerId = ownerId;
            return true;
        }

        return false;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    boolean changeAccess(Type type, Player player) {
        boolean hasPermission = true;

        if (!this.ownerId.isEmpty()) {
            hasPermission = player.getUUID().toString().equals(this.ownerId);
        }

        if (hasPermission) {
            return this.type != type;
        }

        return false;
    }

    public Type getType() {
        return this.type;
    }

    boolean addAccessTo(UUID otherOwner) {
        if (!this.ownerId.isEmpty() && this.type == Type.SHARED) {
            if (!this.sharedAccess.contains(otherOwner)) {
                this.sharedAccess.add(otherOwner);

                return true;
            }
        }

        return false;
    }

    public List<UUID> getSharedAccess() {
        return this.sharedAccess;
    }


    public enum Type implements SerializableEnum {
        PUBLIC("public", 0),
        PRIVATE("private", 1),
        SHARED("shared", 2);

        public static final Codec<PrivacyProperties.Type> CODEC = SerializableEnum.codec(Type.class);
        public static final StreamCodec<ByteBuf, PrivacyProperties.Type> STREAM_CODEC = SerializableEnum.streamCodec(Type.class, Type::id);
        private final String name;
        private final int id;

        Type(String name, int id) {
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
}
