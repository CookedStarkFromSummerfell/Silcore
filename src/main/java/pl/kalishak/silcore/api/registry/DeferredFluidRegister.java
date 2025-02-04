package pl.kalishak.silcore.api.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import pl.kalishak.silcore.api.registry.object.DeferredFluid;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Specialized DeferredRegister for {@link Fluid Fluids} that uses the specialized {@link DeferredFluid} as the return type for {@link #register}.
 */
public class DeferredFluidRegister extends DeferredRegister<Fluid> {
    private final DeferredRegister<FluidType> fluidTypeRegister;

    protected DeferredFluidRegister(String namespace) {
        super(Registries.FLUID, namespace);
        this.fluidTypeRegister = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, namespace);
    }

    public static DeferredFluidRegister createFluids(String namespace) {
        return new DeferredFluidRegister(namespace);
    }

    /**
     * Adds a new fluid type to the list of entries to be registered and returns a {@link DeferredHolder} that will be populated with the created fluid type automatically.
     *
     * @param name The new fluid type's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param properties A factory for the new fluidtype. The factory should not cache the created fluidtype.
     * @return A {@link DeferredHolder} that will track updates from the registry for this fluidtype.
     */
    public DeferredHolder<FluidType, FluidType> registerFluidType(String name, Supplier<FluidType.Properties> properties) {
        return this.fluidTypeRegister.register(name, () -> new FluidType(properties.get().descriptionId("block." + getNamespace() + "." + name)));
    }

    /**
     * Adds a new fluid to the list of entries to be registered and returns a {@link DeferredFluid} that will be populated with the created fluid automatically.
     *
     * @param name The new fluid's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param func A factory for the new fluid. The factory should not cache the created fluid.
     * @return A {@link DeferredFluid} that will track updates from the registry for this fluid.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <F extends Fluid> DeferredFluid<F> register(String name, Function<ResourceLocation, ? extends F> func) {
        return (DeferredFluid<F>) super.register(name, func);
    }

    /**
     * Adds a new fluid to the list of entries to be registered and returns a {@link DeferredFluid} that will be populated with the created fluid automatically.
     *
     * @param name The new fluid's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param sup A factory for the new fluid. The factory should not cache the created fluid.
     * @return A {@link DeferredFluid} that will track updates from the registry for this fluid.
     */
    @Override
    public <F extends Fluid> DeferredFluid<F> register(String name, Supplier<? extends F> sup) {
        return register(name, key -> sup.get());
    }

    /**
     * Adds a new fluid to the list of entries to be registered and returns a {@link DeferredFluid} that will be populated with the created fluid automatically.
     *
     * @param name  The new fluid's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param func  A factory for the new fluid. The factory should not cache the created fluid.
     * @param properties The properties for the created fluid.
     * @return A {@link DeferredHolder} that will track updates from the registry for this fluid.
     * @see #registerFlowing(String, BaseFlowingFluid.Properties)
     * @see #registerSource(String, BaseFlowingFluid.Properties)
     */
    public <F extends Fluid> DeferredHolder<Fluid, F> registerFluid(String name, Function<BaseFlowingFluid.Properties, F> func, BaseFlowingFluid.Properties properties) {
        return register(name, () -> func.apply(properties));
    }

    /**
     * Adds a new source fluid to the list of entries to be registered and returns a {@link DeferredFluid} that will be populated with the created fluid automatically.
     *
     * @param name  The new fluid's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param properties The properties for the created fluid.
     * @return A {@link DeferredHolder} that will track updates from the registry for this fluid.
     * @see #registerFluid(String, Function, BaseFlowingFluid.Properties)
     */
    public DeferredHolder<Fluid, BaseFlowingFluid.Flowing> registerFlowing(String name, BaseFlowingFluid.Properties properties) {
        return registerFluid(name, BaseFlowingFluid.Flowing::new, properties);
    }

    /**
     * Adds a new flowing fluid to the list of entries to be registered and returns a {@link DeferredFluid} that will be populated with the created fluid automatically.
     *
     * @param name  The new fluid's name. It will automatically have the {@linkplain #getNamespace() namespace} prefixed.
     * @param properties The properties for the created fluid.
     * @return A {@link DeferredHolder} that will track updates from the registry for this fluid.
     * @see #registerFluid(String, Function, BaseFlowingFluid.Properties)
     */
    public DeferredHolder<Fluid, BaseFlowingFluid.Source> registerSource(String name, BaseFlowingFluid.Properties properties) {
        return registerFluid(name, BaseFlowingFluid.Source::new, properties);
    }

    /**
     * Creates a new {@link BaseFlowingFluid.Properties}
     * @param fluidType Type of registered fluid
     * @param stillFluid Already declared DeferredFluid with help of {@link DeferredFluid#createFluid(ResourceLocation)}
     * @param stillFactory Function making fluid by its properties
     * @param flowingFluid Already declared DeferredFluid with help of {@link DeferredFluid#createFluid(ResourceLocation)}
     * @param flowingFactory Function making fluid by its properties
     * @param andThen Further properties
     * @return FluidProperties
     * @param <S> Instance of {@link BaseFlowingFluid.Source}
     * @param <F> Instance of {@link BaseFlowingFluid.Flowing}
     * @see #createSimpleFluid(DeferredHolder, DeferredFluid, DeferredFluid, UnaryOperator)
     */
    public <S extends Fluid, F extends Fluid> Supplier<BaseFlowingFluid.Properties> createSimpleFluid(DeferredHolder<FluidType, FluidType> fluidType, DeferredFluid<S> stillFluid, Function<BaseFlowingFluid.Properties, ? extends S> stillFactory, DeferredFluid<F> flowingFluid, Function<BaseFlowingFluid.Properties, ? extends F> flowingFactory, UnaryOperator<BaseFlowingFluid.Properties> andThen) {
        Supplier<BaseFlowingFluid.Properties> properties = () -> andThen.apply(new BaseFlowingFluid.Properties(
                fluidType::value,
                stillFluid::value,
                flowingFluid::value
        ));

        register(stillFluid.getId().getPath(), () -> stillFactory.apply(properties.get()));
        register(flowingFluid.getId().getPath(), () -> flowingFactory.apply(properties.get()));
        return properties;
    }

    /**
     * Creates a new {@link BaseFlowingFluid.Properties}
     * @param fluidType Type of registered fluid
     * @param stillFluid Already declared DeferredFluid with help of {@link DeferredFluid#createFluid(ResourceLocation)}
     * @param flowingFluid Already declared DeferredFluid with help of {@link DeferredFluid#createFluid(ResourceLocation)}
     * @param andThen Further properties
     * @return FluidProperties with default instances of BaseFlowingFluid's subclasses
     * @see #createSimpleFluid(DeferredHolder, DeferredFluid, Function, DeferredFluid, Function, UnaryOperator)
     */
    public Supplier<BaseFlowingFluid.Properties> createSimpleFluid(DeferredHolder<FluidType, FluidType> fluidType, DeferredFluid<BaseFlowingFluid.Source> stillFluid, DeferredFluid<BaseFlowingFluid.Flowing> flowingFluid, UnaryOperator<BaseFlowingFluid.Properties> andThen) {
        return createSimpleFluid(
                fluidType,
                stillFluid,
                BaseFlowingFluid.Source::new,
                flowingFluid,
                BaseFlowingFluid.Flowing::new,
                andThen
        );
    }

    @Override
    public void register(IEventBus bus) {
        this.fluidTypeRegister.register(bus);
        super.register(bus);
    }
}
