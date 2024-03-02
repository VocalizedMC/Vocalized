package committee.nova.vocalized.common.manager;

import com.mojang.datafixers.util.Either;
import committee.nova.vocalized.api.IVocal;
import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.common.network.handler.NetworkHandler;
import committee.nova.vocalized.common.network.msg.S2CVocalizedMsgEntityBound;
import committee.nova.vocalized.common.network.msg.S2CVocalizedMsgPosBound;
import committee.nova.vocalized.common.phys.Vec3WithDim;
import committee.nova.vocalized.common.voice.VoiceContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class VocalizedServerManager {
    public static void sendVoiceMsg(ServerPlayer player, IVoiceMessage msg, VoiceContext context) {
        sendVoiceMsgEntityBound(
                player,
                msg.getId(), msg.getType().getId(),
                context
        );
    }

    public static void sendVoiceMsg(
            Level level, Vec3 pos, Component senderName,
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            IVoiceMessage msg, VoiceContext context
    ) {
        sendVoiceMsgPosBound(
                level, pos,
                senderName,
                voiceId, defaultVoiceId,
                msg.getId(), msg.getType().getId(),
                context
        );
    }

    public static void sendVoiceMsgEntityBound(
            ServerPlayer player,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            VoiceContext context
    ) {
        final IVocal vocal = (IVocal) player;
        final S2CVocalizedMsgEntityBound p = context.getEffect().overDimension() ?
                new S2CVocalizedMsgEntityBound(
                        vocal.vocalized$getVoiceId(), vocal.vocalized$getDefaultVoiceId(),
                        msgId, msgTypeId, player.getName(), context.getEffect()) :
                new S2CVocalizedMsgEntityBound(vocal.vocalized$getVoiceId(), vocal.vocalized$getDefaultVoiceId(),
                        msgId, msgTypeId, player.getName(), context.getEffect(), player);
        for (final PacketDistributor.PacketTarget target : context.getTarget().determine(Either.left(player))) {
            NetworkHandler.getInstance().channel.send(target, p);
        }
    }

    public static void sendVoiceMsgPosBound(
            Level level, Vec3 pos, Component senderName,
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            VoiceContext context
    ) {
        final S2CVocalizedMsgPosBound p = new S2CVocalizedMsgPosBound(
                voiceId, defaultVoiceId,
                msgId, msgTypeId, senderName, context.getEffect(), level.dimension(), pos
        );
        for (final PacketDistributor.PacketTarget target : context.getTarget().determine(Either.right(Vec3WithDim.create(level, pos)))) {
            NetworkHandler.getInstance().channel.send(target, p);
        }
    }
}
