package committee.nova.vocalized.client.util;

import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.api.IVoiceType;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
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

    public static Optional<Component> getVoiceMessageText(
            ResourceLocation vMsg,
            ResourceLocation voiceId,
            ResourceLocation msgId,
            Object... arg
    ) {
        final IVoiceType t = VocalizedRegistry.INSTANCE.getVoiceType(voiceId);
        final IVoiceMessage m = VocalizedRegistry.INSTANCE.getVoiceMessage(msgId);
        if (t != null && m != null) {
            final Optional<Component> c = m.getText(t, arg);
            if (c.isPresent()) return c;
        }
        String key = "v_msg." + vMsg.toString().replace(':', '.');
        if (!I18n.exists(key)) {
            key = "v_msg.default." + msgId.toString().replace(':', '.');
            if (!I18n.exists(key)) return Optional.empty();
        }
        return Optional.of(Component.translatable(key, arg));
    }
}
