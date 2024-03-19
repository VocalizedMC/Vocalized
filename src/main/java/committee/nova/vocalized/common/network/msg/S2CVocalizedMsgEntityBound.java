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
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final List<Component> args;


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
        final int length = buf.readInt();
        this.args = new ArrayList<>();
        for (int i = 0; i < length; i++) args.add(buf.readComponent());
    }

    public S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            Component senderName,
            VoiceEffect voiceEffect, Component... args
    ) {
        this(
                voiceId, defaultVoiceId,
                messageId, messageTypeId,
                senderName,
                voiceEffect, null, -1,
                args
        );
    }

    public S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            VoiceEffect voiceEffect, Component... args
    ) {
        this(
                voiceId, defaultVoiceId,
                messageId, messageTypeId,
                voiceEffect, null, -1,
                args
        );
    }

    public S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            Component senderName,
            VoiceEffect voiceEffect, Entity entity, Component... args
    ) {
        this(
                voiceId, defaultVoiceId,
                messageId, messageTypeId,
                true, senderName,
                voiceEffect, entity.level().dimension(), entity.getId(),
                args
        );
    }

    public S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            VoiceEffect voiceEffect, Entity entity, Component... args
    ) {
        this(
                voiceId, defaultVoiceId,
                messageId, messageTypeId,
                voiceEffect, entity.level().dimension(), entity.getId(),
                args
        );
    }

    private S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            boolean sendText, Component senderName,
            VoiceEffect voiceEffect, ResourceKey<Level> dimension, int entityId, Component... args
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
        this.args = Arrays.stream(args).toList();
    }

    private S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            Component senderName,
            VoiceEffect voiceEffect, ResourceKey<Level> dimension, int entityId, Component... args
    ) {
        this(
                voiceId, defaultVoiceId,
                messageId, messageTypeId,
                true, senderName,
                voiceEffect, dimension, entityId,
                args
        );
    }

    private S2CVocalizedMsgEntityBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            VoiceEffect voiceEffect, ResourceKey<Level> dimension, int entityId, Component... args
    ) {
        this(
                voiceId, defaultVoiceId,
                messageId, messageTypeId,
                false, Component.empty(),
                voiceEffect, dimension, entityId,
                args
        );
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
        buf.writeInt(args.size());
        for (final Component c : args) buf.writeComponent(c);
    }

    public void handler(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (sendText) VocalizedClientManager.onReceivedVoiceMsgEntityBound(
                    voiceId, defaultVoiceId,
                    messageId, messageTypeId,
                    senderName,
                    voiceEffect, dimension, entityId, args.toArray(Component[]::new)
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
