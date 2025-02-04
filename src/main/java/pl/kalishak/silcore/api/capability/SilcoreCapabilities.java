package pl.kalishak.silcore.api.capability;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;
import pl.kalishak.silcore.SilcoreMod;
import pl.kalishak.silcore.api.world.material.gas.IGasTank;

@SuppressWarnings("unused")
public class SilcoreCapabilities {
    private SilcoreCapabilities() {}

    private static ResourceLocation create(String name) {
        return ResourceLocation.fromNamespaceAndPath(SilcoreMod.MOD_ID, name);
    }

    public static final class GasHandler {
        public static final BlockCapability<IGasTank, @Nullable Direction> BLOCK = BlockCapability.createSided(create("gas_tank"), IGasTank.class);
        public static final EntityCapability<IGasTank, @Nullable Direction> ENTITY = EntityCapability.createSided(create("gas_tank"), IGasTank.class);
        public static final ItemCapability<IGasTank, @Nullable Void> ITEM = ItemCapability.createVoid(create("gas_tank"), IGasTank.class);

        private GasHandler() {}
    }
}
