package committee.nova.vocalized.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.OptionEnum;

import java.util.Optional;

public interface IVoiceType extends OptionEnum {
    ResourceLocation getIdentifier();

    Component getName();

    Optional<ResourceLocation> getVoice(IVoiceMessage msg);

    IVoiceType getDefaultVoiceType();

    default float getVolume(IVoiceMessage msg) {
        return msg.getVolume();
    }

    default float getPitch(IVoiceMessage msg) {
        return msg.getPitch();
    }

    @Override
    default int getId() {
        return 0;// Dummy, this is not a real enum, so it has no actual ordinal.
    }
}
