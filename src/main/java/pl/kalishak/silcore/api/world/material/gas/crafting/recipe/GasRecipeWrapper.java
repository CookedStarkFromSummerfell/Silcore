package pl.kalishak.silcore.api.world.material.gas.crafting.recipe;

import pl.kalishak.silcore.api.world.material.gas.GasStack;
import pl.kalishak.silcore.api.world.material.gas.IGasTank;

public class GasRecipeWrapper implements GasInput {
    private final IGasTank tank;

    public GasRecipeWrapper(IGasTank tank) {
        this.tank = tank;
    }

    @Override
    public GasStack getGas(int tank) {
        return this.tank.getGasInTank(tank);
    }

    @Override
    public int size() {
        return this.tank.getTanks();
    }
}
