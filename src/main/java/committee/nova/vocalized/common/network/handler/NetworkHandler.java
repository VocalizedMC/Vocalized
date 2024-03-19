package committee.nova.vocalized.common.network.handler;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.common.network.msg.C2SVocalizedVoiceChanged;
import committee.nova.vocalized.common.network.msg.S2CVocalizedMsgEntityBound;
import committee.nova.vocalized.common.network.msg.S2CVocalizedMsgPosBound;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

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
        channel = ChannelBuilder.named(new ResourceLocation(Vocalized.MODID, "msg"))
                .acceptedVersions(Channel.VersionTest.ACCEPT_VANILLA)
                .simpleChannel();
        channel.messageBuilder(C2SVocalizedVoiceChanged.class, nextId())
                .encoder(C2SVocalizedVoiceChanged::toBytes)
                .decoder(C2SVocalizedVoiceChanged::new)
                .consumerMainThread(C2SVocalizedVoiceChanged::handler)
                .add();
        channel.messageBuilder(S2CVocalizedMsgEntityBound.class, nextId())
                .encoder(S2CVocalizedMsgEntityBound::toBytes)
                .decoder(S2CVocalizedMsgEntityBound::new)
                .consumerMainThread(S2CVocalizedMsgEntityBound::handler)
                .add();
        channel.messageBuilder(S2CVocalizedMsgPosBound.class, nextId())
                .encoder(S2CVocalizedMsgPosBound::toBytes)
                .decoder(S2CVocalizedMsgPosBound::new)
                .consumerMainThread(S2CVocalizedMsgPosBound::handler)
                .add();
    }
}
