package committee.nova.vocalized.common.voice;

import net.minecraft.world.phys.Vec3;

public enum VoiceOffset {
    NONE((s, r) -> s),
    RADIO((s, r) -> r),
    RADIO_POSITIONED((s, r) -> r.add(r.subtract(s).normalize()));

    VoiceOffset(Offset offset) {
        this.offset = offset;
    }

    private static final VoiceOffset[] valuesCache = VoiceOffset.values();
    private final Offset offset;

    public Vec3 offset(Vec3 sender, Vec3 receiver) {
        return offset.offset(sender, receiver);
    }

    public boolean isRadio() {
        return !this.equals(NONE);
    }

    public interface Offset {
        Vec3 offset(Vec3 sender, Vec3 receiver);
    }

    public static VoiceOffset getByOrdinal(byte ordinal) {
        if (ordinal < 0 || ordinal >= valuesCache.length) return VoiceOffset.NONE;
        return valuesCache[ordinal];
    }
}