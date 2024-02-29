package committee.nova.vocalized.common.voice;

import net.minecraftforge.network.PacketDistributor;

public class VoiceContexts {
    public static final VoiceContext RADIO_ALL = VoiceContext.builder().build();

    public static VoiceContext dim(VoiceOffset offset) {
        return VoiceContext.builder()
                .offset(offset)
                .target(p -> PacketDistributor.DIMENSION.with(() -> p.level().dimension()))
                .build();
    }

    public static VoiceContext nearPlayer(double radius, VoiceOffset offset) {
        return VoiceContext.builder()
                .target(p -> PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(
                        p.getX(), p.getY(), p.getZ(),
                        radius, p.level().dimension()
                )))
                .offset(offset)
                .build();
    }
}
