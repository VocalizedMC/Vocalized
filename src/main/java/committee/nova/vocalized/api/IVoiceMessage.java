package committee.nova.vocalized.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface IVoiceMessage {
    ResourceLocation getId();

    Component getName();

    IVoiceMessageType getType();

    default float getVolume() {
        return 1.0F;
    }

    default float getPitch() {
        return 1.0F;
    }
}
