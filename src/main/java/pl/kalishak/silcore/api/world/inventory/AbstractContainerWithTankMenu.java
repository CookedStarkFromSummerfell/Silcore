package pl.kalishak.silcore.api.world.inventory;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import pl.kalishak.silcore.api.world.level.block.entity.AbstractMachineBlockEntity;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Supplier;

public abstract class AbstractContainerWithTankMenu<M extends AbstractMachineBlockEntity> extends AbstractContainerMenu {
    private final NonNullList<FluidStack> lastFluidSlots = NonNullList.create();
    public final NonNullList<FluidSlot> fluidSlots = NonNullList.create();
    private final List<DataSlot> fluidDataSlots = Lists.newArrayList();
    private final NonNullList<FluidStack> remoteFluidSlots = NonNullList.create();
    private final IntList remoteFluidDataSlots = new IntArrayList();
    private final List<TankListener> tankListeners = Lists.newArrayList();
    @Nullable
    private TankSynchronizer synchronizer;
    private boolean suppressRemoteUpdates;

    protected final M machine;
    protected final IItemHandler itemHandler;
    protected final IFluidHandler fluidHandler;

    protected AbstractContainerWithTankMenu(@Nullable MenuType<?> menuType, int containerId, M machine) {
        super(menuType, containerId);
        this.machine = machine;
        this.itemHandler = machine.getInventory();
        this.fluidHandler = machine.getTanks();
    }

    protected static void checkTankSize(IFluidHandler fluidHandler, int minSize) {
        int size = fluidHandler.getTanks();

        if (size < minSize) {
            throw new IllegalArgumentException("Tank size" + size + " is smaller than expected " + minSize);
        }
    }

    public boolean isValidFluidSlotIndex(int slotIndex) {
        return slotIndex == -1 || slotIndex == -999 || slotIndex < this.fluidSlots.size();
    }

    protected FluidSlot addTankSlot(FluidSlot fluidSlot) {
        fluidSlot.index = this.fluidSlots.size();
        this.fluidSlots.add(fluidSlot);
        this.lastFluidSlots.add(FluidStack.EMPTY);
        this.remoteFluidSlots.add(FluidStack.EMPTY);

        return fluidSlot;
    }

    protected DataSlot addFluidDataSlot(DataSlot dataSlot) {
        this.fluidDataSlots.add(dataSlot);
        this.remoteFluidDataSlots.add(0);

        return dataSlot;
    }

    protected void addFluidDataSlots(ContainerData array) {
        for (int i = 0; i < array.getCount(); i++) {
            addFluidDataSlot(DataSlot.forContainer(array, i));
        }
    }

    public void addTankListener(TankListener listener) {
        if (!this.tankListeners.contains(listener)) {
            this.tankListeners.add(listener);
            broadcastTankChanges();
        }
    }

    public void setTankSynchronizer(TankSynchronizer synchronizer) {
        this.synchronizer = synchronizer;
        sendAllFluidDataToRemote();
    }

    public void sendAllFluidDataToRemote() {
        int i = 0;

        for (int j = this.fluidSlots.size(); i < j; i++) {
            this.remoteFluidSlots.set(i, this.fluidSlots.get(i).getFluid());
        }

        setRemoteCarried(getCarried());
        i = 0;

        for (int k = this.fluidDataSlots.size(); i < k; i++) {
            this.remoteFluidDataSlots.set(i, this.fluidDataSlots.get(i).get());
        }

        if (this.synchronizer != null) {
            this.synchronizer.sendInitialData(this, this.remoteFluidSlots, this.remoteFluidDataSlots.toIntArray());
        }
    }

    public void removeSlotListener(TankListener listener) {
        this.tankListeners.remove(listener);
    }

    public NonNullList<FluidStack> getFluids() {
        NonNullList<FluidStack> fluids = NonNullList.create();

        for (FluidSlot slot : this.fluidSlots) {
            fluids.add(slot.getFluid());
        }

        return fluids;
    }


    public void broadcastTankChanges() {
        for (int i = 0; i < this.fluidSlots.size(); i++) {
            FluidStack fluidStack = this.fluidSlots.get(i).getFluid();
            Supplier<FluidStack> supplier = Suppliers.memoize(fluidStack::copy);
            triggerFluidSlotListeners(i, fluidStack, supplier);
            synchronizeFluidSlotToRemote(i, fluidStack, supplier);
        }

        synchronizeCarriedToRemote();

        for (int j = 0; j < this.fluidDataSlots.size(); j++) {
            DataSlot dataSlot = this.fluidDataSlots.get(j);
            int k = dataSlot.get();

            if (dataSlot.checkAndClearUpdateFlag()) {
                updateFluidDataSlotListeners(j, k);
            }

            synchronizeFluidDataSlotToRemote(j, k);
        }
    }

    public void broadcastTankFullState() {
        for (int i = 0; i < this.fluidSlots.size(); i++) {
            FluidStack fluidStack = this.fluidSlots.get(i).getFluid();
            triggerFluidSlotListeners(i, fluidStack, fluidStack::copy);
        }

        for (int j = 0; j < this.fluidDataSlots.size(); j++) {
            DataSlot dataslot = this.fluidDataSlots.get(j);

            if (dataslot.checkAndClearUpdateFlag()) {
                updateFluidDataSlotListeners(j, dataslot.get());
            }
        }

        sendAllFluidDataToRemote();
    }

    private void updateFluidDataSlotListeners(int slotIndex, int value) {
        for (TankListener tankListener : this.tankListeners) {
            tankListener.dataChanged(this, slotIndex, value);
        }
    }

