package pl.kalishak.silcore.api.world.material.gas.crafting.display;

import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.api.world.material.gas.GasStack;

public interface ForGasStacks<T> extends DisplayContentsFactory<T> {
    /**
     * {@return display data for the given gas holder}
     *
     * @param gas Gas holder to display.
     */
    default T forStack(Holder<Gas> gas) {
        return forStack(new GasStack(gas, 1.0F, 1000));
    }

    /**
     * {@return display data for the given gas stack}
     *
     * @param gas GasStack to display
     */
    T forStack(GasStack gas);
}
