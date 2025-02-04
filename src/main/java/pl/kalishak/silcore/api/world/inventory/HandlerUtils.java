package pl.kalishak.silcore.api.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public interface HandlerUtils {
    static NonNullList<ItemStack> copyItems(IItemHandler handler) {
        NonNullList<ItemStack> items = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);

        for (int i = 0; i < handler.getSlots(); i++) {
            items.set(i, handler.getStackInSlot(i));
        }

        return items;
    }

    static NonNullList<FluidStack> copyFluids(IFluidHandler handler) {
        NonNullList<FluidStack> fluids = NonNullList.withSize(handler.getTanks(), FluidStack.EMPTY);

        for (int i = 0; i < handler.getTanks(); i++) {
            fluids.set(i, handler.getFluidInTank(i));
        }

        return fluids;
    }
}
