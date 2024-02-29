package committee.nova.vocalized.common.ref;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.init.RegistryHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;
import java.util.function.Supplier;

public enum SoundEventRef implements Supplier<SoundEvent> {
    MSG_COMMON,
    MSG_RADIO;

    SoundEventRef() {
        final String path = this.name().toLowerCase(Locale.ROOT);
        this.reg = RegistryHandler.SOUNDS.register(
                this.name().toLowerCase(Locale.ROOT),
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Vocalized.MODID, path))
        );
    }

    private final RegistryObject<SoundEvent> reg;

    @Override
    public SoundEvent get() {
        return reg.get();
    }

    public static void init() {
    }
}