    private void triggerFluidSlotListeners(int slotIndex, FluidStack fluidStack, Supplier<FluidStack> supplier) {
        FluidStack lastStack = this.lastFluidSlots.get(slotIndex);

        if (!FluidStack.matches(fluidStack, lastStack)) {
            FluidStack memoizedStack = supplier.get();
            this.lastFluidSlots.set(slotIndex, memoizedStack);

            for (TankListener tankListener : this.tankListeners) {
                tankListener.tankChanged(this, slotIndex, memoizedStack);
            }
        }
    }

    private void synchronizeFluidSlotToRemote(int slotIndex, FluidStack stack, Supplier<FluidStack> supplier) {
        if (!this.suppressRemoteUpdates) {
            FluidStack fluidStack = this.remoteFluidSlots.get(slotIndex);

            if (!FluidStack.matches(fluidStack, stack)) {
                FluidStack memoizedStack = supplier.get();
                this.remoteFluidSlots.set(slotIndex, memoizedStack);

                if (this.synchronizer != null) {
                    this.synchronizer.sendSlotChange(this, slotIndex, memoizedStack);
                }
            }
        }
    }

    private void synchronizeFluidDataSlotToRemote(int slotIndex, int value) {
        if (!this.suppressRemoteUpdates) {
            int i = this.remoteFluidDataSlots.getInt(slotIndex);

            if (i != value) {
                this.remoteFluidDataSlots.set(slotIndex, value);

                if (this.synchronizer != null) {
                    this.synchronizer.sendDataChange(this, slotIndex, value);
                }
            }
        }
    }

    public void setRemoteFluidSlot(int slotIndex, FluidStack fluidStack) {
        this.remoteFluidSlots.set(slotIndex, fluidStack.copy());
    }

    public void setRemoteFluidSlotNoCopy(int slot, FluidStack stack) {
        if (slot >= 0 && slot < this.remoteFluidSlots.size()) {
            this.remoteFluidSlots.set(slot, stack);
        }
    }

    public FluidSlot getTankSlot(int index) {
        return this.fluidSlots.get(index);
    }

    @Override
    protected void doClick(int slotId, int button, ClickType clickType, Player player) {
        if (!(button == InputConstants.MOUSE_BUTTON_LEFT || button == InputConstants.MOUSE_BUTTON_RIGHT)) {
            return;
        }

        FluidSlot fluidTank = this.fluidSlots.get(slotId);
        FluidStack stackInSlot = fluidTank.getFluid();
        ItemStack carried = getCarried();

        if (clickType == ClickType.PICKUP) {
            if (!getCarried().isEmpty()) {
                if (slotId < 0) {
                    super.doClick(slotId, button, clickType, player);
                }

                if (!carried.isEmpty()) {
                    IFluidHandler bucket = carried.getCapability(Capabilities.FluidHandler.ITEM);

                    if (bucket != null) {
                        FluidStack fluidInBucket = bucket.getFluidInTank(0);

                        if ((stackInSlot.isEmpty() && !fluidInBucket.isEmpty()) || FluidStack.isSameFluid(stackInSlot, fluidInBucket)) {
                            fluidTank.setByPlayer(bucket.drain(bucket.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE));

                            if (!FluidStack.matches(fluidInBucket, fluidTank.getFluid())) {
                                broadcastTankChanges();
                            }

                        } else if (!stackInSlot.isEmpty() && fluidInBucket.isEmpty()) {
                            FluidStack newStack = stackInSlot.copy();
                            newStack.setAmount(bucket.fill(stackInSlot, IFluidHandler.FluidAction.EXECUTE));

                            if (!FluidStack.matches(fluidInBucket, newStack)) {
                                fluidTank.setByPlayer(newStack, stackInSlot);
                                fluidTank.onDrain(player, newStack);
                                broadcastTankChanges();
                            }
                        }
                    }
                }
            }
        } else if (clickType == ClickType.CLONE) {
            if (fluidTank.hasFluid() && carried.is(Items.BUCKET)) {
                FluidStack stack = fluidTank.getFluid();

                setCarried(stack.getFluidType().getBucket(stack));
            }
        }
    }

    @Override
    protected void clearContainer(Player player, Container container) {
        this.fluidSlots.clear();
        super.clearContainer(player, container);
    }

    public int getTankSlotCapacity(int index) {
        return this.fluidHandler.getTankCapacity(index);
    }

    public void setFluid(int tankSlotId, FluidStack stack) {
        getTankSlot(tankSlotId).set(stack);
    }


    public void setFluidData(int id, int data) {
        this.fluidDataSlots.get(id).set(data);
    }

    @Override
    public void transferState(AbstractContainerMenu menu) {
        if (menu instanceof AbstractContainerWithTankMenu<?> tankMenu) {
            Table<IFluidHandler, Integer, Integer> table = HashBasedTable.create();
            int i;

            for (i = 0; i < this.fluidSlots.size(); i++) {
                FluidSlot slot = this.fluidSlots.get(i);
                table.put(slot.tank, slot.getTankSlot(), i);
            }

            for (i = 0; i < this.fluidSlots.size(); i++) {
                FluidSlot slot = this.fluidSlots.get(i);
                Integer value = table.get(slot.tank, slot.getTankSlot());

                if (value != null) {
                    this.lastFluidSlots.set(i, tankMenu.lastFluidSlots.get(value));
                    this.remoteFluidSlots.set(i, tankMenu.remoteFluidSlots.get(value));
                }
            }
        }

        super.transferState(menu);
    }

    public OptionalInt findFluidSlot(IFluidHandler tank, int slotIndex) {
        for (int i = 0; i < this.fluidSlots.size(); i++) {
            FluidSlot slot = this.fluidSlots.get(i);
            if (slot.tank == tank && slotIndex == slot.slot) {
                return OptionalInt.of(i);
            }
        }

        return OptionalInt.empty();
    }
}
