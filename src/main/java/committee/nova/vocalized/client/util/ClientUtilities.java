package committee.nova.vocalized.client.util;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.Optional;

public class ClientUtilities {
    public static SimpleSoundInstance getUISound(ResourceLocation sound, float volume, float pitch) {
        return new SimpleSoundInstance(
                sound, SoundSource.MASTER, volume, pitch,
                RandomSource.create(), false, 0, SoundInstance.Attenuation.NONE,
                0.0D, 0.0D, 0.0D, true
        );
    }

    public static Optional<Component> getVoiceMessageText(ResourceLocation voice, ResourceLocation msg, Object... arg) {
        String key = "msg.voice_message." + voice.toString().replace(':', '.');
        if (!I18n.exists(key)) {
            key = "msg.voice_message.default." + msg.toString().replace(':', '.');
            if (!I18n.exists(key)) return Optional.empty();
        }
        return Optional.of(Component.translatable(key, arg));
    }
}
