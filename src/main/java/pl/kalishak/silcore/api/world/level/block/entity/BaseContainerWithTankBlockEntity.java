package pl.kalishak.silcore.api.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import pl.kalishak.silcore.api.world.inventory.FluidStackHandler;
import pl.kalishak.silcore.api.world.inventory.HandlerUtils;
import pl.kalishak.silcore.api.world.item.component.FluidHandlerComponent;
import pl.kalishak.silcore.api.world.item.component.SilcoreApiDataComponents;

public abstract class BaseContainerWithTankBlockEntity extends BaseContainerBlockEntity {
    private FluidStackHandler tanks = createTanks();

    protected BaseContainerWithTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected FluidStackHandler createTanks() {
        return new FluidStackHandler(getTanks()) {
            @Override
            protected void onContentsChanged() {
                BaseContainerWithTankBlockEntity.this.setChanged();
                super.onContentsChanged();
            }
        };
    }

    protected FluidStackHandler createTanks(NonNullList<FluidStack> tanks) {
        if (tanks == null || tanks.isEmpty()) {
            return createTanks();
        }

        return new FluidStackHandler(tanks);
    }

    protected abstract int getTanks();

    protected int getCapacity() {
        return 16 * FluidType.BUCKET_VOLUME;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (this.tanks == null) {
            this.tanks = createTanks();
        }

        this.tanks.deserializeNBT(registries, tag.getCompound("Tanks"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Tanks", this.tanks.serializeNBT(registries));
    }

    @Override
    protected void applyAdditionalComponents(DataComponentInput componentInput) {
        super.applyAdditionalComponents(componentInput);

        this.tanks = createTanks(componentInput.getOrDefault(SilcoreApiDataComponents.FLUID_HANDLER, FluidHandlerComponent.empty(getTanks())).fluids());
    }

    @Override
    protected void collectAdditionalComponents(DataComponentMap.Builder components) {
        super.collectAdditionalComponents(components);

        components.set(SilcoreApiDataComponents.FLUID_HANDLER, new FluidHandlerComponent(HandlerUtils.copyFluids(this.tanks)));
    }
}
