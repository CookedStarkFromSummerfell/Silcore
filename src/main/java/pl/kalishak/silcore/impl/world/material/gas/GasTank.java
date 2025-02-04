package pl.kalishak.silcore.impl.world.material.gas;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.api.world.material.gas.IGasTank;

public class GasTank implements IGasTank, INBTSerializable<CompoundTag> {
    private NonNullList<GasStack> tanks;
    private float pressureForTank = 1.0F;
    private int tankCapacity = 1000;

    public GasTank(NonNullList<GasStack> tanks) {
        this.tanks = tanks;
    }

    public GasTank(int count) {
        this.tanks = NonNullList.withSize(count, GasStack.EMPTY);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        ListTag list = new ListTag();

        for (int i = 0; i < this.tanks.size(); i++) {
            if (!this.tanks.get(i).isEmpty()) {
                CompoundTag tag = new CompoundTag();
                tag.putInt("Index", i);
                list.add(this.tanks.get(i).save(provider, tag));
            }
        }

        CompoundTag tag = new CompoundTag();
        tag.put("Tanks", list);
        tag.putInt("Size", getTanks());

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.tanks = NonNullList.withSize(nbt.getInt("Size"), GasStack.EMPTY);
        ListTag listTag = nbt.getList("Tanks", Tag.TAG_COMPOUND);

        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag tag = listTag.getCompound(i);
            int index = tag.getInt("Index");

            if (index >= 0 && index < this.tanks.size()) {
                GasStack.parse(provider, tag).ifPresent(stack -> this.tanks.set(index, stack));
            }
        }

        onLoad();
    }

    @Override
    public int getTanks() {
        return this.tanks.size();
    }

    @Override
    public GasStack getGasInTank(int index) {
        return this.tanks.get(index);
    }

    @Override
    public float getMaxPressure(int index) {
        return this.pressureForTank;
    }

    @Override
    public int getTankCapacity(int index) {
        return this.tankCapacity;
    }

    @Override
    public boolean doesOverpressureDamageTank(int index) {
        return false;
    }

    @Override
    public boolean isGasValid(int index, GasStack stack) {
        return !isFilled(index) && GasStack.matches(getGasInTank(index), stack);
    }

    @Override
    public int fill(GasStack resource, float withPressure, boolean simulate) {
        return 0;
    }

    @Override
    public GasStack drain(GasStack resource, float withPressure, boolean simulate) {
        return null;
    }

    @Override
    public GasStack drain(int maxDrain, float minPressure, boolean simulate) {
        return null;
    }

    protected boolean isFilled(int index) {
        GasStack stack = getGasInTank(index);

        return !stack.isEmpty() && stack.getPressure() == this.pressureForTank;
    }

    protected void onLoad() {

    }

    protected void setChanged(int tank) {}
}
