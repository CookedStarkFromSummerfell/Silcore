package pl.kalishak.silcore.api.world.inventory;

import net.neoforged.neoforge.fluids.FluidStack;

public interface TankListener {
    void tankChanged(AbstractContainerWithTankMenu<?> tankToSend, int dataSlotId, FluidStack stack);

    void dataChanged(AbstractContainerWithTankMenu<?> tankToSend, int dataSlotId, int value);
}
