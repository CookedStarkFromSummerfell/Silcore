package pl.kalishak.silcore.api.world.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidStackHandler implements IFluidHandler, INBTSerializable<CompoundTag> {
    protected NonNullList<FluidStack> fluids;
    protected NonNullList<Integer> capacityPerTank;

    public FluidStackHandler() {
        this(1);
    }

    public FluidStackHandler(int size) {
        this.fluids = NonNullList.withSize(size, FluidStack.EMPTY);
        this.capacityPerTank = NonNullList.withSize(size, FluidType.BUCKET_VOLUME);
    }

    public FluidStackHandler(NonNullList<FluidStack> fluids) {
        this.fluids = fluids;
        this.capacityPerTank = NonNullList.withSize(fluids.size(), FluidType.BUCKET_VOLUME);
    }

    public void setSize(int size) {
        this.fluids = NonNullList.withSize(size, FluidStack.EMPTY);
        this.capacityPerTank = NonNullList.withSize(size, FluidType.BUCKET_VOLUME);
    }

    public void setTankCapacity(int tank, int capacity) {
        this.capacityPerTank.set(tank, capacity);
    }

    public void setFluidInTank(int tank, FluidStack stack) {
        this.fluids.set(tank, stack);
    }

    @Override
    public int getTanks() {
        return this.fluids.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        validateIndex(tank);
        return this.fluids.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.capacityPerTank.get(tank);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int tankSlot = findValidTank(true, resource);

        if (resource.isEmpty() || tankSlot == -1) {
            return 0;
        }

        FluidStack stackInTank = getFluidInTank(tankSlot).copy();
        final int tankCapacity = getTankCapacity(tankSlot);

        if (action.simulate()) {
            if (stackInTank.isEmpty()) {
                return Math.min(tankCapacity, resource.getAmount());
            }

            if (!FluidStack.isSameFluidSameComponents(stackInTank, resource)) {
                return 0;
            }

            return Math.min(tankCapacity - stackInTank.getAmount(), resource.getAmount());
        }

        if (stackInTank.isEmpty()) {
            stackInTank = resource.copyWithAmount(Math.min(tankCapacity, resource.getAmount()));
            onContentsChanged();

            return stackInTank.getAmount();
        }

        if (!FluidStack.isSameFluidSameComponents(stackInTank, resource)) {
            return 0;
        }

        int filled = tankCapacity - stackInTank.getAmount();

        if (resource.getAmount() < filled) {
            stackInTank.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            stackInTank.setAmount(tankCapacity);
        }

        if (filled > 0) {
            setFluidInTank(tankSlot, stackInTank);
            onContentsChanged();
        }

        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        int tankSlot = findValidTank(false, resource);

        if (tankSlot == -1 || resource.isEmpty() || !FluidStack.isSameFluidSameComponents(getFluidInTank(tankSlot), resource)) {
            return FluidStack.EMPTY;
        }

        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int tankSlot = findValidTank(false, FluidStack.EMPTY);

        if (tankSlot == -1) {
            return FluidStack.EMPTY;
        }

        int drained = maxDrain;

        FluidStack stackInTank = getFluidInTank(tankSlot);

        if (stackInTank.getAmount() < drained) {
            drained = stackInTank.getAmount();
        }

        FluidStack stack = stackInTank.copyWithAmount(drained);

        if (action.execute() && drained > 0) {
            stack.shrink(drained);
            setFluidInTank(tankSlot, stack);
            onContentsChanged();
        }

        return stack;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        FluidStack stackInTank = getFluidInTank(tank);

        return stackInTank.isEmpty() || (FluidStack.isSameFluid(stack, stackInTank) && stackInTank.getAmount() < stack.getAmount());
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        ListTag list = new ListTag();

        for (int i = 0; i < this.fluids.size(); i++) {
            if (this.fluids.get(i).isEmpty()) {
                CompoundTag fluid = new CompoundTag();
                fluid.putInt("Tank", i);

                if (this.capacityPerTank.get(i) != FluidType.BUCKET_VOLUME) {
                    fluid.putInt("Capacity", this.capacityPerTank.get(i));
                }

                list.add(this.fluids.get(i).save(provider));
            }
        }

        CompoundTag fluidsTag = new CompoundTag();
        fluidsTag.put("Fluids", list);
        fluidsTag.putInt("Size", this.fluids.size());

        return fluidsTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : this.fluids.size());
        ListTag tagList = nbt.getList("Fluids", Tag.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag fluidTag = tagList.getCompound(i);
            int slot = fluidTag.getInt("Tank");

            boolean hasCapacity = fluidTag.contains("Capacity", Tag.TAG_INT);

            if (slot >= 0 && slot < this.fluids.size()) {
                FluidStack.parse(provider, fluidTag).ifPresent(stack -> {
                    this.fluids.set(slot, stack);

                    if (hasCapacity) {
                        this.capacityPerTank.set(slot, fluidTag.getInt("Capacity"));
                    }
                });
            }
        }

        onLoad();
    }

    protected boolean canFill(int tank, FluidStack stack) {
        return true;
    }

    protected boolean canDrain(int tank, FluidStack stack) {
        return true;
    }

    protected void onLoad() {

    }

    protected void onContentsChanged() {

    }

    private int findValidTank(boolean input, FluidStack resource) {
        for (int i = 0; i < this.fluids.size(); i++) {
            if (input && this.isFluidValid(i, resource)) {
                return i;
            } else if (!input) {
                if (!getFluidInTank(i).isEmpty() || resource.isEmpty()) {
                    return i;
                }
            }
        }

        return -1;
    }

    private void validateIndex(int tank) {
        if (tank < 0 || tank >= this.fluids.size()) {
            throw new IllegalArgumentException("Tank index " + tank + " not valid in range: <0, " + this.fluids.size() + ")");
        }
    }
}
