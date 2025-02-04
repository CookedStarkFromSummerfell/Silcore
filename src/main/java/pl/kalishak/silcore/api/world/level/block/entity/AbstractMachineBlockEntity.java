package pl.kalishak.silcore.api.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import pl.kalishak.silcore.api.world.item.component.MachinePropertiesComponent;
import pl.kalishak.silcore.api.world.item.component.SilcoreApiDataComponents;
import pl.kalishak.silcore.api.world.level.block.entity.properties.MachineProperties;

public abstract class AbstractMachineBlockEntity extends SyncedBlockEntity implements MenuProvider {
    protected final MachineProperties machineProperties = new MachineProperties() {
        @Override
        public void setChanged() {
            AbstractMachineBlockEntity.this.setChanged();
            super.setChanged();
        }
    };
    protected final ItemStackHandler inventory;

    public AbstractMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.inventory = createInventory();
    }

    protected ItemStackHandler createInventory() {
        return new ItemStackHandler(getInventorySize()) {
            @Override
            protected void onContentsChanged(int slot) {
                AbstractMachineBlockEntity.this.setChanged();
                super.onContentsChanged(slot);
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("Inventory", this.inventory.serializeNBT(registries));
        tag.put("MachineProperties", this.machineProperties.save(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        this.machineProperties.load(tag.getCompound("MachineProperties"), registries);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(SilcoreApiDataComponents.MACHINE_PROPERTIES, new MachinePropertiesComponent(this.machineProperties));
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.machineProperties.copyFrom(componentInput.getOrDefault(SilcoreApiDataComponents.MACHINE_PROPERTIES, MachinePropertiesComponent.EMPTY).properties());
    }

    public abstract int getInventorySize();

    public abstract @Nullable IFluidHandler getTanks();

    public boolean hasInventory() {
        return true;
    }

    public IItemHandler getInventory() {
        return this.inventory;
    }

    public MachineProperties getMachineProperties() {
        return this.machineProperties;
    }
}
