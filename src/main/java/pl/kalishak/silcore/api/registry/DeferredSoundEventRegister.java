package pl.kalishak.silcore.api.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import pl.kalishak.silcore.api.registry.object.DeferredSound;

import java.util.function.Function;

public class DeferredSoundEventRegister extends DeferredRegister<SoundEvent> {
    protected DeferredSoundEventRegister(String namespace) {
        super(Registries.SOUND_EVENT, namespace);
    }

    public static DeferredSoundEventRegister createSoundEvent(String namespace) {
        return new DeferredSoundEventRegister(namespace);
    }

    private DeferredSound registerSound(String name, Function<ResourceLocation, SoundEvent> func) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(getNamespace(), name);
        register(name, () -> func.apply(id));

        return DeferredSound.createSoundEvent(id);
    }

    public DeferredSound registerFixedSound(String name, float range) {
        return registerSound(name, id -> SoundEvent.createFixedRangeEvent(id, range));
    }

    public DeferredSound registerVariableSound(String name) {
        return registerSound(name, SoundEvent::createVariableRangeEvent);
    }
}
