package committee.nova.vocalized.common.network.msg;

import committee.nova.vocalized.client.manager.VocalizedClientManager;
import committee.nova.vocalized.common.voice.VoiceEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CVocalizedMsgMsg {
    private final ResourceLocation voiceId;
    private final ResourceLocation defaultVoiceId;
    private final ResourceLocation messageId;
    private final ResourceLocation messageTypeId;
    private final String senderName;
    private final VoiceEffect voiceEffect;
    private final ResourceKey<Level> dimension;
    private final int entityId;


    public S2CVocalizedMsgMsg(FriendlyByteBuf buf) {
        this.voiceId = buf.readResourceLocation();
        this.defaultVoiceId = buf.readResourceLocation();
        this.messageId = buf.readResourceLocation();
        this.messageTypeId = buf.readResourceLocation();
        this.senderName = buf.readUtf();
        this.voiceEffect = VoiceEffect.getByOrdinal(buf.readByte());
        this.dimension = !voiceEffect.overDimension() ? buf.readResourceKey(Registries.DIMENSION) : null;
        this.entityId = !voiceEffect.overDimension() ? buf.readInt() : -1;
    }

    public S2CVocalizedMsgMsg(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            String senderName,
            VoiceEffect voiceEffect
    ) {
        this(voiceId, defaultVoiceId, messageId, messageTypeId, senderName, voiceEffect, null, -1);
    }

    public S2CVocalizedMsgMsg(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            String senderName,
            VoiceEffect voiceEffect, Entity entity
    ) {
        this(voiceId, defaultVoiceId,
                messageId, messageTypeId,
                senderName,
                voiceEffect, entity.level().dimension(), entity.getId());
    }

    private S2CVocalizedMsgMsg(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            String senderName,
            VoiceEffect voiceEffect, ResourceKey<Level> dimension, int entityId
    ) {
        this.voiceId = voiceId;
        this.defaultVoiceId = defaultVoiceId;
        this.messageId = messageId;
        this.messageTypeId = messageTypeId;
        this.senderName = senderName;
        this.voiceEffect = voiceEffect;
        this.dimension = dimension;
        this.entityId = entityId;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(voiceId);
        buf.writeResourceLocation(defaultVoiceId);
        buf.writeResourceLocation(messageId);
        buf.writeResourceLocation(messageTypeId);
        buf.writeUtf(senderName);
        buf.writeByte(voiceEffect.ordinal());
        if (!voiceEffect.isRadio()) {
            buf.writeResourceKey(dimension);
            buf.writeInt(entityId);
        }
    }

    public void handler(Supplier<NetworkEvent.Context> sup) {
        final NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> VocalizedClientManager.onReceivedVoiceMsg(
                voiceId, defaultVoiceId,
                messageId, messageTypeId,
                senderName,
                voiceEffect, voiceEffect.isRadio(), entityId
        ));
        ctx.setPacketHandled(true);
    }
}
