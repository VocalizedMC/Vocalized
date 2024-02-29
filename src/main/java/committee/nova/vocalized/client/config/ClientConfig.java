package committee.nova.vocalized.client.config;

import committee.nova.vocalized.api.IVoiceType;
import committee.nova.vocalized.common.ref.BuiltInVoiceType;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientConfig {
    public static final ForgeConfigSpec CFG;
    public static final ForgeConfigSpec.ConfigValue<String> _voiceType;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("VoiceSettings");
        _voiceType = builder
                .comment("The voice type you use for various voice messages.")
                .define("voiceType", BuiltInVoiceType.BUILTIN_FEMALE.get().getIdentifier().toString());
        builder.pop();
        CFG = builder.build();
    }

    public static IVoiceType getVoiceType() {
        return VocalizedRegistry.INSTANCE.getVoiceTypeOrDefault(
                ResourceLocation.tryParse(_voiceType.get()),
                BuiltInVoiceType.BUILTIN_FEMALE.get()
        );
    }
}
