package committee.nova.vocalized.common.ref;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import committee.nova.vocalized.common.voice.VoiceMessageType;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Supplier;

public enum BuiltInVoiceMessageType implements Supplier<VoiceMessageType> {
    COMMON,
    RADIO;

    BuiltInVoiceMessageType() {
        final String name = this.name().toLowerCase(Locale.ROOT);
        final VoiceMessageType type = new VoiceMessageType(
                new ResourceLocation(Vocalized.MODID, "msg_type_" + name),
                new ResourceLocation(Vocalized.MODID, "msg_" + name)
        );
        VocalizedRegistry.INSTANCE.registerVoiceMessageType(type);
        this.type = type;
    }

    private final VoiceMessageType type;

    @Override
    public VoiceMessageType get() {
        return type;
    }

    public static void init() {
    }
}
