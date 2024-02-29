package committee.nova.vocalized.common.network.msg;

import committee.nova.vocalized.api.IVocal;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SVocalizedVoiceChangedMsg {
    private final ResourceLocation voiceId;
    private final ResourceLocation defaultVoiceId;


    public C2SVocalizedVoiceChangedMsg(FriendlyByteBuf buf) {
        this.voiceId = buf.readResourceLocation();
        this.defaultVoiceId = buf.readResourceLocation();
    }

    public C2SVocalizedVoiceChangedMsg(ResourceLocation voiceId, ResourceLocation defaultVoiceId) {
        this.voiceId = voiceId;
        this.defaultVoiceId = defaultVoiceId;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(voiceId);
        buf.writeResourceLocation(defaultVoiceId);
    }

    public void handler(Supplier<NetworkEvent.Context> sup) {
        final NetworkEvent.Context ctx = sup.get();
        final ServerPlayer player = ctx.getSender();
        if (player == null) return;
        final IVocal vocal = (IVocal) player;
        vocal.vocalized$setVoiceId(voiceId);
        vocal.vocalized$setDefaultVoiceId(defaultVoiceId);
    }
}
