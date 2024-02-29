package committee.nova.vocalized.client.init;

import committee.nova.vocalized.Vocalized;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import committee.nova.vocalized.init.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ClientRegistryHandler {

    public static void initSound() {
        final VocalizedRegistry registry = VocalizedRegistry.INSTANCE;
        for (final ResourceLocation vt : registry.getVoiceTypes().keySet()) {
            for (final ResourceLocation m : registry.getVoiceMessages().keySet()) {
                final String path = String.format(
                        "%s.%s.%s.%s",
                        vt.getNamespace(),
                        vt.getPath(),
                        m.getNamespace(),
                        m.getPath()
                );
                final ResourceLocation targetRl = new ResourceLocation(Vocalized.MODID, path);
                if (Minecraft.getInstance().getSoundManager().getSoundEvent(targetRl) != null)
                    RegistryHandler.SOUNDS.register(path, () -> SoundEvent.createVariableRangeEvent(targetRl));
            }
        }
    }
}
