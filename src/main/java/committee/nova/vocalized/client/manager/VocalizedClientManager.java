package committee.nova.vocalized.client.manager;

import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.api.IVoiceMessageType;
import committee.nova.vocalized.api.IVoiceType;
import committee.nova.vocalized.client.util.ClientUtilities;
import committee.nova.vocalized.common.ref.BuiltInVoiceMessageType;
import committee.nova.vocalized.common.ref.BuiltInVoiceType;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import committee.nova.vocalized.common.voice.VoiceEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

public class VocalizedClientManager {
    private static final ConcurrentMap<SoundInstance, ResourceLocation> played = new ConcurrentHashMap<>();

    public static void onReceivedVoiceMsg(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            String senderName,
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
        if (_playVoiceSoundInWorld(voiceId, defaultVoiceId, msgId, voiceOwner, senderName)) return;
        _playNotificationSoundInWorld(voiceId, msgId, msgTypeId, voiceOffset.isRadio(), senderName);
    }

    private static void _playNotificationSoundInWorld(ResourceLocation voiceId, ResourceLocation msgId, ResourceLocation msgTypeId, boolean viaRadio, String senderName) {
        final IVoiceMessageType type = VocalizedRegistry.INSTANCE.getVoiceMessageTypeOrDefault(
                msgTypeId,
                viaRadio ? BuiltInVoiceMessageType.RADIO.get() : BuiltInVoiceMessageType.COMMON.get()
        );
        final ResourceLocation sound = type.getMessageSound();
        if (sound == null) return;
        _playSoundInWorld(sound, voiceId, msgId, 1.0F, 1.0F, Minecraft.getInstance().player, senderName);
    }

    private static boolean _playVoiceSoundInWorld(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, Entity voiceOwner, String senderName
    ) {
        final IVoiceMessage msg = VocalizedRegistry.INSTANCE.getVoiceMessage(msgId);
        if (msg == null) return false;
        final IVoiceType targetType = VocalizedRegistry.INSTANCE.getVoiceType(voiceId);
        boolean useDefault = targetType == null;
        IVoiceType actualType = useDefault ?
                VocalizedRegistry.INSTANCE.getVoiceTypeOrDefault(defaultVoiceId, BuiltInVoiceType.SALLI.get()) :
                targetType;
        if (actualType == null) return false;
        Optional<ResourceLocation> targetSound = actualType.getVoice(msg);
        if (targetSound.isPresent()) {
            if (played.containsValue(targetSound.get())) return false;
            _playSoundInWorld(targetSound.get(), voiceId, msgId, actualType.getVolume(msg), actualType.getPitch(msg), voiceOwner, senderName);
            return true;
        } else if (!useDefault && !defaultVoiceId.equals(voiceId)) {
            actualType = VocalizedRegistry.INSTANCE.getVoiceType(defaultVoiceId);
            targetSound = actualType.getVoice(msg);
            if (targetSound.isEmpty()) return false;
            if (played.containsValue(targetSound.get())) return false;
            _playSoundInWorld(targetSound.get(), voiceId, msgId, actualType.getVolume(msg), actualType.getPitch(msg), voiceOwner, senderName);
            return true;
        }
        return false;
    }

    private static void _playSoundInWorld(ResourceLocation sound, ResourceLocation voiceId, ResourceLocation msgId, float volume, float pitch, Entity voiceOwner, String senderName) {
        if (voiceOwner == null) return;
        final Minecraft mc = Minecraft.getInstance();
        final EntityBoundSoundInstance instance = new EntityBoundSoundInstance(
                SoundEvent.createVariableRangeEvent(sound), SoundSource.PLAYERS, volume, pitch,
                voiceOwner, ThreadLocalRandom.current().nextLong()
        );
        mc.getSoundManager().play(instance);
        played.put(instance, sound);
        ClientUtilities.getVoiceMessageText(sound, voiceId, msgId).ifPresent(c -> mc.gui.getChat().addMessage(Component.translatable(
                "chat.type.text",
                senderName,
                c
        )));
    }


    public static void tick() {
        for (final SoundInstance instance : played.keySet())
            if (!Minecraft.getInstance().getSoundManager().isActive(instance)) played.remove(instance);
    }
}
