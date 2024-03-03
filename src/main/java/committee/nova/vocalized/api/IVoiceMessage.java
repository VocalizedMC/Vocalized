package committee.nova.vocalized.api;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IVoiceMessage {
    ResourceLocation getId();

    default String getTranslationKey(IVoiceType type) {
        return String.format(
                "v_msg.vocalized.%s.%s.%s.%s",
                type.getIdentifier().getNamespace(),
                type.getIdentifier().getPath(),
                getId().getNamespace(),
                getId().getPath()
        );
    }

    default String getDefaultTranslationKey() {
        return String.format(
                "v_msg.vocalized.default.%s.%s",
                getId().getNamespace(),
                getId().getPath()
        );
    }

    default Optional<Component> getText(IVoiceType type, Object... args) {
        String key = getTranslationKey(type);
        if (I18n.exists(key)) return Optional.of(Component.translatable(key, args));
        key = getDefaultTranslationKey();
        if (I18n.exists(key)) return Optional.of(Component.translatable(key, args));
        return Optional.empty();
    }

    ;

    IVoiceMessageType getType();

    default float getVolume() {
        return 1.0F;
    }

    default float getPitch() {
        return 1.0F;
    }
}
