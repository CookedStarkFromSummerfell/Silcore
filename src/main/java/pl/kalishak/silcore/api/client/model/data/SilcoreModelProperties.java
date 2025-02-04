package pl.kalishak.silcore.api.client.model.data;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.fluids.FluidStack;
import pl.kalishak.silcore.api.world.material.gas.GasStack;

public final class SilcoreModelProperties {
    public static final ModelProperty<FluidStack> FLUID_IN_TANK = new ModelProperty<>();
    public static final ModelProperty<GasStack> GAS_IN_TANK = new ModelProperty<>();
    public static final ModelProperty<ItemStack> ITEM_IN_CONTAINER = new ModelProperty<>();
}
