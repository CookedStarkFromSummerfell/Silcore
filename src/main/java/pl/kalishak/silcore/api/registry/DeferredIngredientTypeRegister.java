package pl.kalishak.silcore.api.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import pl.kalishak.silcore.api.world.material.gas.crafting.ingredient.GasIngredient;
import pl.kalishak.silcore.api.world.material.gas.crafting.ingredient.GasIngredientType;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;

import java.util.function.BiFunction;

public class DeferredIngredientTypeRegister<T> extends DeferredRegister<T> {
    protected DeferredIngredientTypeRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        super(registryKey, namespace);
    }

    public static DeferredIngredientTypeRegister.Item createItemIngredient(String namespace) {
        return new DeferredIngredientTypeRegister.Item(namespace);
    }

    public static DeferredIngredientTypeRegister.Fluid createFluidIngredient(String namespace) {
        return new DeferredIngredientTypeRegister.Fluid(namespace);
    }

    public static DeferredIngredientTypeRegister.Gas createGasIngredient(String namespace) {
        return new DeferredIngredientTypeRegister.Gas(namespace);
    }

    public <R extends T, I> DeferredHolder<T, R> registerType(String name, BiFunction<MapCodec<I>, StreamCodec<RegistryFriendlyByteBuf, I>, R> func, MapCodec<I> mapCodec, StreamCodec<RegistryFriendlyByteBuf, I> streamCodec) {
        return register(name, () -> func.apply(mapCodec, streamCodec));
    }

    public static class Fluid extends DeferredIngredientTypeRegister<FluidIngredientType<?>> {
        protected Fluid(String namespace) {
            super(NeoForgeRegistries.Keys.FLUID_INGREDIENT_TYPES, namespace);
        }

        public <F extends FluidIngredient> DeferredHolder<FluidIngredientType<?>, FluidIngredientType<F>> registerFluidIngredientType(String name, MapCodec<F> mapCodec, StreamCodec<RegistryFriendlyByteBuf, F> streamCodec) {
            return register(name, () -> new FluidIngredientType<>(mapCodec, streamCodec));
        }

        public <F extends FluidIngredient> DeferredHolder<FluidIngredientType<?>, FluidIngredientType<F>> registerFluidIngredientType(String name, MapCodec<F> mapCodec) {
            return register(name, () -> new FluidIngredientType<>(mapCodec));
        }
    }

    public static class Gas extends DeferredIngredientTypeRegister<GasIngredientType<?>> {
        protected Gas(String namespace) {
            super(SilcoreRegistries.Keys.GAS_INGREDIENT_TYPES, namespace);
        }

        public <G extends GasIngredient> DeferredHolder<GasIngredientType<?>, GasIngredientType<G>> registerGasIngredientType(String name, MapCodec<G> mapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {
            return register(name, () -> new GasIngredientType<>(mapCodec, streamCodec));
        }

        public <G extends GasIngredient> DeferredHolder<GasIngredientType<?>, GasIngredientType<G>> registerGasIngredientType(String name, MapCodec<G> mapCodec) {
            return register(name, () -> new GasIngredientType<>(mapCodec));
        }
    }

    public static class Item extends DeferredIngredientTypeRegister<IngredientType<?>> {
        protected Item(String namespace) {
            super(NeoForgeRegistries.Keys.INGREDIENT_TYPES, namespace);
        }

        public <I extends ICustomIngredient> DeferredHolder<IngredientType<?>, IngredientType<I>> registerIngredientType(String name, MapCodec<I> mapCodec, StreamCodec<RegistryFriendlyByteBuf, I> streamCodec) {
            return register(name, () -> new IngredientType<>(mapCodec, streamCodec));
        }

        public <I extends ICustomIngredient> DeferredHolder<IngredientType<?>, IngredientType<I>> registerIngredientType(String name, MapCodec<I> mapCodec) {
            return register(name, () -> new IngredientType<>(mapCodec));
        }
    }
}
