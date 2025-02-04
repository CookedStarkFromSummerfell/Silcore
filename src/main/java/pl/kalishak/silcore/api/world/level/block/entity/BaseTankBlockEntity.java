package pl.kalishak.silcore.api.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import pl.kalishak.silcore.api.client.model.data.SilcoreModelProperties;
import pl.kalishak.silcore.api.world.item.component.SilcoreApiDataComponents;

public abstract class BaseTankBlockEntity extends FunctionalBlockEntity {
    private FluidTank tank = createTank();

    private ModelData modelData = ModelData.EMPTY;

    public BaseTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected FluidTank createTank() {
        return new FluidTank(getCapacity()) {
            @Override
            protected void onContentsChanged() {
                BaseTankBlockEntity.this.setChanged();
                super.onContentsChanged();
            }
        };
    }

    protected FluidTank createTank(FluidStack fluidStack) {
        FluidTank tank = createTank();
        tank.setFluid(fluidStack);

        return tank;
    }

    protected int getCapacity() {
        return 16 * FluidType.BUCKET_VOLUME;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (this.tank == null) {
            this.tank = createTank();
        }

        this.tank.readFromNBT(registries, tag.getCompound("Tank"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Tank", this.tank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void applyAdditionalComponents(DataComponentInput componentInput) {
        this.tank = createTank(componentInput.getOrDefault(SilcoreApiDataComponents.FLUID_STACK, FluidStack.EMPTY));
    }

    @Override
    protected void collectAdditionalComponents(DataComponentMap.Builder components) {
        components.set(SilcoreApiDataComponents.FLUID_STACK, this.tank.getFluid());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.modelData = this.modelData
                .derive()
                .with(SilcoreModelProperties.FLUID_IN_TANK, this.tank.getFluid())
                .build();
    }

    @Override
    public ModelData getModelData() {
        return this.modelData;
    }
}
