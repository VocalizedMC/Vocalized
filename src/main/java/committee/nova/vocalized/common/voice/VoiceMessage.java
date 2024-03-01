package committee.nova.vocalized.common.voice;

import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.api.IVoiceMessageType;
import committee.nova.vocalized.common.ref.BuiltInVoiceMessageType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class VoiceMessage implements IVoiceMessage {
    private final ResourceLocation id;
    private final IVoiceMessageType messageType;
    private Component name;

    private VoiceMessage(ResourceLocation id, IVoiceMessageType messageType) {
        this.id = id;
        this.messageType = messageType;
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
        return messageType;
    }

    public static Builder builder(ResourceLocation id) {
        return new Builder(id);
    }

    public static class Builder {
        private final ResourceLocation id;
        private IVoiceMessageType messageType = BuiltInVoiceMessageType.RADIO.get();

        private Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder messageType(IVoiceMessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public VoiceMessage build() {
            return new VoiceMessage(id, messageType);
        }
    }
}
