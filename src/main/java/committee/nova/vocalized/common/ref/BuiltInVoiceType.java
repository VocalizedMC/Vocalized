package committee.nova.vocalized.common.ref;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import committee.nova.vocalized.common.voice.VoiceType;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Supplier;

public enum BuiltInVoiceType implements Supplier<VoiceType> {
    BUILTIN_FEMALE,
    BUILTIN_MALE;

    BuiltInVoiceType() {
        final VoiceType type = new VoiceType(new ResourceLocation(Vocalized.MODID, this.name().toLowerCase(Locale.ROOT)));
        VocalizedRegistry.INSTANCE.registerVoiceType(type);
        this.type = type;
    }

    private final VoiceType type;

    @Override
    public VoiceType get() {
        return type;
    }

    public static void init() {
    }

    ;
}
