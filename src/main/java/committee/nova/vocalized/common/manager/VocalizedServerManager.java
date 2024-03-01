package committee.nova.vocalized.common.manager;

import committee.nova.vocalized.api.IVocal;
import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.common.network.handler.NetworkHandler;
import committee.nova.vocalized.common.network.msg.S2CVocalizedMsgMsg;
import committee.nova.vocalized.common.voice.VoiceContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class VocalizedServerManager {
    public static void sendVoiceMsg(ServerPlayer player, IVoiceMessage msg, VoiceContext context) {
        final IVocal vocal = (IVocal) player;
        final S2CVocalizedMsgMsg p = context.getEffect().overDimension() ?
                new S2CVocalizedMsgMsg(
                        vocal.vocalized$getVoiceId(), vocal.vocalized$getDefaultVoiceId(),
                        msg.getId(), msg.getType().getId(), context.getEffect()) :
                new S2CVocalizedMsgMsg(vocal.vocalized$getVoiceId(), vocal.vocalized$getDefaultVoiceId(),
                        msg.getId(), msg.getType().getId(), context.getEffect(), player);
        for (final PacketDistributor.PacketTarget target : context.getTarget().determine(player)) {
            NetworkHandler.getInstance().channel.send(target, p);
        }
    }
}
