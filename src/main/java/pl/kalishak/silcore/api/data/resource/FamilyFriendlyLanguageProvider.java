package pl.kalishak.silcore.api.data.resource;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import pl.kalishak.silcore.api.data.family.ItemFamily;

import java.util.Map;

public abstract class FamilyFriendlyLanguageProvider extends LanguageProvider {
    private static final String NAME_PATTERN = "V#B";
    private static final String DEFAULT_NAME_PATTERN = "B#V";

    protected final Map<BlockFamily.Variant, String> translationPattern = ImmutableMap.<BlockFamily.Variant, String>builder()
            .put(BlockFamily.Variant.CHISELED, NAME_PATTERN)
            .put(BlockFamily.Variant.CRACKED, NAME_PATTERN)
            .put(BlockFamily.Variant.CUT, NAME_PATTERN)
            .put(BlockFamily.Variant.POLISHED, NAME_PATTERN)
            .build();

    public FamilyFriendlyLanguageProvider(PackOutput output, String namespace, String locale) {
        super(output, namespace, locale);
    }

    protected String formatFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    protected void addItemFamily(ItemFamily family) {
        for (var entry : family.getVariants().entrySet()) {
            StringBuilder builder = new StringBuilder();

            boolean firstWord = entry.getKey() == ItemFamily.Variant.RAW;
            String variantKey = formatFirstLetter(entry.getKey().getRecipeGroupPrefix());
            String itemName = formatFirstLetter(family.getName().getPath());

            builder.append(firstWord ? variantKey : itemName);
            builder.append(' ');
            builder.append(firstWord ? itemName : variantKey);

            add(entry.getValue(), builder.toString());
        }
    }

    protected void addBlockFamily(BlockFamily family) {
        for (var entry : family.getVariants().entrySet()) {
            String translationKey = this.translationPattern.getOrDefault(entry.getKey(), DEFAULT_NAME_PATTERN);
            String[] keys = translationKey.split("#");
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < keys.length; i++) {
                if (keys[i].contains("V")) {
                    keys[i] = formatFirstLetter(keys[i].replace("V", entry.getKey().getRecipeGroup()));
                } else if (keys[i].contains("B")) {
                    keys[i] = formatFirstLetter(keys[i].replace("B", BuiltInRegistries.BLOCK.getKey(family.getBaseBlock()).getPath()));
                }

                builder.append(keys[i]);
            }

            add(entry.getValue(), builder.toString());
        }
    }
}
