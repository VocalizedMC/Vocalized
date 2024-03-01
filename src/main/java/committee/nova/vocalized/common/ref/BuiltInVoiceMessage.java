package committee.nova.vocalized.common.ref;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import committee.nova.vocalized.common.voice.VoiceMessage;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public enum BuiltInVoiceMessage implements Supplier<VoiceMessage> {
    BIO(VoiceMessage.builder(new ResourceLocation(Vocalized.MODID, "bio"))
            .messageType(BuiltInVoiceMessageType.COMMON.get())
            .build());

    private final VoiceMessage obj;

    BuiltInVoiceMessage(VoiceMessage obj) {
        VocalizedRegistry.INSTANCE.registerVoiceMessage(obj);
        this.obj = obj;
    }

    @Override
    public VoiceMessage get() {
        return obj;
    }

    public static void init() {
    }
}
