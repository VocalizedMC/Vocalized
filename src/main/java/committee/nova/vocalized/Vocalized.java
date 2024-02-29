package committee.nova.vocalized;

import com.mojang.logging.LogUtils;
import committee.nova.vocalized.common.network.handler.NetworkHandler;
import committee.nova.vocalized.init.RegistryHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Vocalized.MODID)
public class Vocalized {
    public static final String MODID = "vocalized";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Vocalized() {
        NetworkHandler.getInstance();
        RegistryHandler.init(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
