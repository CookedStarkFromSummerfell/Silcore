package pl.kalishak.silcore.api.world.level.block.entity.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BeehiveBlock;

import java.util.function.Predicate;

public class MachineProperties {
    public static final Codec<MachineProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MachineIO.CODEC.fieldOf("machine_io").forGetter(MachineProperties::machineIo),
            PrivacyProperties.CODEC.fieldOf("privacy_properties").forGetter(MachineProperties::privacyProperties),
            RedstoneActivationMode.CODEC.fieldOf("redstone_mode").forGetter(MachineProperties::redstoneMode)
    ).apply(instance, MachineProperties::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, MachineProperties> STREAM_CODEC = StreamCodec.composite(
            MachineIO.STREAM_CODEC, MachineProperties::machineIo,
            PrivacyProperties.STREAM_CODEC, MachineProperties::privacyProperties,
            RedstoneActivationMode.STREAM_CODEC, MachineProperties::redstoneMode,
            MachineProperties::new
    );

    protected MachineIO io;
    protected PrivacyProperties privacy;
    protected RedstoneActivationMode redstoneMode;

    private MachineProperties(MachineIO io, PrivacyProperties privacy, RedstoneActivationMode redstoneMode) {
        this.io = io;
        this.privacy = privacy;
        this.redstoneMode = redstoneMode;
    }

    public MachineProperties() {
        this(new MachineIO(), new PrivacyProperties(""), RedstoneActivationMode.ALWAYS_ON);
    }

    public MachineIO machineIo() {
        return this.io;
    }

    public PrivacyProperties privacyProperties() {
        return this.privacy;
    }

    public RedstoneActivationMode redstoneMode() {
        return this.redstoneMode;
    }

    public void changeIo(Predicate<MachineIO> action) {
        if (action.test(this.io)) {
            setChanged();
        }
    }

    public void setOwner(Player player) {
        if (this.privacy.setOwnerId(player.getUUID().toString())) {
            setChanged();
        }
    }

    public void setAccess(PrivacyProperties.Type type, Player player) {
        if (this.privacy.changeAccess(type, player)) {
            setChanged();
        }
    }

    public void addAccessTo(Player player) {
        if (this.privacy.addAccessTo(player.getUUID())) {
            setChanged();
        }
    }

    public void setRedstoneMode(RedstoneActivationMode redstoneMode) {
        if (this.redstoneMode != redstoneMode) {
            this.redstoneMode = redstoneMode;
            setChanged();
        }
    }

    public void setChanged() {

    }

    public void copyFrom(MachineProperties other) {
        this.io = other.io;
        this.privacy = other.privacy;
        this.redstoneMode = other.redstoneMode;
    }

    public Tag save(HolderLookup.Provider registries) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);

        return CODEC.encodeStart(ops, this).getOrThrow();
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        MachineProperties other = CODEC.parse(ops, tag).getOrThrow();

        copyFrom(other);
    }
}
