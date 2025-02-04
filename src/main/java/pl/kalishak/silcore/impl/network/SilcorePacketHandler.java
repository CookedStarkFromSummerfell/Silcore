package pl.kalishak.silcore.impl.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import pl.kalishak.silcore.api.network.ServerboundDrainTankPacket;
import pl.kalishak.silcore.api.network.ServerboundFillTankPacket;
import pl.kalishak.silcore.api.network.handler.DrainTankPacketHandler;
import pl.kalishak.silcore.api.network.handler.FillTankPacketHandler;

public final class SilcorePacketHandler {
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                ServerboundFillTankPacket.TYPE,
                ServerboundFillTankPacket.STREAM_CODEC,
                FillTankPacketHandler::handle
        );

        registrar.playToServer(
                ServerboundDrainTankPacket.TYPE,
                ServerboundDrainTankPacket.STREAM_CODEC,
                DrainTankPacketHandler::handle
        );
    }
}
