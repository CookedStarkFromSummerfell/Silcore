package pl.kalishak.silcore.api.world.level.block.entity;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;
import pl.kalishak.silcore.api.crafting.MachineRecipe;
import pl.kalishak.silcore.api.crafting.MixedRecipeInput;

import java.util.List;
import java.util.Objects;

public abstract class AbstractProcessingMachineBlockEntity<T extends MixedRecipeInput> extends AbstractMachineBlockEntity implements RecipeCraftingHolder, StackedContentsCompatible {
    protected final RecipeType<? extends MachineRecipe<T>> recipeType;
    protected final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap<>();
    protected final RecipeManager.CachedCheck<T, ? extends MachineRecipe<T>> quickCheck;
    
    protected AbstractProcessingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, RecipeType<? extends MachineRecipe<T>> recipeType) {
        super(type, pos, blockState);
        this.recipeType = recipeType;
        this.quickCheck = RecipeManager.createCheck(recipeType);
    }

    @Override
    public @Nullable RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player player, List<ItemStack> items) {
        
    }
    
    protected final NonNullList<ItemStack> getItemsCopy() {
        NonNullList<ItemStack> items = NonNullList.withSize(getInventorySize(), ItemStack.EMPTY);
        
        for (int i = 0; i < getInventorySize(); i++) {
            items.set(i, this.inventory.getStackInSlot(i).copy());
        }
        
        return items;
    }
    
    public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
        List<RecipeHolder<?>> recipes = getRecipesToAwardAndPopExperience(player.serverLevel(), player.position());
        player.awardRecipes(recipes);
        
        recipes.stream().filter(Objects::nonNull).forEach(recipe -> player.triggerRecipeCrafted(recipe, getItemsCopy()));
        
        this.recipesUsed.clear();
    }
    
    private List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVector) {
        List<RecipeHolder<?>> recipes = Lists.newArrayList();
        
        this.recipesUsed.reference2IntEntrySet().forEach(entry -> {
            level.recipeAccess().byKey(entry.getKey()).ifPresent(recipe -> {
                recipes.add(recipe);
                createExperience(level, popVector, entry.getIntValue(), ((MachineRecipe<?>) recipe.value()).experience());
            });
        });

        return recipes;
    }

    private static void createExperience(ServerLevel level, Vec3 popVec, int recipeIndex, float experience) {
        int i = Mth.floor((float)recipeIndex * experience);
        float f = Mth.frac((float)recipeIndex * experience);

        if (f != 0.0F && Math.random() < (double) f) {
            i++;
        }

        ExperienceOrb.award(level, popVec, i);
    }

    @Override
    public void fillStackedContents(StackedItemContents stackedContents) {
        for (int i = 0; i < getInventorySize(); i++) {
            stackedContents.accountStack(this.inventory.getStackInSlot(i));
        }
    }
}
