package committee.nova.vocalized;

import com.mojang.logging.LogUtils;
import committee.nova.vocalized.client.config.ClientConfig;
import committee.nova.vocalized.client.screen.ClientConfigScreen;
import committee.nova.vocalized.common.network.handler.NetworkHandler;
import committee.nova.vocalized.init.RegistryHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Vocalized.MODID)
public class Vocalized {
    public static final String MODID = "vocalized";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Vocalized() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CFG);
        NetworkHandler.getInstance();
        RegistryHandler.init(FMLJavaModLoadingContext.get().getModEventBus());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                ClientConfigScreen.getFactory()));
    }
}
