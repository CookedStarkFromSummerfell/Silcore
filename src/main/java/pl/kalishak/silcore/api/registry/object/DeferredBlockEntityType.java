package pl.kalishak.silcore.api.registry.object;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredBlockEntityType<B extends BlockEntity> extends DeferredHolder<BlockEntityType<?>, BlockEntityType<B>> {
    protected DeferredBlockEntityType(ResourceKey<BlockEntityType<?>> key) {
        super(key);
    }

    public static <B extends BlockEntity> DeferredBlockEntityType<B> createBlockEntityType(ResourceLocation key) {
        return createBlockEntityType(ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, key));
    }

    public static <B extends BlockEntity> DeferredBlockEntityType<B> createBlockEntityType(ResourceKey<BlockEntityType<?>> key) {
        return new DeferredBlockEntityType<>(key);
    }
}
