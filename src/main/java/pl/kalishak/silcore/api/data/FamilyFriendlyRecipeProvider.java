package pl.kalishak.silcore.api.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.*;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import pl.kalishak.silcore.api.data.family.ItemFamily;

import java.util.Map;
import java.util.stream.Stream;

public abstract class FamilyFriendlyRecipeProvider extends RecipeProvider {
    private static final Map<ItemFamily.Variant, FamilyFriendlyRecipeProvider.FamilyRecipeProvider> ITEM_BUILDERS = ImmutableMap.<ItemFamily.Variant, FamilyFriendlyRecipeProvider.FamilyRecipeProvider>builder()
            .put(ItemFamily.Variant.AXE, FamilyFriendlyRecipeProvider::axeBuilder)
            .put(ItemFamily.Variant.BOOTS, FamilyFriendlyRecipeProvider::bootsBuilder)
            .put(ItemFamily.Variant.CHESTPLATE, FamilyFriendlyRecipeProvider::chestplateBuilder)
            .put(ItemFamily.Variant.HOE, FamilyFriendlyRecipeProvider::hoeBuilder)
            .put(ItemFamily.Variant.HELMET, FamilyFriendlyRecipeProvider::helmetBuilder)
            .put(ItemFamily.Variant.LEGGINGS, FamilyFriendlyRecipeProvider::leggingsBuilder)
            .put(ItemFamily.Variant.NUGGET, FamilyFriendlyRecipeProvider::nuggetBuilder)
            .put(ItemFamily.Variant.PICKAXE, FamilyFriendlyRecipeProvider::pickaxeBuilder)
            .put(ItemFamily.Variant.SHOVEL, FamilyFriendlyRecipeProvider::shovelBuilder)
            .put(ItemFamily.Variant.SWORD, FamilyFriendlyRecipeProvider::swordBuilder)
            .build();

    protected FamilyFriendlyRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    protected void generateForEnabledBlockFamilies(Stream<BlockFamily> blockFamilies, FeatureFlagSet enabledFeatures) {
        blockFamilies.filter(BlockFamily::shouldGenerateRecipe).forEach(blockFamily -> generateRecipes(blockFamily, enabledFeatures));
    }

    protected void generateForEnabledItemFamilies(Stream<ItemFamily> itemFamilies, FeatureFlagSet enabledFeatures) {
        itemFamilies.filter(ItemFamily::shouldGenerateRecipe).forEach(itemFamily -> generateItemRecipes(itemFamily, enabledFeatures));
    }

    protected RecipeBuilder axeBuilder(ItemLike ingredient, ItemLike result) {
        return shaped(RecipeCategory.TOOLS, result)
                .define('#', ingredient)
                .define('S', Items.STICK)
                .pattern("##")
                .pattern("#S")
                .pattern(" S");
    }

    protected RecipeBuilder bootsBuilder(ItemLike ingredient, ItemLike result) {
        return shaped(RecipeCategory.COMBAT, result)
                .define('#', ingredient)
                .pattern("# #")
                .pattern("# #");
    }

    protected RecipeBuilder chestplateBuilder(ItemLike ingredient, ItemLike result) {
        return shaped(RecipeCategory.COMBAT, result)
                .define('#', ingredient)
                .pattern("# #")
                .pattern("###")
                .pattern("###");
    }
    protected RecipeBuilder hoeBuilder(ItemLike ingredient, ItemLike result) {
        return shaped(RecipeCategory.TOOLS, result)
                .define('#', ingredient)
                .define('S', Items.STICK)
                .pattern("##")
                .pattern(" S")
                .pattern(" S");
    }
    protected RecipeBuilder helmetBuilder(ItemLike ingredient, ItemLike result) {
        return shaped(RecipeCategory.COMBAT, result)
                .define('#', ingredient)
                .pattern("###")
                .pattern("# #");
    }

    protected RecipeBuilder leggingsBuilder(ItemLike ingredient, ItemLike result) {
        return shaped(RecipeCategory.COMBAT, result)
                .define('#', ingredient)
                .pattern("###")
                .pattern("# #")
                .pattern("# #");
    }

    protected RecipeBuilder nuggetBuilder(ItemLike ingredient, ItemLike result) {
        return shaped(RecipeCategory.COMBAT, result, 9)
                .define('#', ingredient)
                .pattern("#");
    }

    protected RecipeBuilder pickaxeBuilder(ItemLike ingredient, ItemLike result) {
        return shaped(RecipeCategory.COMBAT, result)
                .define('#', ingredient)
                .define('S', Items.STICK)
                .pattern("###")
                .pattern(" S ")
                .pattern(" S ");
    }

    protected RecipeBuilder shovelBuilder(ItemLike ingredient, ItemLike result) {
        return shaped(RecipeCategory.COMBAT, result)
                .define('#', ingredient)
                .define('S', Items.STICK)
                .pattern("#")
                .pattern("S")
                .pattern("S");
    }

    protected RecipeBuilder swordBuilder(ItemLike ingredient, ItemLike result) {
        return shaped(RecipeCategory.COMBAT, result)
                .define('#', ingredient)
                .define('S', Items.STICK)
                .pattern("#")
                .pattern("#")
                .pattern("S");
    }

    protected void generateItemRecipes(ItemFamily itemFamily, FeatureFlagSet requiredFeatures) {
        itemFamily.getVariants()
                .forEach(((variant, item) -> {
                    if (item.requiredFeatures().isSubsetOf(requiredFeatures)) {
                        FamilyRecipeProvider recipeProvider = ITEM_BUILDERS.get(variant);
                        ItemLike baseItem = itemFamily.getVariants().get(ItemFamily.Variant.INGOT);

                        if (recipeProvider != null) {
                            RecipeBuilder recipeBuilder = recipeProvider.create(this, baseItem, item);
                            itemFamily.getRecipeGroupPrefix().ifPresent(group -> recipeBuilder.group(group + "_" + variant.getRecipeGroupPrefix()));
                            recipeBuilder.unlockedBy(itemFamily.getRecipeUnlockedBy().orElseGet(() -> getHasName(baseItem)), has(baseItem));
                            recipeBuilder.save(this.output);
                        }

                        if (variant == ItemFamily.Variant.RAW) {
                            smeltingResultFromBase(baseItem, item);
                        }
                    }
                }));
    }

    @FunctionalInterface
    protected interface FamilyRecipeProvider {
        RecipeBuilder create(FamilyFriendlyRecipeProvider recipeProvider, ItemLike ingredient, ItemLike result);
    }
}
