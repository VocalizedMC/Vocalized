package committee.nova.vocalized.common.network.msg;

import committee.nova.vocalized.client.manager.VocalizedClientManager;
import committee.nova.vocalized.common.voice.VoiceEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CVocalizedMsgEntityBound {
    private final ResourceLocation voiceId;
    private final ResourceLocation defaultVoiceId;
    private final ResourceLocation messageId;
    private final ResourceLocation messageTypeId;
    private final boolean sendText;
    private final Component senderName;
    private final VoiceEffect voiceEffect;
    private final ResourceKey<Level> dimension;
    private final int entityId;


    public S2CVocalizedMsgEntityBound(FriendlyByteBuf buf) {
        this.voiceId = buf.readResourceLocation();
        this.defaultVoiceId = buf.readResourceLocation();
        this.messageId = buf.readResourceLocation();
        this.messageTypeId = buf.readResourceLocation();
        this.sendText = buf.readBoolean();
        this.senderName = sendText ? buf.readComponent() : Component.empty();
        this.voiceEffect = VoiceEffect.getByOrdinal(buf.readByte());
        this.dimension = !voiceEffect.overDimension() ? buf.readResourceKey(Registries.DIMENSION) : null;
        this.entityId = !voiceEffect.overDimension() ? buf.readInt() : -1;
    }

    public S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            Component senderName,
            VoiceEffect voiceEffect
    ) {
        this(voiceId, defaultVoiceId, messageId, messageTypeId, senderName, voiceEffect, null, -1);
    }

    public S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            VoiceEffect voiceEffect
    ) {
        this(voiceId, defaultVoiceId, messageId, messageTypeId, voiceEffect, null, -1);
    }

    public S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            Component senderName,
            VoiceEffect voiceEffect, Entity entity
    ) {
        this(voiceId, defaultVoiceId,
                messageId, messageTypeId,
                true, senderName,
                voiceEffect, entity.level().dimension(), entity.getId());
    }

    public S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            VoiceEffect voiceEffect, Entity entity
    ) {
        this(voiceId, defaultVoiceId,
                messageId, messageTypeId,
                voiceEffect, entity.level().dimension(), entity.getId());
    }

    private S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            boolean sendText, Component senderName,
            VoiceEffect voiceEffect, ResourceKey<Level> dimension, int entityId
    ) {
        this.voiceId = voiceId;
        this.defaultVoiceId = defaultVoiceId;
        this.messageId = messageId;
        this.messageTypeId = messageTypeId;
        this.sendText = sendText;
        this.senderName = senderName;
        this.voiceEffect = voiceEffect;
        this.dimension = dimension;
        this.entityId = entityId;
    }

    private S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            Component senderName,
            VoiceEffect voiceEffect, ResourceKey<Level> dimension, int entityId
    ) {
        this(voiceId, defaultVoiceId, messageId, messageTypeId, true, senderName, voiceEffect, dimension, entityId);
    }

    private S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            VoiceEffect voiceEffect, ResourceKey<Level> dimension, int entityId
    ) {
        this(voiceId, defaultVoiceId, messageId, messageTypeId, false, Component.empty(), voiceEffect, dimension, entityId);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(voiceId);
        buf.writeResourceLocation(defaultVoiceId);
        buf.writeResourceLocation(messageId);
        buf.writeResourceLocation(messageTypeId);
        buf.writeBoolean(sendText);
        if (sendText) buf.writeComponent(senderName);
        buf.writeByte(voiceEffect.ordinal());
        if (!voiceEffect.overDimension()) {
            buf.writeResourceKey(dimension);
            buf.writeInt(entityId);
        }
    }

    public void handler(Supplier<NetworkEvent.Context> sup) {
        final NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
            if (sendText) VocalizedClientManager.onReceivedVoiceMsgEntityBound(
                    voiceId, defaultVoiceId,
                    messageId, messageTypeId,
                    senderName,
                    voiceEffect, dimension, entityId
            );
            else VocalizedClientManager.onReceivedVoiceMsgEntityBound(
                    voiceId, defaultVoiceId,
                    messageId, messageTypeId,
                    voiceEffect, dimension, entityId
            );
        });
        ctx.setPacketHandled(true);
    }
}
