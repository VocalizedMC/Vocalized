package committee.nova.vocalized.common.voice;

public enum VoiceEffect {
    NONE,
    RADIO;


    private static final VoiceEffect[] valuesCache = VoiceEffect.values();

    public boolean overDimension() {
        return this.equals(RADIO);
    }

    public static VoiceEffect getByOrdinal(byte ordinal) {
        if (ordinal < 0 || ordinal >= valuesCache.length) return VoiceEffect.NONE;
        return valuesCache[ordinal];
    }
}