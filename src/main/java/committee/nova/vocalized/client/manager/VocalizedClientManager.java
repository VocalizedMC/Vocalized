package committee.nova.vocalized.client.manager;

import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.api.IVoiceMessageType;
import committee.nova.vocalized.api.IVoiceType;
import committee.nova.vocalized.common.ref.BuiltInVoiceMessageType;
import committee.nova.vocalized.common.ref.BuiltInVoiceType;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import committee.nova.vocalized.common.voice.VoiceEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

public class VocalizedClientManager {
    private static long lastPlayedTime = -1;
    private static final ConcurrentMap<SoundInstance, ResourceLocation> played = new ConcurrentHashMap<>();

    public static void onReceivedVoiceMsg(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            VoiceEffect voiceOffset, boolean isSelf, int entityId
    ) {
        final Entity voiceOwner;
        if (isSelf) {
            voiceOwner = Minecraft.getInstance().player;
        } else {
            final Level level = Minecraft.getInstance().level;
            if (level == null) return;
            voiceOwner = level.getEntity(entityId);
        }
        if (voiceOwner == null) return;
        if (_playVoiceSoundInWorld(voiceId, defaultVoiceId, msgId, voiceOwner)) {
            //lastPlayedTime = currentTime;
            return;
        }
        _playNotificationSoundInWorld(msgTypeId, voiceOffset.isRadio());
    }

    private static void _playNotificationSoundInWorld(ResourceLocation msgTypeId, boolean viaRadio) {
        final IVoiceMessageType type = VocalizedRegistry.INSTANCE.getVoiceMessageTypeOrDefault(
                msgTypeId,
                viaRadio ? BuiltInVoiceMessageType.RADIO.get() : BuiltInVoiceMessageType.COMMON.get()
        );
        final ResourceLocation sound = type.getMessageSound();
        if (sound == null) return;
        _playSoundInWorld(sound, 1.0F, 1.0F, Minecraft.getInstance().player);
    }

    private static boolean _playVoiceSoundInWorld(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, Entity voiceOwner
    ) {
        final IVoiceMessage msg = VocalizedRegistry.INSTANCE.getVoiceMessage(msgId);
        if (msg == null) return false;
        final IVoiceType targetType = VocalizedRegistry.INSTANCE.getVoiceType(voiceId);
        boolean useDefault = targetType == null;
        IVoiceType actualType = useDefault ?
                VocalizedRegistry.INSTANCE.getVoiceTypeOrDefault(defaultVoiceId, BuiltInVoiceType.BUILTIN_FEMALE.get()) :
                targetType;
        if (actualType == null) return false;
        Optional<ResourceLocation> targetSound = actualType.getVoice(msg);
        if (targetSound.isPresent()) {
            if (played.containsValue(targetSound.get())) return false;
            _playSoundInWorld(targetSound.get(), actualType.getVolume(msg), actualType.getPitch(msg), voiceOwner);
            return true;
        } else if (!useDefault) {
            actualType = VocalizedRegistry.INSTANCE.getVoiceType(defaultVoiceId);
            targetSound = actualType.getVoice(msg);
            if (targetSound.isEmpty()) return false;
            if (played.containsValue(targetSound.get())) return false;
            _playSoundInWorld(targetSound.get(), actualType.getVolume(msg), actualType.getPitch(msg), voiceOwner);
            return true;
        }
        return false;
    }

    private static void _playSoundInWorld(ResourceLocation sound, float volume, float pitch, Entity voiceOwner) {
        if (voiceOwner == null) return;
        final Minecraft minecraft = Minecraft.getInstance();
        final EntityBoundSoundInstance instance = new EntityBoundSoundInstance(
                SoundEvent.createVariableRangeEvent(sound), SoundSource.PLAYERS, volume, pitch,
                voiceOwner, ThreadLocalRandom.current().nextLong()
        );
        minecraft.getSoundManager().play(instance);
        played.put(instance, sound);
    }

    public static SimpleSoundInstance getUISound(ResourceLocation sound, float volume, float pitch) {
        return new SimpleSoundInstance(
                sound, SoundSource.MASTER, volume, pitch,
                RandomSource.create(), false, 0, SoundInstance.Attenuation.NONE,
                0.0D, 0.0D, 0.0D, true
        );
    }

    public static void tick() {
        for (final SoundInstance instance : played.keySet())
            if (!Minecraft.getInstance().getSoundManager().isActive(instance)) played.remove(instance);
    }
}
