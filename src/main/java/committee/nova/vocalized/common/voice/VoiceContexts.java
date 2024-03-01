package committee.nova.vocalized.common.voice;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collections;
import java.util.function.Predicate;

public class VoiceContexts {
    public static final VoiceContext RADIO_ALL = VoiceContext.builder().build();

    public static VoiceContext dim(VoiceEffect offset) {
        return VoiceContext.builder()
                .offset(offset)
                .target(p -> Collections.singletonList(PacketDistributor.DIMENSION.with(() -> p.level().dimension())))
                .build();
    }

    public static VoiceContext nearPlayer(double radius, VoiceEffect offset) {
        return VoiceContext.builder()
                .target(p -> Collections.singletonList(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(
                        p.getX(), p.getY(), p.getZ(),
                        radius, p.level().dimension()
                ))))
                .offset(offset)
                .build();
    }

    public static VoiceContext playerMatches(Predicate<ServerPlayer> filter, VoiceEffect offset) {
        return VoiceContext.builder()
                .target(p -> {
                    final MinecraftServer server = p.getServer();
                    if (server == null) return Collections.emptyList();
                    return server.getPlayerList().getPlayers().stream()
                            .filter(filter)
                            .map(s -> PacketDistributor.PLAYER.with(() -> s))
                            .toList();
                })
                .offset(offset)
                .build();
    }
}
