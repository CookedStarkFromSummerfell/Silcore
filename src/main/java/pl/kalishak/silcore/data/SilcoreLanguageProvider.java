package pl.kalishak.silcore.data;

import net.minecraft.data.PackOutput;
import pl.kalishak.silcore.SilcoreMod;
import pl.kalishak.silcore.api.data.resource.FamilyFriendlyLanguageProvider;
import pl.kalishak.silcore.api.world.level.block.entity.properties.PrivacyProperties;

public class SilcoreLanguageProvider extends FamilyFriendlyLanguageProvider {
    public SilcoreLanguageProvider(PackOutput output) {
        super(output, SilcoreMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("machine.properties.privacy.owner", "Owner: %s");
        add("machine.properties.privacy.shared_access", "Shared access: ");
        add("machine.properties.io.modified", "Modified I/O");
        addPrivacyProperties();
    }

    private void addPrivacyProperties() {
        for (PrivacyProperties.Type type : PrivacyProperties.Type.values()) {
            add("machine.properties.private.access." + type.getSerializedName(), "Access: " + formatFirstLetter(type.getSerializedName()));
        }
    }
}
