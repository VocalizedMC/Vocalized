package committee.nova.vocalized.common.ref;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.api.IVoiceType;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import committee.nova.vocalized.common.voice.VoiceType;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Supplier;

public enum BuiltInVoiceType implements Supplier<IVoiceType> {
    MUTE(VoiceType.MUTE),
    MATTHEW,
    SALLI,
    ZHI_YU;

    BuiltInVoiceType() {
        this(null);
    }

    BuiltInVoiceType(IVoiceType type) {
        if (type == null)
            type = new VoiceType(new ResourceLocation(Vocalized.MODID, this.name().toLowerCase(Locale.ROOT)));
        VocalizedRegistry.INSTANCE.registerVoiceType(type);
        this.type = type;
    }

    private final IVoiceType type;

    @Override
    public IVoiceType get() {
        return type;
    }

    public static void init() {
    }
}
