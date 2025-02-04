package pl.kalishak.silcore.api.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DeferredRecipeTypeRegister extends DeferredRegister<RecipeType<?>> {
    protected DeferredRecipeTypeRegister(String namespace) {
        super(Registries.RECIPE_TYPE, namespace);
    }

    public static DeferredRecipeTypeRegister createRecipeType(String namespace) {
        return new DeferredRecipeTypeRegister(namespace);
    }

    public <R extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<R>> registerRecipeType(String name) {
        return register(name, () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(getNamespace(), name)));
    }
}
