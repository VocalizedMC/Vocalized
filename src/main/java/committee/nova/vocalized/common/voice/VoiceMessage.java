package committee.nova.vocalized.common.voice;

import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.api.IVoiceMessageType;
import committee.nova.vocalized.common.ref.BuiltInVoiceMessageType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class VoiceMessage implements IVoiceMessage {
    private final ResourceLocation id;
    private Component name;

    public VoiceMessage(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public Component getName() {
        if (name == null) name = Component.translatable(String.format(
                "vocalized.voice_msg.%s.%s",
                getId().getNamespace(),
                getId().getPath()
        ));
        return name;
    }

    @Override
    public IVoiceMessageType getType() {
        return BuiltInVoiceMessageType.RADIO.get();
    }
}
