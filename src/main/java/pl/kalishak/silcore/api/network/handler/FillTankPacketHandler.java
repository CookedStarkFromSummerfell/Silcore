package pl.kalishak.silcore.api.network.handler;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import pl.kalishak.silcore.api.network.ServerboundFillTankPacket;
import pl.kalishak.silcore.api.world.inventory.AbstractContainerWithTankMenu;

public class FillTankPacketHandler {
    public static void handle(ServerboundFillTankPacket packet, IPayloadContext cxt) {
        cxt.enqueueWork(() -> {
            Player player = cxt.player();
            AbstractContainerMenu menu = player.containerMenu;

            if (menu instanceof AbstractContainerWithTankMenu<?> tankMenu && menu.containerId == packet.containerId()) {
                IFluidHandlerItem fluidHandler = packet.carried().getCapability(Capabilities.FluidHandler.ITEM);

                if (fluidHandler != null) {
                    FluidStack fluidInSlot = tankMenu.getTankSlot(packet.slotId()).getFluid();

                    int filled = fluidHandler.fill(fluidInSlot, IFluidHandler.FluidAction.SIMULATE);

                    if (filled > 0) {
                        tankMenu.setFluid(packet.slotId(), fluidHandler.drain(fluidInSlot, IFluidHandler.FluidAction.EXECUTE));
                    }
                }
            }

        }).exceptionally(e -> {
            cxt.disconnect(Component.translatable("silcore.payload.networking_failed", e.getMessage()));
            return null;
        });
    }
}
