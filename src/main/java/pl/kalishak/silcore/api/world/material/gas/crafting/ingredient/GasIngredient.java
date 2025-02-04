package pl.kalishak.silcore.api.world.material.gas.crafting.ingredient;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.api.world.material.gas.crafting.display.GasSlotDisplay;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class GasIngredient implements Predicate<GasStack> {
    public static final Codec<GasIngredient> CODEC = GasIngredientCodecs.codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, GasIngredient> STREAM_CODEC = GasIngredientCodecs.streamCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<GasIngredient>> OPTIONAL_STREAM_CODEC = ByteBufCodecs.optional(STREAM_CODEC);

    @Nullable
    private List<Holder<Gas>> gases;

    /**
     * {@return a cached list of all Gas holders that this ingredient accepts}
     * This list is immutable and thus <b>can and should not</b> be modified by the caller!
     *
     * @see #generateGases()
     */
    public final List<Holder<Gas>> gases() {
        if (this.gases == null) {
            this.gases = generateGases().toList();
        }

        return this.gases;
    }

    /**
     * Checks if a given fluid stack matches this ingredient.
     * The stack <b>must not</b> be modified in any way.
     *
     * @param gasStack the stack to test
     * @return {@code true} if the stack matches, {@code false} otherwise
     */
    @Override
    public abstract boolean test(GasStack gasStack);

    /**
     * {@return a stream of gases accepted by this ingredient}
     * <p>
     * For compatibility reasons, implementations should follow the same guidelines
     * as for custom item ingredients, i.e.:
     * <ul>
     * <li>Returned gases are generally used for display purposes, and need not be exhaustive or perfectly accurate,
     * as ingredients may additionally filter by e.g. data component values.</li>
     * <li>An exception is ingredients that {@linkplain #isSimple() are simple},
     * for which it is important that this stream corresponds exactly all gases accepted by {@link #test(GasStack)}!</li>
     * <li>At least one stack should always be returned, so that the ingredient is not considered empty. <b>Empty ingredients may invalidate recipes!</b></li>
     * </ul>
     *
     * <p>Note: no caching needs to be done by the implementation, this is already handled by {@link #gases}!
     *
     * @return a stream of all gas stacks this ingredient accepts.
     *         <p>
     *         Note: No guarantees are made as to the amount of the gas,
     *         as GasIngredients are generally not meant to match by amount
     *         and these stacks are mostly used for display.
     *         <p>
     * @see ICustomIngredient#items()
     */
    @ApiStatus.OverrideOnly
    protected abstract Stream<Holder<Gas>> generateGases();

    /**
     * {@return a slot display for this ingredient, used for display on the client-side}
     *
     * @implNote The default implementation just constructs a list of stacks from {@link #gases()}.
     *           This is generally suitable for {@link #isSimple() simple} ingredients.
     *           Non-simple ingredients can either override this method to provide a more customized display,
     *           or let data pack writers use {@link CustomDisplayGasIngredient} to override the display of an ingredient.
     *
     * @see Ingredient#display()
     * @see GasSlotDisplay
     */
    public SlotDisplay display() {
        return new SlotDisplay.Composite(gases()
                .stream()
                .map(GasIngredient::displayForSingleGas)
                .toList()
        );
    }

    /**
     * Returns whether this gas ingredient always requires {@linkplain #test direct stack testing}.
     *
     * @return {@code true} if this ingredient ignores NBT data when matching stacks, {@code false} otherwise
     * @see ICustomIngredient#isSimple()
     */
    public abstract boolean isSimple();

    /**
     * {@return The type of this gas ingredient.}
     *
     * <p>The type <b>must</b> be registered to {@link pl.kalishak.silcore.api.registry.internal.SilcoreRegistries#GAS_INGREDIENT_TYPE}.
     */
    public abstract GasIngredientType<?> getType();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    public static SlotDisplay displayForSingleGas(Holder<Gas> gas) {
        return new GasSlotDisplay(gas);
    }

    public static GasIngredient of(GasStack... gases) {
        return of(Arrays.stream(gases).map(GasStack::getGasHolder));
    }

    public static GasIngredient of(Holder<Gas>... gases) {
        return of(Arrays.stream(gases));
    }

    public static GasIngredient of(Stream<Holder<Gas>> gases) {
        return of(HolderSet.direct(gases.toList()));
    }

    public static GasIngredient of(HolderSet<Gas> gases) {
        return new SimpleGasIngredient(gases);
    }
}
