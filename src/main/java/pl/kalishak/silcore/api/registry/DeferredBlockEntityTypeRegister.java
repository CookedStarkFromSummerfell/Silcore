package pl.kalishak.silcore.api.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import pl.kalishak.silcore.api.registry.object.DeferredBlockEntityType;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DeferredBlockEntityTypeRegister extends DeferredRegister<BlockEntityType<?>> {
    protected DeferredBlockEntityTypeRegister(String namespace) {
        super(Registries.BLOCK_ENTITY_TYPE, namespace);
    }

    public static DeferredBlockEntityTypeRegister createBlockEntityType(String namespace) {
        return new DeferredBlockEntityTypeRegister(namespace);
    }

    public <B extends BlockEntity> DeferredBlockEntityType<B> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<? extends B> supplier, boolean hasPermission, Set<Supplier<? extends Block>> validBlocks) {
        register(name, () -> new BlockEntityType<>(supplier, validBlocks.stream().map(Supplier::get).collect(Collectors.toSet()), hasPermission));

        return DeferredBlockEntityType.createBlockEntityType(ResourceLocation.fromNamespaceAndPath(getNamespace(), name));
    }

    public <B extends BlockEntity> DeferredBlockEntityType<B> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<? extends B> supplier, Set<Supplier<? extends Block>> validBlocks) {
        return registerBlockEntity(name, supplier, false, validBlocks);
    }

    public <B extends BlockEntity> DeferredBlockEntityType<B> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<? extends B> supplier, boolean hasPermission, Block... validBlocks) {
        register(name, () -> new BlockEntityType<>(supplier, hasPermission, validBlocks));

        return DeferredBlockEntityType.createBlockEntityType(ResourceLocation.fromNamespaceAndPath(getNamespace(), name));
    }

    public <B extends BlockEntity> DeferredBlockEntityType<B> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<? extends B> supplier, Block... validBlocks) {
        return registerBlockEntity(name, supplier, false, validBlocks);
    }
}
