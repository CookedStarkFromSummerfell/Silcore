package pl.kalishak.silcore.impl.world.level.block.entity.properties;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredRegister;
import pl.kalishak.silcore.SilcoreMod;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;
import pl.kalishak.silcore.api.world.level.block.entity.properties.IOType;
import pl.kalishak.silcore.api.world.material.gas.IGasTank;
import pl.kalishak.silcore.impl.world.material.gas.GasTank;

import java.util.function.Supplier;

public class SilcoreIoTypes {
    private static final DeferredRegister<IOType<?>> IO_TYPES = DeferredRegister.create(SilcoreRegistries.Keys.IO_TYPE, SilcoreMod.MOD_ID);

    public static final Supplier<EmptyIoType> EMPTY = IO_TYPES.register("empty", EmptyIoType::new);
    public static final Supplier<SimpleIoType<IItemHandler>> ITEM = IO_TYPES.register("item", () -> new SimpleIoType<>(ItemStackHandler::new));
    public static final Supplier<SimpleIoType<IFluidHandler>> FLUID = IO_TYPES.register("fluid", () -> new SimpleIoType<>(FluidTank::new));
    public static final Supplier<SimpleIoType<IGasTank>> GAS = IO_TYPES.register("gas", () -> new SimpleIoType<>(GasTank::new));

    public static void init(IEventBus bus) {
        IO_TYPES.register(bus);
    }
}
