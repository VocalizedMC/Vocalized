package committee.nova.vocalized.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface IVoiceMessageType {
    ResourceLocation getId();

    Component getName();

    ResourceLocation getMessageSound();
}
