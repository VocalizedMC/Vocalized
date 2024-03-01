package committee.nova.vocalized.common.voice;

import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.api.IVoiceType;
import committee.nova.vocalized.common.ref.BuiltInVoiceType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@MethodsReturnNonnullByDefault
public class VoiceType implements IVoiceType {
    private final ResourceLocation id;
    private String keyCache;
    private Component nameCache;
    private final Map<IVoiceMessage, Optional<ResourceLocation>> soundMap = new HashMap<>();

    public VoiceType(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return id;
    }

    @Override
    public Component getName() {
        if (nameCache == null) nameCache = Component.translatable(getKey());
        return nameCache;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Optional<ResourceLocation> getVoice(IVoiceMessage msg) {
        if (soundMap.containsKey(msg)) return soundMap.get(msg);
        final ResourceLocation targetRl = new ResourceLocation(
                getIdentifier().getNamespace(),
                String.format(
                        "%s.%s.%s",
                        getIdentifier().getPath(),
                        msg.getId().getNamespace(),
                        msg.getId().getPath()
                )
        );
        // e.g.: vocalized:builtin_male.vocalized.bio
        final Optional<ResourceLocation> ret = Minecraft.getInstance().getSoundManager().getSoundEvent(targetRl) != null ?
                Optional.of(targetRl) :
                Optional.empty();
        soundMap.put(msg, ret);
        return ret;
    }

    @Override
    public IVoiceType getDefaultVoiceType() {
        return BuiltInVoiceType.BUILTIN_FEMALE.get();
    }

    @Override
    public String getKey() {
        if (keyCache == null) keyCache = "vocalized.voice_type." + getIdentifier().toString().replace(':', '.');
        return keyCache;
    }
}
