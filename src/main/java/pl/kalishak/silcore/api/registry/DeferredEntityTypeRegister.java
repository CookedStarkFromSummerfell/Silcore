package pl.kalishak.silcore.api.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import pl.kalishak.silcore.api.registry.object.DeferredEntityType;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class DeferredEntityTypeRegister extends DeferredRegister<EntityType<?>> {
    protected DeferredEntityTypeRegister(String namespace) {
        super(Registries.ENTITY_TYPE, namespace);
    }

    public static DeferredEntityTypeRegister createEntityType(String namespace) {
        return new DeferredEntityTypeRegister(namespace);
    }

    public <E extends Entity> DeferredEntityType<E> registerEntityType(String name, Supplier<EntityType<E>> entityType) {
        register(name, entityType);

        return DeferredEntityType.createEntityType(ResourceLocation.fromNamespaceAndPath(getNamespace(), name));
    }

    public <E extends Entity> DeferredEntityType<E> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, UnaryOperator<EntityType.Builder<E>> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(getRegistryKey(), ResourceLocation.fromNamespaceAndPath(getNamespace(), name));
        return registerEntityType(name, () -> builder.apply(EntityType.Builder.of(factory, category)).build(key));
    }
}
