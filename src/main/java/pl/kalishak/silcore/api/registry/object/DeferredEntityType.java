package pl.kalishak.silcore.api.registry.object;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredEntityType<E extends Entity> extends DeferredHolder<EntityType<?>, EntityType<E>> {
    protected DeferredEntityType(ResourceKey<EntityType<?>> key) {
        super(key);
    }

    public static <E extends Entity> DeferredEntityType<E> createEntityType(ResourceLocation key) {
        return createEntityType(ResourceKey.create(Registries.ENTITY_TYPE, key));
    }

    public static <E extends Entity> DeferredEntityType<E> createEntityType(ResourceKey<EntityType<?>> key) {
        return new DeferredEntityType<>(key);
    }
}
