package pl.kalishak.silcore.api.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class FunctionalBlockEntity extends SyncedBlockEntity implements MenuProvider, Nameable {
    @Nullable
    private Component name;

    protected FunctionalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected boolean shouldSaveDataOnBreak() {
        return false;
    }

    protected void applyAdditionalComponents(DataComponentInput componentInput) {
    }

    protected void collectAdditionalComponents(DataComponentMap.Builder components) {
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("CustomName", Tag.TAG_STRING)) {
            this.name = parseCustomNameSafe(tag.getString("CustomName"), registries);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (this.name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.name, registries));
        }
    }

    @Override
    public Component getName() {
        return this.name != null ? this.name : getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public @Nullable Component getCustomName() {
        return this.name;
    }

    protected Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    protected final void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);

        if (shouldSaveDataOnBreak()) {
            applyAdditionalComponents(componentInput);
        }
    }

    @Override
    protected final void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.name);

        if (shouldSaveDataOnBreak()) {
            collectAdditionalComponents(components);
        }
    }
}
