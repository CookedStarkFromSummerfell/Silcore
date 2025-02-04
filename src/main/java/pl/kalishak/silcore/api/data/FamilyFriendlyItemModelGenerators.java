package pl.kalishak.silcore.api.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import pl.kalishak.silcore.api.data.family.ItemFamily;

import java.util.Map;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class FamilyFriendlyItemModelGenerators {
    private final ItemModelGenerators itemModelGenerators;
    protected final Map<ItemFamily.Variant, ModelProvider> SHAPE_CONSUMERS = ImmutableMap.<ItemFamily.Variant, ModelProvider>builder()
            .put(ItemFamily.Variant.AXE, this::handheldItem)
            .put(ItemFamily.Variant.BOOTS, this::trimmableItem)
            .put(ItemFamily.Variant.CHESTPLATE, this::trimmableItem)
            .put(ItemFamily.Variant.HOE, this::handheldItem)
            .put(ItemFamily.Variant.HELMET, this::trimmableItem)
            .put(ItemFamily.Variant.HORSE_ARMOR, this::flatItem)
            .put(ItemFamily.Variant.INGOT, this::flatItem)
            .put(ItemFamily.Variant.LEGGINGS, this::trimmableItem)
            .put(ItemFamily.Variant.NUGGET, this::flatItem)
            .put(ItemFamily.Variant.RAW, this::flatItem)
            .put(ItemFamily.Variant.PICKAXE, this::handheldItem)
            .put(ItemFamily.Variant.SHOVEL, this::handheldItem)
            .put(ItemFamily.Variant.SWORD, this::handheldItem)
            .build();

    public FamilyFriendlyItemModelGenerators(ItemModelGenerators itemModelGenerators) {
        this.itemModelGenerators = itemModelGenerators;
    }

    protected void handheldItem(ItemModelGenerators itemModelGenerators, Item item) {
        itemModelGenerators.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    protected void flatItem(ItemModelGenerators itemModelGenerators, Item item) {
        itemModelGenerators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }

    protected void trimmableItem(ItemModelGenerators itemModelGenerators, Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        String[] rawId = id.getPath().split("_");

        itemModelGenerators.generateTrimmableItem(
                item,
                ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), rawId[0])),
                rawId[1],
                false
        );
    }

    public void generateEnabledFamilies(Stream<ItemFamily> families, FeatureFlagSet requiredFeatures) {
        families.filter(ItemFamily::shouldGenerateModel).forEach(family -> generateForFamily(family, requiredFeatures));
    }

    public void generateFamilies(Stream<ItemFamily> families) {
        families.filter(ItemFamily::shouldGenerateModel).forEach(this::generateForFamily);
    }

    protected void generateForFamily(ItemFamily itemFamily, FeatureFlagSet requiredFeatures) {
        itemFamily.getVariants().forEach(((variant, item) -> {
            if (item.requiredFeatures().isSubsetOf(requiredFeatures)) {
                ModelProvider modelProvider = SHAPE_CONSUMERS.get(variant);

                if (modelProvider != null) {
                    modelProvider.generate(this.itemModelGenerators, item);
                }
            }
        }));
    }

    protected void generateForFamily(ItemFamily itemFamily) {
        itemFamily.getVariants().forEach(((variant, item) -> {
            ModelProvider modelProvider = SHAPE_CONSUMERS.get(variant);

            if (modelProvider != null) {
                modelProvider.generate(this.itemModelGenerators, item);
            }
        }));
    }

    @OnlyIn(Dist.CLIENT)
    public interface ModelProvider {
        void generate(ItemModelGenerators itemModelGenerators, Item item);
    }
}
