package committee.nova.vocalized.common.voice;

import committee.nova.vocalized.common.phys.Vec3WithDim;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class VoiceContexts {
    public static final VoiceContext RADIO_ALL = VoiceContext.builder().build();

    public static VoiceContext dim(VoiceEffect offset) {
        return VoiceContext.builder()
                .offset(offset)
                .target(e -> {
                    AtomicReference<ResourceKey<Level>> atomic = new AtomicReference<>(null);
                    e.ifLeft(p -> atomic.set(p.level().dimension()))
                            .ifRight(d -> atomic.set(d.level().dimension()));
                    final ResourceKey<Level> dim = atomic.get();
                    return dim != null ? Collections.singletonList(PacketDistributor.DIMENSION.with(dim)) : Collections.emptyList();
                })
                .build();
    }

    public static VoiceContext near(double radius, VoiceEffect offset) {
        return VoiceContext.builder()
                .target(e -> {
                    final AtomicReference<Vec3WithDim> atomic = new AtomicReference<>(null);
                    e.ifLeft(p -> atomic.set(Vec3WithDim.create(p.level(), p.getEyePosition())))
                            .ifRight(atomic::set);
                    final Vec3WithDim posWithDim = atomic.get();
                    if (posWithDim != null) {
                        final Vec3 pos = posWithDim.pos();
                        return Collections.singletonList(PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(
                                pos.x, pos.y, pos.z, radius, posWithDim.level().dimension()
                        )));
                    }
                    return Collections.emptyList();
                })
                .offset(offset)
                .build();
    }

    public static VoiceContext playerMatches(Predicate<ServerPlayer> filter, VoiceEffect offset) {
        return VoiceContext.builder()
                .target(e -> {
                    final AtomicReference<MinecraftServer> atomic = new AtomicReference<>(null);
                    e.ifLeft(p -> atomic.set(p.getServer()))
                            .ifRight(d -> atomic.set(d.level().getServer()));
                    final MinecraftServer server = atomic.get();
                    if (server == null) return Collections.emptyList();
                    return server.getPlayerList().getPlayers().stream()
                            .filter(filter)
                            .map(PacketDistributor.PLAYER::with)
                            .toList();
                })
                .offset(offset)
                .build();
    }
}
