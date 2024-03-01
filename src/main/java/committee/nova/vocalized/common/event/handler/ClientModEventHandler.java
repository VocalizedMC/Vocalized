package committee.nova.vocalized.common.event.handler;

import committee.nova.vocalized.client.config.ClientConfig;
import committee.nova.vocalized.common.network.handler.NetworkHandler;
import committee.nova.vocalized.common.network.msg.C2SVocalizedVoiceChangedMsg;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventHandler {
    @SubscribeEvent
    public static void onConfig(ModConfigEvent event) {
        if (!FMLEnvironment.dist.isClient()) return;
        if (Minecraft.getInstance().getConnection() == null) return;
        NetworkHandler.getInstance().channel.send(PacketDistributor.SERVER.noArg(), new C2SVocalizedVoiceChangedMsg(
                ClientConfig.getVoiceType().getIdentifier(),
                ClientConfig.getVoiceType().getDefaultVoiceType().getIdentifier()
        ));
    }
}
