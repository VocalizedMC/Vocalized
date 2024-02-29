package committee.nova.vocalized.common.manager;

import committee.nova.vocalized.api.IVocal;
import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.common.network.handler.NetworkHandler;
import committee.nova.vocalized.common.network.msg.S2CVocalizedMsgMsg;
import committee.nova.vocalized.common.voice.VoiceContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class VocalizedServerManager {
    public static void sendVoiceMsg(ServerPlayer player, IVoiceMessage msg, VoiceContext context) {
        final IVocal vocal = (IVocal) player;
        NetworkHandler.getInstance().channel.send(context.getTarget().determine(player), new S2CVocalizedMsgMsg(
                vocal.vocalized$getVoiceId(), vocal.vocalized$getDefaultVoiceId(),
                msg.getId(), msg.getType().getId(), player.position(), context.getOffset()
        ));
    }

    public static void sendVoiceMsg(
            Vec3 pos,
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            IVoiceMessage msg, VoiceContext context
    ) {
        NetworkHandler.getInstance().channel.send(context.getTarget().determine(null), new S2CVocalizedMsgMsg(
                voiceId, defaultVoiceId,
                msg.getId(), msg.getType().getId(), pos, context.getOffset()
        ));
    }
}
