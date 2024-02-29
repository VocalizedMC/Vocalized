package committee.nova.vocalized.common.network.msg;

import committee.nova.vocalized.client.manager.VocalizedClientManager;
import committee.nova.vocalized.common.voice.VoiceOffset;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CVocalizedMsgMsg {
    private final ResourceLocation voiceId;
    private final ResourceLocation defaultVoiceId;
    private final ResourceLocation messageId;
    private final ResourceLocation messageTypeId;
    private final Vec3 pos;
    private final VoiceOffset voiceOffset;


    public S2CVocalizedMsgMsg(FriendlyByteBuf buf) {
        this.voiceId = buf.readResourceLocation();
        this.defaultVoiceId = buf.readResourceLocation();
        this.messageId = buf.readResourceLocation();
        this.messageTypeId = buf.readResourceLocation();
        this.pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.voiceOffset = VoiceOffset.getByOrdinal(buf.readByte());
    }

    public S2CVocalizedMsgMsg(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            Vec3 pos, VoiceOffset voiceOffset
    ) {
        this.voiceId = voiceId;
        this.defaultVoiceId = defaultVoiceId;
        this.messageId = messageId;
        this.messageTypeId = messageTypeId;
        this.pos = pos;
        this.voiceOffset = voiceOffset;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(voiceId);
        buf.writeResourceLocation(defaultVoiceId);
        buf.writeResourceLocation(messageId);
        buf.writeResourceLocation(messageTypeId);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeByte(voiceOffset.ordinal());
    }

    public void handler(Supplier<NetworkEvent.Context> sup) {
        final NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> VocalizedClientManager.onReceivedVoiceMsg(
                voiceId, defaultVoiceId,
                messageId, messageTypeId,
                pos, voiceOffset
        ));
        ctx.setPacketHandled(true);
    }
}
