package pl.kalishak.silcore.api.client.gui;

import net.minecraft.world.inventory.ClickType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import pl.kalishak.silcore.api.world.inventory.FluidSlot;

@OnlyIn(Dist.CLIENT)
public interface FluidTankSlotMouseAction {
    boolean matches(FluidSlot slot);

    void onStopHovering(FluidSlot slot);

    void onTankSlotClicked(FluidSlot slot, ClickType clickType);
}
