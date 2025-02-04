package pl.kalishak.silcore.api.world.item.component;

import com.google.common.base.Suppliers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Supplier;

public class SilcoreApiDataComponents {
    public static final Supplier<DataComponentType<MachinePropertiesComponent>> MACHINE_PROPERTIES = Suppliers.memoize(() -> DataComponentType.<MachinePropertiesComponent>builder()
            .persistent(MachinePropertiesComponent.CODEC)
            .networkSynchronized(MachinePropertiesComponent.STREAM_CODEC)
            .cacheEncoding()
            .build()
    );
    public static final Supplier<DataComponentType<ItemHandlerComponent>> ITEM_HANDLER = Suppliers.memoize(() -> DataComponentType.<ItemHandlerComponent>builder()
            .persistent(ItemHandlerComponent.CODEC)
            .networkSynchronized(ItemHandlerComponent.STREAM_CODEC)
            .build()
    );
    public static final Supplier<DataComponentType<ItemStack>> ITEM_STACK = Suppliers.memoize(() -> DataComponentType.<ItemStack>builder()
            .persistent(ItemStack.SINGLE_ITEM_CODEC)
            .networkSynchronized(ItemStack.STREAM_CODEC)
            .build()
    );
    public static final Supplier<DataComponentType<FluidHandlerComponent>> FLUID_HANDLER = Suppliers.memoize(() -> DataComponentType.<FluidHandlerComponent>builder()
            .persistent(FluidHandlerComponent.CODEC)
            .networkSynchronized(FluidHandlerComponent.STREAM_CODEC)
            .build()
    );
    public static final Supplier<DataComponentType<FluidStack>> FLUID_STACK = Suppliers.memoize(() -> DataComponentType.<FluidStack>builder()
            .persistent(FluidStack.CODEC)
            .networkSynchronized(FluidStack.STREAM_CODEC)
            .build()
    );
}
