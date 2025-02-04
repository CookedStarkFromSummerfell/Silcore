package pl.kalishak.silcore.api.world.material.gas;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.DecoderException;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.common.util.DataComponentUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;
import pl.kalishak.silcore.SilcoreMod;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * FluidStack implementation for Gases
 */
public class GasStack implements MutableDataComponentHolder {
    public static final Codec<Holder<Gas>> NON_EMPTY_GAS_CODEC = Codec.lazyInitialized(() -> SilcoreRegistries.GAS.holderByNameCodec().validate(holder ->
            holder.is(ResourceLocation.fromNamespaceAndPath(SilcoreMod.MOD_ID, "empty"))
                    ? DataResult.error(() -> "Gas must not be empty")
                    : DataResult.success(holder)
    ));

    public static final Codec<GasStack> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(instance -> instance.group(
                    NON_EMPTY_GAS_CODEC.fieldOf("id").forGetter(GasStack::getGasHolder),
                    ExtraCodecs.POSITIVE_FLOAT.fieldOf("pressure").forGetter(GasStack::getPressure),
                    ExtraCodecs.POSITIVE_INT.fieldOf("volume").forGetter(GasStack::getVolume),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(gasStack -> gasStack.components.asPatch())
            ).apply(instance, GasStack::new))
    );

    public static Codec<GasStack> fixedAmountCodec(float pressure, int volume) {
        return Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
                NON_EMPTY_GAS_CODEC.fieldOf("id").forGetter(GasStack::getGasHolder),
                DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(gasStack -> gasStack.components.asPatch())
        ).apply(instance, (holder, patch) -> new GasStack(holder, pressure, volume, patch))));
    }

    public static final Codec<GasStack> OPTIONAL_CODEC = ExtraCodecs.optionalEmptyMap(CODEC)
            .xmap(optional -> optional.orElse(GasStack.EMPTY), gasStack -> gasStack.isEmpty() ? Optional.empty() : Optional.of(gasStack));

    public static final StreamCodec<RegistryFriendlyByteBuf, GasStack> OPTIONAL_STREAM_CODEC = new StreamCodec<>() {
        private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Gas>> GAS_STREAM_CODEC = ByteBufCodecs.holderRegistry(SilcoreRegistries.Keys.GAS);

        @Override
        public GasStack decode(RegistryFriendlyByteBuf buffer) {
            float pressure = buffer.readFloat();
            int volume = buffer.readVarInt();

            if (pressure == 0.0F || volume == 0) {
                return GasStack.EMPTY;
            }

            Holder<Gas> holder = GAS_STREAM_CODEC.decode(buffer);
            DataComponentPatch patch = DataComponentPatch.STREAM_CODEC.decode(buffer);

            return new GasStack(holder, pressure, volume, patch);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, GasStack value) {
            if (value.isEmpty()) {
                buffer.writeFloat(0.0F);
                buffer.writeVarInt(0);
            } else {
                buffer.writeFloat(value.getPressure());
                buffer.writeVarInt(value.getVolume());
                GAS_STREAM_CODEC.encode(buffer, value.getGasHolder());
                DataComponentPatch.STREAM_CODEC.encode(buffer, value.components.asPatch());
            }
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, GasStack> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public GasStack decode(RegistryFriendlyByteBuf buffer) {
            GasStack stack = GasStack.OPTIONAL_STREAM_CODEC.decode(buffer);

            if (stack.isEmpty()) {
                throw new DecoderException("Empty GasStack not allowed");
            }

            return stack;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, GasStack value) {
            if (value.isEmpty()) {
                throw new DecoderException("Empty GasStack not allowed");
            }

            GasStack.OPTIONAL_STREAM_CODEC.encode(buffer, value);
        }
    };

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final GasStack EMPTY = new GasStack(null, 0.0F, 0);
    private float pressure;
    private int volume;
    private final Holder<Gas> gas;
    private final PatchedDataComponentMap components;

    /**
     * Full GasStack declaration,
     * @param gas Gas itself
     * @param pressure Initial pressure for stack, meant to describe whether stack may be used in recipes
     * @param volume Amount of gas
     * @param patch Components
     */
    public GasStack(Holder<Gas> gas, float pressure, int volume, DataComponentPatch patch) {
        this(gas, pressure, volume, PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, patch));
    }

    /**
     * Default GasStack declaration
     * @param gas Gas itself
     * @param pressure Initial pressure for stack, meant to describe whether stack may be used in recipes
     * @param volume Amount of gas
     */
    public GasStack(Holder<Gas> gas, float pressure, int volume) {
        this(gas, pressure, volume, new PatchedDataComponentMap(DataComponentMap.EMPTY));
    }

    private GasStack(Holder<Gas> gas, float pressure, int volume, PatchedDataComponentMap components) {
        this.gas = gas;
        this.pressure = pressure;
        this.volume = volume;
        this.components = components;
    }

    /**
     * Loads GasStack from Tag
     * @param registries Registries lookup
     * @param tag Tag containing GasStack data
     * @return Optional GasStack read from tag
     */
    public static Optional<GasStack> parse(HolderLookup.Provider registries, Tag tag) {
        return CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag)
                .resultOrPartial(error -> LOGGER.error("Tried to load invalid gas: '{}'", error));
    }

    /**
     * Loads GasStack from Tag or returns an empty one
     * @param registries Registries lookup
     * @param tag Tag containing GasStack data
     * @return Empty or GasStack
     */
    public static GasStack parseOptional(HolderLookup.Provider registries, CompoundTag tag) {
        return tag.isEmpty() ? EMPTY : parse(registries, tag).orElse(EMPTY);
    }

    public boolean isEmpty() {
        return this == EMPTY || this.gas == null || this.volume < 0;
    }

    /**
     * Splits GasStack with provided volume
     * @param volume Amount of gas needed
     * @return GasStack with wanted amount
     */
    public GasStack split(int volume) {
        int i = Math.min(volume, this.volume);

        GasStack gasStack = copyWithVolume(i);
        decreaseVolume(i);

        return gasStack;
    }

    /**
     * Copies and then clears this GasStack
     * @return Copy of this stack
     */
    public GasStack copyAndClear() {
        if (isEmpty()) {
            return EMPTY;
        }

        GasStack gasStack = copy();
        setAmount(0.0F, 0);

        return gasStack;
    }

    public Holder<Gas> getGasHolder() {
        return this.gas;
    }

    public Gas getGas() {
        return getGasHolder().value();
    }

    public float getPressure() {
        return this.pressure;
    }

    public int getVolume() {
        return this.volume;
    }

    public boolean is(TagKey<Gas> tag) {
        return this.gas.is(tag);
    }

    public boolean is(Holder<Gas> gas) {
        return this.gas.value() == gas.value();
    }

    public boolean is(Predicate<Holder<Gas>> predicate) {
        return predicate.test(this.gas);
    }

    public boolean is(HolderSet<Gas> holders) {
        return holders.contains(this.gas);
    }

    public Stream<TagKey<Gas>> getTags() {
        return this.gas.tags();
    }

    /**
     * Serializes GasStack onto Tag
     * @param registries Registries lookup
     * @param tag Tag to save stack
     * @return Tag containing this stack
     */
    public Tag save(HolderLookup.Provider registries, Tag tag) {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot encode empty GasStack");
        }

        return DataComponentUtil.wrapEncodingExceptions(this, CODEC, registries, tag);
    }

    /**
     * Serializes GasStack
     * @param registries Registries lookup
     * @return New tag containing GasStack
     */
    public Tag save(HolderLookup.Provider registries) {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot encode empty GasStack");
        }

        return DataComponentUtil.wrapEncodingExceptions(this, CODEC, registries);
    }

    /**
     * Saves only GasStack with variables
     * @param registries Registries Lookup
     * @return Tag containing GasStack or empty when there's nothing to serialize
     */
    public Tag saveOptional(HolderLookup.Provider registries) {
        return isEmpty() ? new CompoundTag() : save(registries, new CompoundTag());
    }

    /**
     * @return Pure copy of this GasStack
     */
    public GasStack copy() {
        if (isEmpty()) {
            return EMPTY;
        }

        return new GasStack(this.gas, this.pressure, this.volume, this.components.copy());
    }

    /**
     * Copies GasStack with given amount
     * @param volume Wanted amount
     * @return A copy with amount
     */
    public GasStack copyWithVolume(int volume) {
        if (isEmpty()) {
            return EMPTY;
        }

        GasStack gasStack = copy();
        gasStack.setVolume(volume);
        return gasStack;
    }

    /**
     * Checks whether stacks are the same
     * @param first Stack to compare
     * @param second Stack to compare
     * @return Whether both are the same
     */
    public static boolean matches(GasStack first, GasStack second) {
        if (first == second) {
            return true;
        }

        return first.getPressure() == second.getPressure() && first.getVolume() == second.getVolume() && isSameGasComponents(first, second);
    }

    /**
     * Checks if both stacks have the same gas
     * @param first Stack to compare
     * @param second Stack to compare
     * @return Whether both are the same
     */
    public static boolean isSameGas(GasStack first, GasStack second) {
        return first.is(second.getGasHolder());
    }

    /**
     * Checks if both stacks have same components
     * @param first Stack to compare
     * @param second Stack to compare
     * @return Whether both stacks have the same components
     */
    public static boolean isSameGasComponents(GasStack first, GasStack second) {
        if (!first.is(second.getGasHolder())) {
            return false;
        }

        return (first.isEmpty() && second.isEmpty()) || Objects.equals(first.components, second.components);
    }

    public static MapCodec<GasStack> lenientOptionalFieldOf(String fieldName) {
        return CODEC.lenientOptionalFieldOf(fieldName)
                .xmap(optional -> optional.orElse(EMPTY), gasStack -> gasStack.isEmpty() ? Optional.empty() : Optional.of(gasStack));
    }

    public static int hashGasAndComponents(@Nullable GasStack gasStack) {
        if (gasStack != null) {
            int i = 32 + gasStack.getGasHolder().value().hashCode();
            return 32 * i + gasStack.getComponents().hashCode();
        }

        return 0;
    }

    public String getDescriptionId() {
        return getGasHolder().value().getDescriptionId();
    }

    @Override
    public String toString() {
        return getGasHolder().getRegisteredName() + "[p=" + getPressure() + ", V=" + getVolume() + "]";
    }

    @Override
    public <T> @Nullable T set(DataComponentType<? super T> componentType, @Nullable T value) {
        return this.components.set(componentType, value);
    }

    @Override
    public <T> @Nullable T remove(DataComponentType<? extends T> componentType) {
        return this.components.remove(componentType);
    }

    @Override
    public void applyComponents(DataComponentPatch patch) {
        this.components.applyPatch(patch);
    }

    @Override
    public void applyComponents(DataComponentMap components) {
        this.components.setAll(components);
    }

    @Override
    public DataComponentMap getComponents() {
        return this.components;
    }

    public Component getHoverName() {
        return getGasHolder().value().getDescriptionId(this);
    }

    public void setAmount(float pressure, int volume) {
        this.pressure = pressure;
        this.volume = volume;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void limit(float pressure, int volume) {
        if (!isEmpty() && getPressure() > pressure && getVolume() > volume) {
            setAmount(pressure, volume);
        }
    }

    public void increase(float pressure, int volume) {
        increasePressure(pressure);
        increaseVolume(volume);
    }

    public void increasePressure(float pressure) {
        setPressure(getPressure() + pressure);
    }

    public void increaseVolume(int volume) {
        setVolume(getVolume() + volume);
    }

    public void decrease(float pressure, int volume) {
        decreasePressure(pressure);
        decreaseVolume(volume);
    }

    public void decreasePressure(float pressure) {
        setPressure(getPressure() - pressure);
    }

    public void decreaseVolume(int volume) {
        setVolume(getVolume() - volume);
    }
}
