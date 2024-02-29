package committee.nova.vocalized.client.manager;

import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.api.IVoiceMessageType;
import committee.nova.vocalized.api.IVoiceType;
import committee.nova.vocalized.common.ref.BuiltInVoiceMessageType;
import committee.nova.vocalized.common.ref.BuiltInVoiceType;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import committee.nova.vocalized.common.voice.VoiceOffset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class VocalizedClientManager {
    private static long lastPlayedTime = -1;
    public static IVoiceType using = BuiltInVoiceType.DEFAULT.get();

    public static void onReceivedVoiceMsg(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            Vec3 pos, VoiceOffset voiceOffset
    ) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        final long currentTime = System.currentTimeMillis();
        final Vec3 targetPos = voiceOffset.offset(pos, player.getEyePosition());
        if (currentTime - lastPlayedTime < 1000) {
            playNotificationSound(msgTypeId, targetPos, voiceOffset.isRadio());
            return;
        }
        if (playVoiceSound(voiceId, defaultVoiceId, msgId, targetPos)) {
            lastPlayedTime = currentTime;
            return;
        }
        playNotificationSound(msgTypeId, targetPos, voiceOffset.isRadio());
    }

    public static void playNotificationSound(ResourceLocation msgTypeId, Vec3 pos, boolean viaRadio) {
        final IVoiceMessageType type = VocalizedRegistry.INSTANCE.getVoiceMessageTypeOrDefault(
                msgTypeId,
                viaRadio ? BuiltInVoiceMessageType.RADIO.get() : BuiltInVoiceMessageType.COMMON.get()
        );
        final ResourceLocation sound = type.getMessageSound();
        if (sound == null) return;
        playSound(pos, sound, 1.0F, 1.0F, false);
    }

    public static boolean playVoiceSound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, Vec3 pos
    ) {
        final IVoiceMessage msg = VocalizedRegistry.INSTANCE.getVoiceMessage(msgId);
        if (msg == null) return false;
        final IVoiceType targetType = VocalizedRegistry.INSTANCE.getVoiceType(voiceId);
        boolean useDefault = targetType == null;
        IVoiceType actualType = useDefault ?
                VocalizedRegistry.INSTANCE.getVoiceTypeOrDefault(defaultVoiceId, BuiltInVoiceType.DEFAULT.get()) :
                targetType;
        if (actualType == null) return false;
        Optional<ResourceLocation> targetSound = actualType.getVoice(msg);
        if (targetSound.isPresent()) {
            playSound(pos, targetSound.get(), actualType.getVolume(msg), actualType.getPitch(msg), false);
            return true;
        } else if (!useDefault) {
            actualType = VocalizedRegistry.INSTANCE.getVoiceType(defaultVoiceId);
            targetSound = actualType.getVoice(msg);
            if (targetSound.isEmpty()) return false;
            playSound(pos, targetSound.get(), actualType.getVolume(msg), actualType.getPitch(msg), false);
            return true;
        }
        return false;
    }

    private static void playSound(Vec3 pos, ResourceLocation sound, float volume, float pitch, boolean delay) {
        final Minecraft minecraft = Minecraft.getInstance();
        double d0 = minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(pos.x, pos.y, pos.z);
        final SimpleSoundInstance simplesoundinstance = new SimpleSoundInstance(
                sound, SoundSource.PLAYERS, volume, pitch, RandomSource.create(),
                false, 0, SoundInstance.Attenuation.LINEAR,
                pos.x, pos.y, pos.z, false
        );
        if (delay && d0 > 100.0D) {
            final double d1 = Math.sqrt(d0) / 40.0D;
            minecraft.getSoundManager().playDelayed(simplesoundinstance, (int) (d1 * 20.0));
        } else minecraft.getSoundManager().play(simplesoundinstance);
    }
}
