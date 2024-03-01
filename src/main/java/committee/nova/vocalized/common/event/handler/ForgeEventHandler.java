package committee.nova.vocalized.common.event.handler;

import committee.nova.vocalized.common.manager.VocalizedServerManager;
import committee.nova.vocalized.common.ref.BuiltInVoiceMessage;
import committee.nova.vocalized.common.voice.VoiceContexts;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod.EventBusSubscriber
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (FMLEnvironment.production) return;
        if (event.getLevel().isClientSide()) return;
        if (event.getHand().equals(InteractionHand.OFF_HAND)) return;
        if (event.getEntity() instanceof ServerPlayer s) VocalizedServerManager.sendVoiceMsg(
                s, BuiltInVoiceMessage.BIO.get(), VoiceContexts.RADIO_ALL
        );
    }
}
