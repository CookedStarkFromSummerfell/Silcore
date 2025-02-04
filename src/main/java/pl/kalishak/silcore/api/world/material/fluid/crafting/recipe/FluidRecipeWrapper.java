package pl.kalishak.silcore.api.world.material.fluid.crafting.recipe;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidRecipeWrapper implements FluidInput {
    private final IFluidHandler fluidHandler;

    public FluidRecipeWrapper(IFluidHandler fluidHandler) {
        this.fluidHandler = fluidHandler;
    }

    @Override
    public FluidStack getFluid(int tank) {
        return this.fluidHandler.getFluidInTank(tank);
    }

    @Override
    public int size() {
        return this.fluidHandler.getTanks();
    }
}
