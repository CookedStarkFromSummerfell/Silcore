package pl.kalishak.silcore.impl.world.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import pl.kalishak.silcore.SilcoreMod;
import pl.kalishak.silcore.api.world.item.component.FluidHandlerComponent;
import pl.kalishak.silcore.api.world.item.component.ItemHandlerComponent;
import pl.kalishak.silcore.api.world.item.component.MachinePropertiesComponent;
import pl.kalishak.silcore.api.world.item.component.SilcoreApiDataComponents;
import pl.kalishak.silcore.api.world.level.block.entity.properties.MachineProperties;

public class SilcoreDataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SilcoreMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MachinePropertiesComponent>> MACHINE_PROPERTIES = DATA_COMPONENT_TYPES.register(
            "machine_properties",
            SilcoreApiDataComponents.MACHINE_PROPERTIES
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemHandlerComponent>> ITEM_HANDLER = DATA_COMPONENT_TYPES.register(
            "item_handler",
            SilcoreApiDataComponents.ITEM_HANDLER
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStack>> ITEM_STACK = DATA_COMPONENT_TYPES.register(
            "item_stack",
            SilcoreApiDataComponents.ITEM_STACK
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidHandlerComponent>> FLUID_HANDLER = DATA_COMPONENT_TYPES.register(
            "fluid_handler",
            SilcoreApiDataComponents.FLUID_HANDLER
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidStack>> FLUID_STACK = DATA_COMPONENT_TYPES.register(
            "fluid_stack",
            SilcoreApiDataComponents.FLUID_STACK
    );

    public static void init(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}
