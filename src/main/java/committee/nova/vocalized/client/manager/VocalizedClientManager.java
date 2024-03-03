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
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

public class VocalizedClientManager {
    private static final ConcurrentMap<SoundInstance, ResourceLocation> played = new ConcurrentHashMap<>();

    public static void onReceivedVoiceMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            Component senderName,
            VoiceEffect voiceOffset, ResourceKey<Level> dimension, int entityId
    ) {
        onReceivedVoiceMsgEntityBound(
                voiceId, defaultVoiceId,
                msgId, msgTypeId,
                true, senderName,
                voiceOffset, dimension, entityId
        );
    }

    public static void onReceivedVoiceMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            VoiceEffect voiceOffset, ResourceKey<Level> dimension, int entityId
    ) {
        onReceivedVoiceMsgEntityBound(
                voiceId, defaultVoiceId,
                msgId, msgTypeId,
                false, Component.empty(),
                voiceOffset, dimension, entityId
        );
    }

    private static void onReceivedVoiceMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            boolean sendText, Component senderName,
            VoiceEffect voiceOffset, ResourceKey<Level> dimension, int entityId
    ) {
        final Minecraft mc = Minecraft.getInstance();
        final Entity voiceOwner;
        final boolean isSelf = voiceOffset.overDimension();
        if (isSelf) {
            voiceOwner = Minecraft.getInstance().player;
        } else {
            final Level level = mc.level;
            if (level == null) return;
            if (!level.dimension().equals(dimension)) return;
            voiceOwner = level.getEntity(entityId);
        }
        if (voiceOwner == null) return;
        if (_playVoiceSoundInWorldEntityBound(voiceId, defaultVoiceId, msgId, voiceOwner, senderName, sendText)) return;
        _playNotificationSoundInWorld(voiceId, msgId, msgTypeId, senderName, sendText);
    }

    public static void onReceivedVoiceMsgPosBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            Component senderName,
            VoiceEffect voiceOffset, ResourceKey<Level> dimension, Vec3 pos
    ) {
        onReceivedVoiceMsgPosBound(
                voiceId, defaultVoiceId,
                msgId, msgTypeId,
                true, senderName,
                dimension, pos
        );
    }

    public static void onReceivedVoiceMsgPosBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            VoiceEffect voiceOffset, ResourceKey<Level> dimension, Vec3 pos
    ) {
        onReceivedVoiceMsgPosBound(
                voiceId, defaultVoiceId,
                msgId, msgTypeId,
                false, Component.empty(),
                dimension, pos
        );
    }

    private static void onReceivedVoiceMsgPosBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            boolean sendText, Component senderName,
            ResourceKey<Level> dimension, Vec3 pos
    ) {
        final Minecraft mc = Minecraft.getInstance();
        final Level level = mc.level;
        if (level == null) return;
        if (!level.dimension().equals(dimension)) return;
        if (_playVoiceSoundInWorldPosBound(voiceId, defaultVoiceId, msgId, pos, senderName, sendText)) return;
        _playNotificationSoundInWorld(voiceId, msgId, msgTypeId, senderName, sendText);
    }

    private static void _playNotificationSoundInWorld(
            ResourceLocation voiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            Component senderName, boolean sendText
    ) {
        final IVoiceMessageType type = VocalizedRegistry.INSTANCE.getVoiceMessageTypeOrDefault(
                msgTypeId,
                BuiltInVoiceMessageType.COMMON.get()
        );
        final ResourceLocation sound = type.getMessageSound();
        if (sound == null) return;
        _playSoundInWorldEntityBound(sound, voiceId, msgId, 1.0F, 1.0F, Minecraft.getInstance().player, senderName, sendText);
    }

    private static boolean _playVoiceSoundInWorldEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, Entity voiceOwner, Component senderName, boolean sendText
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
            _playSoundInWorldEntityBound(
                    targetSound.get(),
                    voiceId, msgId,
                    actualType.getVolume(msg), actualType.getPitch(msg),
                    voiceOwner, senderName, sendText);
            return true;
        } else if (!useDefault && !defaultVoiceId.equals(voiceId)) {
            actualType = VocalizedRegistry.INSTANCE.getVoiceType(defaultVoiceId);
            targetSound = actualType.getVoice(msg);
            if (targetSound.isEmpty()) return false;
            if (played.containsValue(targetSound.get())) return false;
            _playSoundInWorldEntityBound(
                    targetSound.get(),
                    voiceId, msgId,
                    actualType.getVolume(msg), actualType.getPitch(msg),
                    voiceOwner, senderName, sendText
            );
            return true;
        }
        return false;
    }

    private static boolean _playVoiceSoundInWorldPosBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, Vec3 pos, Component senderName, boolean sendText
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
            _playSoundInWorldPosBound(
                    targetSound.get(),
                    voiceId, msgId,
                    actualType.getVolume(msg), actualType.getPitch(msg),
                    pos, senderName, sendText
            );
            return true;
        } else if (!useDefault && !defaultVoiceId.equals(voiceId)) {
            actualType = VocalizedRegistry.INSTANCE.getVoiceType(defaultVoiceId);
            targetSound = actualType.getVoice(msg);
            if (targetSound.isEmpty()) return false;
            if (played.containsValue(targetSound.get())) return false;
            _playSoundInWorldPosBound(
                    targetSound.get(),
                    voiceId, msgId,
                    actualType.getVolume(msg), actualType.getPitch(msg),
                    pos, senderName, sendText
            );
            return true;
        }
        return false;
    }

    private static void _playSoundInWorldEntityBound(ResourceLocation sound, ResourceLocation voiceId, ResourceLocation msgId, float volume, float pitch, Entity voiceOwner, Component senderName, boolean sendText) {
        if (voiceOwner == null) return;
        final Minecraft mc = Minecraft.getInstance();
        final EntityBoundSoundInstance instance = new EntityBoundSoundInstance(
                SoundEvent.createVariableRangeEvent(sound), SoundSource.PLAYERS, volume, pitch,
                voiceOwner, ThreadLocalRandom.current().nextLong()
        );
        mc.getSoundManager().play(instance);
        played.put(instance, sound);
        if (!sendText) return;
        ClientUtilities.getVoiceMessageText(sound, voiceId, msgId).ifPresent(c -> mc.gui.getChat().addMessage(
                Component.translatable(
                        "chat.type.text",
                        senderName,
                        c
                )
        ));
    }

    private static void _playSoundInWorldPosBound(ResourceLocation sound, ResourceLocation voiceId, ResourceLocation msgId, float volume, float pitch, Vec3 pos, Component senderName, boolean sendText) {
        final Minecraft mc = Minecraft.getInstance();
        final SimpleSoundInstance instance = new SimpleSoundInstance(
                sound, SoundSource.PLAYERS, volume, pitch, RandomSource.create(), false, 0, SoundInstance.Attenuation.LINEAR, pos.x, pos.y, pos.z, true
        );
        mc.getSoundManager().play(instance);
        played.put(instance, sound);
        if (!sendText) return;
        ClientUtilities.getVoiceMessageText(sound, voiceId, msgId).ifPresent(c -> mc.gui.getChat().addMessage(
                Component.translatable(
                        "chat.type.text",
                        senderName,
                        c
                )
        ));
    }


    public static void tick() {
        for (final SoundInstance instance : played.keySet())
            if (!Minecraft.getInstance().getSoundManager().isActive(instance)) played.remove(instance);
    }
}
