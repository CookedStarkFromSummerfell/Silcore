package pl.kalishak.silcore.api.network.handler;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import pl.kalishak.silcore.api.network.ServerboundDrainTankPacket;
import pl.kalishak.silcore.api.world.inventory.AbstractContainerWithTankMenu;

public class DrainTankPacketHandler {
    public static void handle(ServerboundDrainTankPacket packet, IPayloadContext cxt) {
        cxt.enqueueWork(() -> {
            Player player = cxt.player();
            AbstractContainerMenu menu = player.containerMenu;

            if (menu instanceof AbstractContainerWithTankMenu<?> tankMenu && menu.containerId == packet.containerId()) {
                IFluidHandlerItem fluidHandler = packet.carried().getCapability(Capabilities.FluidHandler.ITEM);

                if (fluidHandler != null) {
                    FluidStack fluidInTank = tankMenu.getTankSlot(packet.slotId()).getFluid();

                    int filledToItem = fluidHandler.fill(fluidInTank, IFluidHandler.FluidAction.SIMULATE);

                    if (filledToItem > 0) {
                        fluidHandler.fill(fluidInTank, IFluidHandler.FluidAction.EXECUTE);
                        fluidInTank.shrink(filledToItem);
                        tankMenu.setFluid(packet.slotId(), fluidInTank);
                    }
                }
            }
        }).exceptionally(e -> {
            cxt.disconnect(Component.translatable("silcore.payload.networking_failed", e.getMessage()));
            return null;
        });
    }
}
