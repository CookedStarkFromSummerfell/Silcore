package pl.kalishak.silcore.api.registry.object;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredSound extends DeferredHolder<SoundEvent, SoundEvent> {
    protected DeferredSound(ResourceKey<SoundEvent> key) {
        super(key);
    }

    public static DeferredSound createSoundEvent(ResourceKey<SoundEvent> key) {
        return new DeferredSound(key);
    }

    public static DeferredSound createSoundEvent(ResourceLocation key) {
        return createSoundEvent(ResourceKey.create(Registries.SOUND_EVENT, key));
    }
}
