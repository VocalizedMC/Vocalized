package committee.nova.vocalized.common.registry;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.api.IVoiceMessageType;
import committee.nova.vocalized.api.IVoiceType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class VocalizedRegistry {
    public static final VocalizedRegistry INSTANCE = new VocalizedRegistry();
    private final Map<ResourceLocation, IVoiceType> voiceTypes = new HashMap<>();
    private final Map<ResourceLocation, IVoiceMessage> voiceMessages = new HashMap<>();
    private final Map<ResourceLocation, IVoiceMessageType> voiceMessageTypes = new HashMap<>();

    public boolean registerVoiceType(IVoiceType type) {
        final ResourceLocation id = type.getId();
        if (voiceTypes.containsKey(id)) {
            Vocalized.LOGGER.warn("Trying to register voice type {}, duplicated and skipped!", id.toString());
            return false;
        }
        voiceTypes.put(id, type);
        return true;
    }

    public boolean registerVoiceMessage(IVoiceMessage msg) {
        final ResourceLocation id = msg.getId();
        if (voiceMessages.containsKey(id)) {
            Vocalized.LOGGER.warn("Trying to register voice message {}, duplicated and skipped!", id.toString());
            return false;
        }
        voiceMessages.put(id, msg);
        return true;
    }

    public boolean registerVoiceMessageType(IVoiceMessageType type) {
        final ResourceLocation id = type.getId();
        if (voiceMessageTypes.containsKey(id)) {
            Vocalized.LOGGER.warn("Trying to register voice message type {}, duplicated and skipped!", id.toString());
            return false;
        }
        voiceMessageTypes.put(id, type);
        return true;
    }

    public IVoiceType getVoiceType(ResourceLocation id) {
        return voiceTypes.get(id);
    }

    public IVoiceType getVoiceTypeOrDefault(ResourceLocation id, IVoiceType defaultType) {
        final IVoiceType type = getVoiceType(id);
        if (type != null) return type;
        return defaultType;
    }

    public IVoiceMessage getVoiceMessage(ResourceLocation id) {
        return voiceMessages.get(id);
    }

    public IVoiceMessageType getVoiceMessageType(ResourceLocation id) {
        return voiceMessageTypes.get(id);
    }

    public IVoiceMessageType getVoiceMessageTypeOrDefault(ResourceLocation id, IVoiceMessageType defaultType) {
        final IVoiceMessageType type = getVoiceMessageType(id);
        if (type != null) return type;
        return defaultType;
    }

    public Map<ResourceLocation, IVoiceType> getVoiceTypes() {
        return Map.copyOf(voiceTypes);
    }

    public Map<ResourceLocation, IVoiceMessage> getVoiceMessages() {
        return Map.copyOf(voiceMessages);
    }

    public Map<ResourceLocation, IVoiceMessageType> getVoiceMessageTypes() {
        return Map.copyOf(voiceMessageTypes);
    }
}
