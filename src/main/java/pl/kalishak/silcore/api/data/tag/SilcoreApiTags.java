package pl.kalishak.silcore.api.data.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import pl.kalishak.silcore.SilcoreMod;

public final class SilcoreApiTags {
    public static class Items {
        private static TagKey<Item> itemTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(SilcoreMod.MOD_ID, name));
        }

        private static TagKey<Item> sharedItemTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        public static final TagKey<Item> WRENCH = sharedItemTag("tools/wrench");
    }
}
