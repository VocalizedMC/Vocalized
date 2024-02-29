package committee.nova.vocalized.common.voice;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class VoiceContext {
    private final TargetDeterminer target;
    private final VoiceOffset offset;

    private VoiceContext(TargetDeterminer target, VoiceOffset offset) {
        this.target = target;
        this.offset = offset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public TargetDeterminer getTarget() {
        return target;
    }

    public VoiceOffset getOffset() {
        return offset;
    }

    public static class Builder {
        private TargetDeterminer target = p -> PacketDistributor.ALL.noArg();
        private VoiceOffset offset = VoiceOffset.RADIO;

        public Builder target(TargetDeterminer target) {
            this.target = target;
            return this;
        }

        public Builder offset(VoiceOffset offset) {
            this.offset = offset;
            return this;
        }

        public VoiceContext build() {
            return new VoiceContext(target, offset);
        }
    }

    public interface TargetDeterminer {
        PacketDistributor.PacketTarget determine(ServerPlayer sender);
    }
}
