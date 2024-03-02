package committee.nova.vocalized.common.network.msg;

import committee.nova.vocalized.client.manager.VocalizedClientManager;
import committee.nova.vocalized.common.voice.VoiceEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CVocalizedMsgPosBound {
    private final ResourceLocation voiceId;
    private final ResourceLocation defaultVoiceId;
    private final ResourceLocation messageId;
    private final ResourceLocation messageTypeId;
    private final Component senderName;
    private final VoiceEffect voiceEffect;
    private final ResourceKey<Level> dimension;
    private final Vec3 pos;


    public S2CVocalizedMsgPosBound(FriendlyByteBuf buf) {
        this.voiceId = buf.readResourceLocation();
        this.defaultVoiceId = buf.readResourceLocation();
        this.messageId = buf.readResourceLocation();
        this.messageTypeId = buf.readResourceLocation();
        this.senderName = buf.readComponent();
        this.voiceEffect = VoiceEffect.getByOrdinal(buf.readByte());
        this.dimension = buf.readResourceKey(Registries.DIMENSION);
        this.pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public S2CVocalizedMsgPosBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            Component senderName,
            VoiceEffect voiceEffect, ResourceKey<Level> dimension, Vec3 pos
    ) {
        this.voiceId = voiceId;
        this.defaultVoiceId = defaultVoiceId;
        this.messageId = messageId;
        this.messageTypeId = messageTypeId;
        this.senderName = senderName;
        this.voiceEffect = voiceEffect;
        this.dimension = dimension;
        this.pos = pos;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(voiceId);
        buf.writeResourceLocation(defaultVoiceId);
        buf.writeResourceLocation(messageId);
        buf.writeResourceLocation(messageTypeId);
        buf.writeComponent(senderName);
        buf.writeByte(voiceEffect.ordinal());
        buf.writeResourceKey(dimension);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
    }

    public void handler(Supplier<NetworkEvent.Context> sup) {
        final NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> VocalizedClientManager.onReceivedVoiceMsgPosBound(
                voiceId, defaultVoiceId,
                messageId, messageTypeId,
                senderName,
                voiceEffect, dimension, pos
        ));
        ctx.setPacketHandled(true);
    }
}
