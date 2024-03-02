package committee.nova.vocalized.common.phys;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record Vec3WithDim(Level level, Vec3 pos) {
    public static Vec3WithDim create(Level level, Vec3 pos) {
        return new Vec3WithDim(level, pos);
    }
}
