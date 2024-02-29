package committee.nova.vocalized.common.event.handler;

import committee.nova.vocalized.client.manager.VocalizedClientManager;
import committee.nova.vocalized.common.network.handler.NetworkHandler;
import committee.nova.vocalized.common.network.msg.C2SVocalizedVoiceChangedMsg;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ForgeClientEventHandler {
    @SubscribeEvent
    public static void onLogin(EntityJoinLevelEvent event) {
        if (!FMLEnvironment.dist.isClient()) return;
        if (!event.getEntity().equals(Minecraft.getInstance().player)) return;
        NetworkHandler.getInstance().channel.send(PacketDistributor.SERVER.noArg(), new C2SVocalizedVoiceChangedMsg(
                VocalizedClientManager.using.getId(),
                VocalizedClientManager.using.getDefaultVoiceType().getId()
        ));
    }
}
