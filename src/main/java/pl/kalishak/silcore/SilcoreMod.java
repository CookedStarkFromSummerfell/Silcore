package pl.kalishak.silcore;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import pl.kalishak.silcore.api.SilcoreApi;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;
import pl.kalishak.silcore.api.world.material.gas.SilcoreGases;
import pl.kalishak.silcore.api.world.material.gas.crafting.display.SilcoreSlotDisplays;
import pl.kalishak.silcore.impl.network.SilcorePacketHandler;
import pl.kalishak.silcore.impl.world.item.SilcoreDataComponents;
import pl.kalishak.silcore.impl.world.level.block.entity.properties.SilcoreIoTypes;
import pl.kalishak.silcore.impl.world.material.gas.crafting.ingredient.SilcoreGasIngredientTypes;

@Mod(SilcoreMod.MOD_ID)
public class SilcoreMod {
    public static final String MOD_ID = "silcore";

    public SilcoreMod(IEventBus bus, ModContainer container) {
        bus.addListener(SilcoreRegistries::newRegistries);
        bus.addListener(SilcorePacketHandler::registerPackets);

        SilcoreDataComponents.init(bus);
        SilcoreGases.init(bus);
        SilcoreGasIngredientTypes.init(bus);
        SilcoreIoTypes.init(bus);
        SilcoreSlotDisplays.init(bus);
    }
}
