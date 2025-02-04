package pl.kalishak.silcore.api.registry.internal;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import pl.kalishak.silcore.api.world.level.block.entity.properties.IOType;
import pl.kalishak.silcore.api.world.material.gas.Gas;
import pl.kalishak.silcore.SilcoreMod;
import pl.kalishak.silcore.api.world.material.gas.crafting.ingredient.GasIngredientType;

public final class SilcoreRegistries {
    public static final Registry<Gas> GAS = createSyncedRegistry(Keys.GAS);
    public static final Registry<GasIngredientType<?>> GAS_INGREDIENT_TYPES = createSyncedRegistry(Keys.GAS_INGREDIENT_TYPES);
    public static final Registry<IOType<?>> IO_TYPE = createSyncedRegistry(Keys.IO_TYPE);

    private static <R> Registry<R> createSyncedRegistry(ResourceKey<? extends Registry<R>> key) {
        return new RegistryBuilder<>(key).sync(true).create();
    }

    public static void newRegistries(final NewRegistryEvent event) {
        event.register(GAS);
        event.register(GAS_INGREDIENT_TYPES);
        event.register(IO_TYPE);
    }

    public static class Keys {
        private static <R> ResourceKey<Registry<R>> createRegistryKey(String registryKey) {
            return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(SilcoreMod.MOD_ID, registryKey));
        }

        public static final ResourceKey<Registry<Gas>> GAS = createRegistryKey("gas");
        public static final ResourceKey<Registry<GasIngredientType<?>>> GAS_INGREDIENT_TYPES = createRegistryKey("gas_ingredient_type");
        public static final ResourceKey<Registry<IOType<?>>> IO_TYPE = createRegistryKey("io_type");
    }
}
