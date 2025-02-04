package pl.kalishak.silcore.api.registry.object;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Special {@link DeferredHolder} for {@link Fluid}.
 * @param <T> The specific {@link Fluid} type.
 */
public class DeferredFluid<T extends Fluid> extends DeferredHolder<Fluid, T> {
    protected DeferredFluid(ResourceKey<Fluid> key) {
        super(key);
    }

    /**
     * Creates a new {@link DeferredHolder} targeting the specified {@link Fluid}.
     *
     * @param <T> The type of the target {@link Fluid}.
     * @param key The resource key of the target {@link Fluid}.
     */
    public static <T extends Fluid> DeferredFluid<T> createFluid(ResourceKey<Fluid> key) {
        return new DeferredFluid<>(key);
    }

    /**
     * Creates a new {@link DeferredHolder} targeting the {@link Fluid} with the specified name.
     *
     * @param <T> The type of the target {@link Fluid}.
     * @param key The name of the target {@link Fluid}.
     */
    public static <T extends Fluid> DeferredFluid<T> createFluid(ResourceLocation key) {
        return createFluid(ResourceKey.create(Registries.FLUID, key));
    }

    /**
     * Creates a new {@link FluidStack} with the given size from this {@link Fluid}
     *
     * @param amount The size of the stack to create
     */
    public FluidStack toStack(int amount) {
        return new FluidStack(getDelegate(), amount);
    }

    /**
     * Creates a new {@link FluidStack} with a default size of 1 Bucket from this {@link Fluid}
     */
    public FluidStack toStack() {
        return new FluidStack(getDelegate(), FluidType.BUCKET_VOLUME);
    }
}
