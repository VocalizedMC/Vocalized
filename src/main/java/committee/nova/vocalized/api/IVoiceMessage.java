package committee.nova.vocalized.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IVoiceMessage {
    ResourceLocation getId();

    Optional<Component> getText(IVoiceType type, Object... arg);

    IVoiceMessageType getType();

    default float getVolume() {
        return 1.0F;
    }

    default float getPitch() {
        return 1.0F;
    }
}
