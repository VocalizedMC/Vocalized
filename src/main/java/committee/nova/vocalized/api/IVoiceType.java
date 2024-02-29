package committee.nova.vocalized.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IVoiceType {
    ResourceLocation getId();

    Component getName();

    Optional<ResourceLocation> getVoice(IVoiceMessage msg);

    IVoiceType getDefaultVoiceType();

    default float getVolume(IVoiceMessage msg) {
        return msg.getVolume();
    }

    default float getPitch(IVoiceMessage msg) {
        return msg.getPitch();
    }
}
