package committee.nova.vocalized.api;

import net.minecraft.resources.ResourceLocation;

public interface IVocal {
    ResourceLocation vocalized$getVoiceId();

    ResourceLocation vocalized$getDefaultVoiceId();

    void vocalized$setVoiceId(ResourceLocation id);

    void vocalized$setDefaultVoiceId(ResourceLocation id);
}
