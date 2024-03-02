package committee.nova.vocalized.common.voice;

import com.mojang.datafixers.util.Either;
import committee.nova.vocalized.common.phys.Vec3WithDim;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collections;
import java.util.List;

public class VoiceContext {
    private final TargetDeterminer target;
    private final VoiceEffect effect;

    private VoiceContext(TargetDeterminer target, VoiceEffect effect) {
        this.target = target;
        this.effect = effect;
    }

    public static Builder builder() {
        return new Builder();
    }

    public TargetDeterminer getTarget() {
        return target;
    }

    public VoiceEffect getEffect() {
        return effect;
    }

    public static class Builder {
        private TargetDeterminer target = e -> Collections.singletonList(PacketDistributor.ALL.noArg());
        private VoiceEffect offset = VoiceEffect.RADIO;

        public Builder target(TargetDeterminer target) {
            this.target = target;
            return this;
        }

        public Builder offset(VoiceEffect offset) {
            this.offset = offset;
            return this;
        }

        public VoiceContext build() {
            return new VoiceContext(target, offset);
        }
    }

    public interface TargetDeterminer {
        List<PacketDistributor.PacketTarget> determine(Either<ServerPlayer, Vec3WithDim> sender);
    }
}
