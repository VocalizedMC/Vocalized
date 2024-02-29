package committee.nova.vocalized.client.screen;

import com.mojang.serialization.Codec;
import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.api.IVoiceType;
import committee.nova.vocalized.client.config.ClientConfig;
import committee.nova.vocalized.client.manager.VocalizedClientManager;
import committee.nova.vocalized.common.ref.BuiltInVoiceMessage;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ConfigScreenHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class ClientConfigScreen extends Screen {
    private SoundInstance playing;
    private final Screen parent;
    private final int titleOffset = 8;
    private static final int OPTIONS_LIST_TOP_HEIGHT = 24;
    private static final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
    private static final int OPTIONS_LIST_ITEM_HEIGHT = 25;
    private static final int BUTTON_WIDTH = 160;
    private static final int BUTTON_HEIGHT = 20;
    private static final int DONE_BUTTON_TOP_OFFSET = 26;
    private static final int PLAY_BUTTON_TOP_HEIGHT = OPTIONS_LIST_ITEM_HEIGHT + 27;
    private OptionsList optionsRowList;
    public Button playBio;
    public Button quit;
    private final OptionInstance<IVoiceType> voiceType = new OptionInstance<>("screen.vocalized.cfg.selection.voice_type", v -> Tooltip.create(Component.translatable(v.getKey() + ".desc")),
            OptionInstance.forOptionEnum(), new OptionInstance.Enum<>(VocalizedRegistry.INSTANCE.getVoiceTypes().values().stream().toList(),
            Codec.STRING.xmap(s -> VocalizedRegistry.INSTANCE.getVoiceType(ResourceLocation.tryParse(s)), v -> v.getIdentifier().toString())), ClientConfig.getVoiceType(), r -> {
        interruptBio();
        ClientConfig._voiceType.set(r.getIdentifier().toString());
        ClientConfig.CFG.save();
    });

    public ClientConfigScreen(Screen parent) {
        super(Component.translatable("screen.vocalized.cfg.title"));
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
        renderBackground(g);
        optionsRowList.render(g, mouseX, mouseY, partialTicks);
        g.drawCenteredString(font, title, width / 2, titleOffset, 0xFFFFFF);
        super.render(g, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void init() {
        if (minecraft == null) return;
        optionsRowList = new OptionsList(
                minecraft, width, height,
                OPTIONS_LIST_TOP_HEIGHT,
                height - OPTIONS_LIST_BOTTOM_OFFSET,
                OPTIONS_LIST_ITEM_HEIGHT
        );
        optionsRowList.addBig(voiceType);
        playBio = addRenderableWidget(Button
                .builder(Component.translatable("screen.vocalized.button.play_bio"), b -> {
                    if (!b.isActive()) return;
                    final IVoiceType voice = ClientConfig.getVoiceType();
                    final IVoiceMessage bio = BuiltInVoiceMessage.BIO.get();
                    voice.getVoice(bio).ifPresent(s -> {
                        final SimpleSoundInstance sound = VocalizedClientManager.getUISound(s, voice.getVolume(bio), voice.getPitch(bio));
                        this.playing = sound;
                        getMinecraft().getSoundManager().play(sound);
                        b.active = false;
                    });
                })
                .bounds((width - BUTTON_WIDTH) / 2,
                        PLAY_BUTTON_TOP_HEIGHT,
                        BUTTON_WIDTH, BUTTON_HEIGHT)
                .build()
        );
        quit = addRenderableWidget(Button
                .builder(Component.translatable("gui.done"), b -> {
                    ClientConfig.CFG.save();
                    getMinecraft().setScreen(parent);
                })
                .bounds((width - BUTTON_WIDTH) / 2,
                        height - DONE_BUTTON_TOP_OFFSET,
                        BUTTON_WIDTH, BUTTON_HEIGHT)
                .build()
        );
        addWidget(optionsRowList);
    }

    @Override
    public void tick() {
        super.tick();
        if (playing == null) {
            playBio.active = true;
            return;
        }
        if (!getMinecraft().getSoundManager().isActive(playing)) {
            playBio.active = true;
            playing = null;
        }
    }

    @Override
    public void removed() {
        interruptBio();
        ClientConfig.CFG.save();
    }

    @Override
    public void onClose() {
        interruptBio();
        getMinecraft().setScreen(parent instanceof PauseScreen ? null : parent);
    }

    private void interruptBio() {
        if (playing != null) {
            getMinecraft().getSoundManager().stop(playing);
            playing = null;
        }
    }

    public static Supplier<ConfigScreenHandler.ConfigScreenFactory> getFactory() {
        return () -> new ConfigScreenHandler.ConfigScreenFactory((mc, s) -> new ClientConfigScreen(s));
    }
}
