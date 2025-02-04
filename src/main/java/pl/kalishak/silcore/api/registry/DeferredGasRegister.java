package pl.kalishak.silcore.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;
import pl.kalishak.silcore.api.registry.object.DeferredGas;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Specialized DeferredRegister for {@link Gas Gases} that uses the specialized {@link DeferredGas} as the return type for {@link #register}.
 */
public class DeferredGasRegister extends DeferredRegister<Gas> {
    protected DeferredGasRegister(String namespace) {
        super(SilcoreRegistries.Keys.GAS, namespace);
    }

    public static DeferredGasRegister createGas(String namespace) {
        return new DeferredGasRegister(namespace);
    }

    /**
     * Adds a new gas to the list of entries to be registered and returns a {@link DeferredGas} that will be populated with the created gas automatically.
     *
     * @param name The new gas's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param func A factory for the new gas. The factory should not cache the created gas.
     * @return A {@link DeferredGas} that will track updates from the registry for this gas.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <G extends Gas> DeferredGas<G> register(String name, Function<ResourceLocation, ? extends G> func) {
        return (DeferredGas<G>) super.register(name, func);
    }

    /**
     * Adds a new gas to the list of entries to be registered and returns a {@link DeferredGas} that will be populated with the created gas automatically.
     *
     * @param name The new gas's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param sup A factory for the new gas. The factory should not cache the created gas.
     * @return A {@link DeferredGas} that will track updates from the registry for this gas.
     */
    @Override
    public <G extends Gas> DeferredGas<G> register(String name, Supplier<? extends G> sup) {
        return register(name, key -> sup.get());
    }

    /**
     * Adds a new simple {@link Gas} with the given {@link Gas.Properties properties} to the list of entries to be registered and returns a {@link DeferredGas} that will be populated with the created gas automatically.
     *
     * @param name  The new gas's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param properties The properties for the created gas.
     * @return A {@link DeferredGas} that will track updates from the registry for this gas.
     */
    public <G extends Gas> DeferredGas<G> registerSimpleGas(String name, Function<Gas.Properties, ? extends G> getter, Gas.Properties properties) {
        return register(name, () -> getter.apply(properties));
    }
}
