package committee.nova.vocalized.common.voice;

import committee.nova.vocalized.api.IVoiceMessageType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class VoiceMessageType implements IVoiceMessageType {

    public VoiceMessageType(ResourceLocation id, ResourceLocation sound) {
        this.id = id;
        this.sound = sound;
    }

    private final ResourceLocation id;
    private final ResourceLocation sound;
    private Component name;

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public Component getName() {
        if (name == null) name = Component.translatable(String.format(
                "vocalized.msg_type.%s.%s",
                getId().getNamespace(),
                getId().getPath()
        ));
        return name;
    }

    @Override
    public ResourceLocation getMessageSound() {
        return sound;
    }
}
