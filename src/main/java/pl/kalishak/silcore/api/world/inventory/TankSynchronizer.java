package pl.kalishak.silcore.api.world.inventory;

import net.minecraft.core.NonNullList;
import net.neoforged.neoforge.fluids.FluidStack;

public interface TankSynchronizer {
    void sendInitialData(AbstractContainerWithTankMenu<?> container, NonNullList<FluidStack> fluids, int[] initialData);

    void sendSlotChange(AbstractContainerWithTankMenu<?> container, int slot, FluidStack itemStack);

    void sendDataChange(AbstractContainerWithTankMenu<?> container, int id, int value);
}
