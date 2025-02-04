package pl.kalishak.silcore.api.world.material.gas;

import net.neoforged.bus.api.IEventBus;
import pl.kalishak.silcore.SilcoreMod;
import pl.kalishak.silcore.api.registry.DeferredGasRegister;
import pl.kalishak.silcore.api.registry.object.DeferredGas;
import pl.kalishak.silcore.impl.world.material.gas.SimpleGas;

public class SilcoreGases {
    private static final DeferredGasRegister GASES = DeferredGasRegister.createGas(SilcoreMod.MOD_ID);

    public static final DeferredGas<Gas> EMPTY = GASES.registerSimpleGas("empty", SimpleGas::new, new Gas.Properties());
    public static final DeferredGas<Gas> OXYGEN = GASES.registerSimpleGas("oxygen", SimpleGas::new, new Gas.Properties());

    public static void init(IEventBus eventBus) {
        GASES.register(eventBus);
    }
}
