package pl.kalishak.silcore.api.world.material.gas;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import pl.kalishak.silcore.api.registry.internal.SilcoreRegistries;

import java.util.function.Supplier;

public abstract class Gas implements FeatureElement {
    private final Supplier<Holder<Gas>> builtInRegistryHolder = () -> SilcoreRegistries.GAS.wrapAsHolder(this);
    private final Supplier<? extends Item> container;
    private final String descriptionId;
    private final int density;
    private final boolean canTurnIntoFluid;
    private final FeatureFlagSet requiredFeatures;

    protected Gas(Properties properties) {
        this.container = properties.container;
        this.descriptionId = properties.descriptionId;
        this.density = properties.density;
        this.canTurnIntoFluid = properties.canTurnIntoFluid;
        this.requiredFeatures = properties.requiredFeatures;
    }

    public Holder<Gas> builtInRegistryHolder() {
        return this.builtInRegistryHolder.get();
    }

    public String getDescriptionId() {
        return this.descriptionId;
    }

    public Component getDescriptionId(GasStack gas) {
        return Component.translatable(getDescriptionId());
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.requiredFeatures;
    }

    public static class Properties {
        private Supplier<? extends Item> container;
        private String descriptionId = "";
        private int density = 1000;
        private boolean canTurnIntoFluid = false;
        private FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;

        public Properties() {}

        public Properties container(Supplier<? extends Item> container) {
            this.container = container;
            return this;
        }

        public Properties descriptionId(String descriptionId) {
            this.descriptionId = descriptionId;
            return this;
        }

        public Properties density(int density) {
            this.density = density;
            return this;
        }

        public Properties canTurnIntoFluid() {
            this.canTurnIntoFluid = true;
            return this;
        }

        public Properties requiredFeatures(FeatureFlag... requiredFeatures) {
            this.requiredFeatures = FeatureFlags.REGISTRY.subset(requiredFeatures);
            return this;
        }
    }
}
