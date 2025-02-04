package pl.kalishak.silcore.api.data.family;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class ItemFamily {
    private final ResourceLocation name;
    private final Map<ItemFamily.Variant, Item> variants = Maps.newHashMap();
    private boolean generateModel = true;
    private boolean generateRecipes = true;
    @Nullable
    private String recipeGroupPrefix;
    @Nullable
    private String recipeUnlockedBy;
    private FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;

    private ItemFamily(ResourceLocation name) {
        this.name = name;
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public Map<Variant, Item> getVariants() {
        return this.variants;
    }

    public boolean shouldGenerateModel() {
        return this.generateModel;
    }

    public boolean shouldGenerateRecipe() {
        return this.generateRecipes;
    }

    public Optional<String> getRecipeGroupPrefix() {
        return StringUtil.isBlank(this.recipeGroupPrefix) ? Optional.empty() : Optional.of(this.recipeGroupPrefix);
    }

    public Optional<String> getRecipeUnlockedBy() {
        return StringUtil.isBlank(this.recipeUnlockedBy) ? Optional.empty() : Optional.of(this.recipeUnlockedBy);
    }

    public boolean isEnabled(FeatureFlagSet requiredFeatures) {
        return this.requiredFeatures == null || this.requiredFeatures.isSubsetOf(requiredFeatures);
    }

    public static class Builder {
        private final ItemFamily itemFamily;

        public Builder(ResourceLocation name) {
            this.itemFamily = new ItemFamily(name);
        }

        public Builder axe(Item axe) {
            this.itemFamily.variants.put(Variant.AXE, axe);
            return this;
        }

        public Builder boots(Item boots) {
            this.itemFamily.variants.put(Variant.BOOTS, boots);
            return this;
        }

        public Builder chestplate(Item chestplate) {
            this.itemFamily.variants.put(Variant.CHESTPLATE, chestplate);
            return this;
        }

        public Builder hoe(Item hoe) {
            this.itemFamily.variants.put(Variant.HOE, hoe);
            return this;
        }

        public Builder helmet(Item helmet) {
            this.itemFamily.variants.put(Variant.HELMET, helmet);
            return this;
        }

        public Builder horseArmor(Item horseArmor) {
            this.itemFamily.variants.put(Variant.HORSE_ARMOR, horseArmor);
            return this;
        }

        public Builder ingot(Item ingot) {
            this.itemFamily.variants.put(Variant.INGOT, ingot);
            return this;
        }

        public Builder leggings(Item leggings) {
            this.itemFamily.variants.put(Variant.LEGGINGS, leggings);
            return this;
        }

        public Builder nugget(Item nugget) {
            this.itemFamily.variants.put(Variant.NUGGET, nugget);
            return this;
        }

        public Builder raw(Item raw) {
            this.itemFamily.variants.put(Variant.RAW, raw);
            return this;
        }

        public Builder pickaxe(Item pickaxe) {
            this.itemFamily.variants.put(Variant.PICKAXE, pickaxe);
            return this;
        }

        public Builder shovel(Item shovel) {
            this.itemFamily.variants.put(Variant.SHOVEL, shovel);
            return this;
        }

        public Builder sword(Item sword) {
            this.itemFamily.variants.put(Variant.SWORD, sword);
            return this;
        }

        public Builder noRecipeGen() {
            this.itemFamily.generateRecipes = false;
            return this;
        }

        public Builder noModelGen() {
            this.itemFamily.generateModel = false;
            return this;
        }

        public Builder requiredFeatures(FeatureFlag... requiredFeatures) {
            this.itemFamily.requiredFeatures = FeatureFlags.REGISTRY.subset(requiredFeatures);
            return this;
        }

        public Builder recipeGroupPrefix(String recipeGroupPrefix) {
            this.itemFamily.recipeGroupPrefix = recipeGroupPrefix;
            return this;
        }

        public Builder recipeUnlockedBy(String recipeUnlockedBy) {
            this.itemFamily.recipeUnlockedBy = recipeUnlockedBy;
            return this;
        }

        public ItemFamily build() {
            return this.itemFamily;
        }
    }

    public enum Variant {
        AXE("axe"),
        BOOTS("boots"),
        CHESTPLATE("chestplate"),
        HOE("hoe"),
        HELMET("helmet"),
        HORSE_ARMOR("horse_armor"),
        INGOT("ingot"),
        LEGGINGS("leggings"),
        NUGGET("nugget"),
        RAW("raw"),
        PICKAXE("pickaxe"),
        SHOVEL("shovel"),
        SWORD("sword");

        private final String name;

        Variant(String name) {
            this.name = name;
        }

        public String getRecipeGroupPrefix() {
            return this.name;
        }
    }
}
