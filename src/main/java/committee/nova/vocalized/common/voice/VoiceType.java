package committee.nova.vocalized.common.voice;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.api.IVoiceType;
import committee.nova.vocalized.common.ref.BuiltInVoiceType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VoiceType implements IVoiceType {
    private final ResourceLocation id;
    private Component name;
    private final Map<IVoiceMessage, Optional<ResourceLocation>> soundMap = new HashMap<>();

    public VoiceType(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public Component getName() {
        if (name == null) name = Component.translatable("vocalized.voice_type." + getId().toString()
                .replace(':', '.'));
        return name;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Optional<ResourceLocation> getVoice(IVoiceMessage msg) {
        if (soundMap.containsKey(msg)) return soundMap.get(msg);
        final ResourceLocation targetRl = new ResourceLocation(
                Vocalized.MODID,
                String.format(
                        "%s.%s.%s.%s",
                        getId().getNamespace(),
                        getId().getPath(),
                        msg.getId().getNamespace(),
                        msg.getId().getPath()
                )
        );
        // e.g.: vocalized:vocalized.builtin_male.vocalized.bio
        final Optional<ResourceLocation> ret = Minecraft.getInstance().getSoundManager().getSoundEvent(targetRl) != null ?
                Optional.of(targetRl) :
                Optional.empty();
        soundMap.put(msg, ret);
        return ret;
    }

    @Override
    public IVoiceType getDefaultVoiceType() {
        return BuiltInVoiceType.DEFAULT.get();
    }
}
