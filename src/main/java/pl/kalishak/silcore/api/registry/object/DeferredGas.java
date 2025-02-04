package pl.kalishak.silcore.api.registry.object;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;

/**
 * Special {@link DeferredHolder} for {@link Gas}.
 * @param <T> The specific {@link Fluid} type.
 */
public class DeferredGas<T extends Gas> extends DeferredHolder<Gas, T> {
    protected DeferredGas(ResourceKey<Gas> key) {
        super(key);
    }

    /**
     * Creates a new {@link DeferredHolder} targeting the specified {@link Gas}.
     *
     * @param <T> The type of the target {@link Gas}.
     * @param key The resource key of the target {@link Gas}.
     */
    public static <T extends Gas> DeferredGas<T> createGas(ResourceKey<Gas> key) {
        return new DeferredGas<>(key);
    }

    /**
     * Creates a new {@link DeferredHolder} targeting the {@link Gas} with the specified name.
     *
     * @param <T> The type of the target {@link Gas}.
     * @param key The name of the target {@link Gas}.
     */
    public static <T extends Gas> DeferredGas<T> createGas(ResourceLocation key) {
        return createGas(ResourceKey.create(SilcoreRegistries.Keys.GAS, key));
    }

    /**
     * Creates a new {@link GasStack} with the given size from this {@link Gas}
     * @param pressure The pressure of the stack to create
     * @param volume The size of the stack to create
     */
    public GasStack toStack(float pressure, int volume) {
        return new GasStack(getDelegate(), pressure, volume);
    }

    /**
     * Creates a new {@link GasStack} with a default pressure of 1 atmosphere and size of 1 Bucket from this {@link Gas}
     */
    public GasStack toStack() {
        return new GasStack(getDelegate(), 1.0F, FluidType.BUCKET_VOLUME);
    }
}
