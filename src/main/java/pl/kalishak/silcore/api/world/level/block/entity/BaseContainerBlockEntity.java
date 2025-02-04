package pl.kalishak.silcore.api.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import pl.kalishak.silcore.api.world.inventory.HandlerUtils;
import pl.kalishak.silcore.api.world.item.component.ItemHandlerComponent;
import pl.kalishak.silcore.api.world.item.component.SilcoreApiDataComponents;

public abstract class BaseContainerBlockEntity extends FunctionalBlockEntity {
    private ItemStackHandler inventory = createInventory();

    protected BaseContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected abstract int getInventorySize();

    protected ItemStackHandler createInventory() {
        return new ItemStackHandler(getInventorySize()) {
            @Override
            protected void onContentsChanged(int slot) {
                BaseContainerBlockEntity.this.setChanged();
                super.onContentsChanged(slot);
            }
        };
    }

    protected final ItemStackHandler createInventory(@Nullable NonNullList<ItemStack> inventory) {
        if (inventory == null) {
            return createInventory();
        }

        return new ItemStackHandler(inventory) {
            @Override
            protected void onContentsChanged(int slot) {
                BaseContainerBlockEntity.this.setChanged();
                super.onContentsChanged(slot);
            }
        };
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (this.inventory == null) {
            this.inventory = createInventory();
        }

        this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", this.inventory.serializeNBT(registries));
    }

    @Override
    protected void collectAdditionalComponents(DataComponentMap.Builder components) {
        components.set(SilcoreApiDataComponents.ITEM_HANDLER, new ItemHandlerComponent(HandlerUtils.copyItems(this.inventory)));
    }

    @Override
    protected void applyAdditionalComponents(DataComponentInput componentInput) {
        this.inventory = createInventory(componentInput.getOrDefault(SilcoreApiDataComponents.ITEM_HANDLER, ItemHandlerComponent.empty(getInventorySize())).items());
    }
}
