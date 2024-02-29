package committee.nova.vocalized.common.network.handler;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.common.network.msg.C2SVocalizedVoiceChangedMsg;
import committee.nova.vocalized.common.network.msg.S2CVocalizedMsgMsg;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static NetworkHandler instance;

    public static NetworkHandler getInstance() {
        if (instance == null) instance = new NetworkHandler();
        return instance;
    }

    public final SimpleChannel channel;
    private int id = 0;

    private int nextId() {
        return id++;
    }

    public NetworkHandler() {
        channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Vocalized.MODID, "msg"),
                () -> NetworkRegistry.ACCEPTVANILLA,
                NetworkRegistry.ACCEPTVANILLA::equals,
                NetworkRegistry.ACCEPTVANILLA::equals
        );
        channel.messageBuilder(S2CVocalizedMsgMsg.class, nextId())
                .encoder(S2CVocalizedMsgMsg::toBytes)
                .decoder(S2CVocalizedMsgMsg::new)
                .consumerMainThread(S2CVocalizedMsgMsg::handler)
                .add();
        channel.messageBuilder(C2SVocalizedVoiceChangedMsg.class, nextId())
                .encoder(C2SVocalizedVoiceChangedMsg::toBytes)
                .decoder(C2SVocalizedVoiceChangedMsg::new)
                .consumerMainThread(C2SVocalizedVoiceChangedMsg::handler)
                .add();
    }
}
