package pl.kalishak.silcore.api.world.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import pl.kalishak.silcore.api.world.level.block.entity.properties.MachineProperties;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record MachinePropertiesComponent(MachineProperties properties) {
    public static final MachinePropertiesComponent EMPTY = new MachinePropertiesComponent(new MachineProperties());
    public static final Codec<MachinePropertiesComponent> CODEC = MachineProperties.CODEC
            .xmap(MachinePropertiesComponent::new, MachinePropertiesComponent::properties);
    public static final StreamCodec<RegistryFriendlyByteBuf, MachinePropertiesComponent> STREAM_CODEC = MachineProperties.STREAM_CODEC
            .map(MachinePropertiesComponent::new, MachinePropertiesComponent::properties);

    private Component getOwnerName(Level level) {
        String ownerId = this.properties.privacyProperties().getOwnerId();

        if (!ownerId.isEmpty()) {
            Player player = level.getPlayerByUUID(UUID.fromString(ownerId));

            if (player != null) {
                return player.getDisplayName();
            }
        }

        return Component.empty();
    }

    public Component getOwnerTooltip(Level level) {
        Component ownerName = getOwnerName(level);

        return ownerName.getString().isEmpty() ? Component.empty() : Component.translatable("machine.properties.privacy.owner", ownerName);
    }

    public List<Component> getSharedAccess(Level level) {
        List<Component> playerNames = properties().privacyProperties().getSharedAccess().stream().map(uuid -> {
            Player player = level.getPlayerByUUID(uuid);
            if (player != null) {
                return player.getDisplayName();
            } else {
                return Component.empty();
            }
        }).filter(Objects::nonNull).filter(component -> !component.getString().isEmpty()).toList();

        playerNames.addFirst(Component.translatable("machine.properties.privacy.shared_access"));

        return playerNames;
    }
}
