package committee.nova.vocalized.common.ref;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import committee.nova.vocalized.common.voice.VoiceMessage;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Supplier;

public enum BuiltInVoiceMessage implements Supplier<VoiceMessage> {
    BIO;

    private final VoiceMessage msg;

    BuiltInVoiceMessage() {
        final VoiceMessage msg = new VoiceMessage(new ResourceLocation(Vocalized.MODID, this.name().toLowerCase(Locale.ROOT)));
        VocalizedRegistry.INSTANCE.registerVoiceMessage(msg);
        this.msg = msg;
    }

    @Override
    public VoiceMessage get() {
        return msg;
    }

    public static void init() {
    }
}
