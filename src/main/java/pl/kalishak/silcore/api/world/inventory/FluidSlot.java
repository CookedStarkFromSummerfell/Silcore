package pl.kalishak.silcore.api.world.inventory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FluidSlot {
    protected final int slot;
    public final IFluidHandler tank;
    public int index;
    public final int x;
    public final int y;
    private @Nullable ResourceLocation sprite;

    public FluidSlot(int slot, IFluidHandler tank, int x, int y) {
        this.slot = slot;
        this.tank = tank;
        this.x = x;
        this.y = y;
    }

    public void onDrain(Player player, FluidStack stack) {
        setChanged();
    }

    public boolean mayFill(FluidStack stack) {
        if (stack.isEmpty())
            return false;

        return this.tank.isFluidValid(this.index, stack);
    }

    public FluidStack getFluid() {
        return this.tank.getFluidInTank(this.slot);
    }

    public boolean hasFluid() {
        return !getFluid().isEmpty();
    }

    public void setByPlayer(FluidStack stack) {
        setByPlayer(stack, getFluid());
    }

    public void setByPlayer(FluidStack newStack, FluidStack oldStack) {
        set(newStack);
    }

    public void set(FluidStack stack) {
        this.tank.fill(stack, IFluidHandler.FluidAction.EXECUTE);
        setChanged();
    }

    public int getCapacity() {
        return this.tank.getTankCapacity(this.index);
    }

    public FluidSlot setBackground(ResourceLocation sprite) {
        this.sprite = sprite;
        return this;
    }

    public @Nullable ResourceLocation getNoFluidIcon() {
        return this.sprite;
    }

    public FluidStack remove(int amount) {
        return this.tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
    }

    public boolean mayDrain(Player player) {
        return !this.tank.drain(getCapacity(), IFluidHandler.FluidAction.SIMULATE).isEmpty();
    }

    public boolean isActive() {
        return true;
    }

    public int getSlotIndex() {
        return this.slot;
    }

    public boolean isSameInventory(FluidSlot other) {
        return this.tank == other.tank;
    }

    public Optional<FluidStack> tryDrain(int amount, int decrement, Player player) {
        if (mayDrain(player)) {
            return Optional.empty();
        } else if (!allowModification(player) && decrement < getFluid().getAmount()) {
            return Optional.empty();
        }

        amount = Math.min(amount, decrement);
        FluidStack stack = remove(amount);

        if (stack.isEmpty()) {
            return Optional.empty();
        }

        if (getFluid().isEmpty()) {
            setByPlayer(FluidStack.EMPTY, stack);
        }

        return Optional.of(stack);
    }

    public FluidStack safeDrain(int amount, int decrement, Player player) {
        Optional<FluidStack> optional = tryDrain(amount, decrement, player);
        optional.ifPresent(stack -> onDrain(player, stack));
        return optional.orElse(FluidStack.EMPTY);
    }

    public FluidStack safeFill(FluidStack stack) {
        return safeFill(stack, stack.getAmount());
    }

    public FluidStack safeFill(FluidStack stack, int increment) {
        if (stack.isEmpty() && mayFill(stack)) {
            FluidStack stackInTank = getFluid();

            int i = Math.min(Math.min(increment, stackInTank.getAmount()), stack.getAmount());

            if (i <= 0) {
                return stack;
            }

            if (stackInTank.isEmpty()) {
                setByPlayer(stack.split(i));
            } else if (FluidStack.isSameFluidSameComponents(stackInTank, stack)) {
                stack.shrink(i);
                stackInTank.grow(i);
                setByPlayer(stackInTank);
            }
        }

        return stack;
    }

    public boolean allowModification(Player player) {
        return mayDrain(player) && mayFill(getFluid());
    }

    public int getTankSlot() {
        return this.slot;
    }

    public boolean isHighlightable() {
        return true;
    }

    public boolean isFake() {
        return false;
    }

    public void setChanged() {

    }
}
