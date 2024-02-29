package committee.nova.vocalized.init;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.common.ref.BuiltInVoiceMessage;
import committee.nova.vocalized.common.ref.BuiltInVoiceMessageType;
import committee.nova.vocalized.common.ref.BuiltInVoiceType;
import committee.nova.vocalized.common.ref.SoundEventRef;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Vocalized.MODID);

    public static void init(IEventBus bus) {
        SOUNDS.register(bus);
        BuiltInVoiceType.init();
        BuiltInVoiceMessage.init();
        BuiltInVoiceMessageType.init();
        //if (FMLEnvironment.dist.isClient()) ClientRegistryHandler.initSound();
        SoundEventRef.init();
    }
}
