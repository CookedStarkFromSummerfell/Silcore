package pl.kalishak.silcore.api.world.material.gas.crafting.display;

import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.ApiStatus;
import pl.kalishak.silcore.SilcoreMod;
import pl.kalishak.silcore.api.registry.DeferredSlotDisplayTypeRegister;

@ApiStatus.Internal
public final class SilcoreSlotDisplays {
    private static final DeferredSlotDisplayTypeRegister SLOT_DISPLAYS = DeferredSlotDisplayTypeRegister.createSlotDisplay(SilcoreMod.MOD_ID);

    public static final DeferredHolder<SlotDisplay.Type<?>, SlotDisplay.Type<GasSlotDisplay>> GAS_SLOT_DISPLAY = SLOT_DISPLAYS.registerSlotDisplay(
            "gas", GasSlotDisplay.MAP_CODEC, GasSlotDisplay.STREAM_CODEC
    );
    public static final DeferredHolder<SlotDisplay.Type<?>, SlotDisplay.Type<GasStackSlotDisplay>> GAS_STACK_SLOT_DISPLAY = SLOT_DISPLAYS.registerSlotDisplay(
            "gas_stack", GasStackSlotDisplay.MAP_CODEC, GasStackSlotDisplay.STREAM_CODEC
    );
    public static final DeferredHolder<SlotDisplay.Type<?>, SlotDisplay.Type<GasTagSlotDisplay>> GAS_TAG_SLOT_DISPLAY = SLOT_DISPLAYS.registerSlotDisplay(
            "gas_tag", GasTagSlotDisplay.MAP_CODEC, GasTagSlotDisplay.STREAM_CODEC
    );

    public static void init(IEventBus bus) {
        SLOT_DISPLAYS.register(bus);
    }
}
