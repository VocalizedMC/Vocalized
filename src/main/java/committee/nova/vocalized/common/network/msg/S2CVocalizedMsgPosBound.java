package committee.nova.vocalized.common.network.msg;

import committee.nova.vocalized.client.manager.VocalizedClientManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class S2CVocalizedMsgPosBound {
    private final ResourceLocation voiceId;
    private final ResourceLocation defaultVoiceId;
    private final ResourceLocation messageId;
    private final ResourceLocation messageTypeId;
    private final boolean sendText;
    private final Component senderName;
    private final ResourceKey<Level> dimension;
    private final Vec3 pos;
    private final List<Component> args;


    public S2CVocalizedMsgPosBound(FriendlyByteBuf buf) {
        this.voiceId = buf.readResourceLocation();
        this.defaultVoiceId = buf.readResourceLocation();
        this.messageId = buf.readResourceLocation();
        this.messageTypeId = buf.readResourceLocation();
        this.sendText = buf.readBoolean();
        this.senderName = sendText ? buf.readComponent() : Component.empty();
        this.dimension = buf.readResourceKey(Registries.DIMENSION);
        this.pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        final int length = buf.readInt();
        this.args = new ArrayList<>();
        for (int i = 0; i < length; i++) args.add(buf.readComponent());
    }

    public S2CVocalizedMsgPosBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            Component senderName,
            ResourceKey<Level> dimension, Vec3 pos, Component... args
    ) {
        this(
                voiceId, defaultVoiceId,
                messageId, messageTypeId,
                true, senderName,
                dimension, pos,
                args
        );
    }

    public S2CVocalizedMsgPosBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            ResourceKey<Level> dimension, Vec3 pos, Component... args
    ) {
        this(
                voiceId, defaultVoiceId,
                messageId, messageTypeId,
                false, Component.empty(),
                dimension, pos,
                args
        );
    }

    private S2CVocalizedMsgPosBound(
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation messageId, ResourceLocation messageTypeId,
            boolean sendText, Component senderName,
            ResourceKey<Level> dimension, Vec3 pos, Component... args
    ) {
        this.voiceId = voiceId;
        this.defaultVoiceId = defaultVoiceId;
        this.messageId = messageId;
        this.messageTypeId = messageTypeId;
        this.sendText = sendText;
        this.senderName = senderName;
        this.dimension = dimension;
        this.pos = pos;
        this.args = Arrays.stream(args).toList();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(voiceId);
        buf.writeResourceLocation(defaultVoiceId);
        buf.writeResourceLocation(messageId);
        buf.writeResourceLocation(messageTypeId);
        buf.writeBoolean(sendText);
        if (sendText) buf.writeComponent(senderName);
        buf.writeResourceKey(dimension);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeInt(args.size());
        for (final Component c : args) buf.writeComponent(c);
    }

    public void handler(Supplier<NetworkEvent.Context> sup) {
        final NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
            if (sendText) VocalizedClientManager.onReceivedVoiceMsgPosBound(
                    voiceId, defaultVoiceId,
                    messageId, messageTypeId,
                    senderName,
                    dimension, pos, args.toArray(Component[]::new)
            );
            else VocalizedClientManager.onReceivedVoiceMsgPosBound(
                    voiceId, defaultVoiceId,
                    messageId, messageTypeId,
                    dimension, pos
            );
        });
        ctx.setPacketHandled(true);
    }
}
