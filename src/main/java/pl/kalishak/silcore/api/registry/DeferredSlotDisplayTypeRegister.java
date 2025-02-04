package pl.kalishak.silcore.api.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DeferredSlotDisplayTypeRegister extends DeferredRegister<SlotDisplay.Type<?>> {
    protected DeferredSlotDisplayTypeRegister(String namespace) {
        super(Registries.SLOT_DISPLAY, namespace);
    }

    public static DeferredSlotDisplayTypeRegister createSlotDisplay(String namespace) {
        return new DeferredSlotDisplayTypeRegister(namespace);
    }

    public <T extends SlotDisplay> DeferredHolder<SlotDisplay.Type<?>, SlotDisplay.Type<T>> registerSlotDisplay(String name, MapCodec<T> mapCodec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return register(name, () -> new SlotDisplay.Type<>(mapCodec, streamCodec));
    }
}
