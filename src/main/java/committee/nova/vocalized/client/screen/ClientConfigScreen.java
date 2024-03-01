package committee.nova.vocalized.client.screen;

import com.mojang.serialization.Codec;
import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.api.IVoiceType;
import committee.nova.vocalized.client.config.ClientConfig;
import committee.nova.vocalized.client.util.ClientUtilities;
import committee.nova.vocalized.common.network.handler.NetworkHandler;
import committee.nova.vocalized.common.network.msg.C2SVocalizedVoiceChangedMsg;
import committee.nova.vocalized.common.ref.BuiltInVoiceMessage;
import committee.nova.vocalized.common.ref.BuiltInVoiceType;
import committee.nova.vocalized.common.registry.VocalizedRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
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
    private static final int BIO_TEXT_TOP_HEIGHT = PLAY_BUTTON_TOP_HEIGHT + 22;
    private OptionsList optionsRowList;
    public Button playBio;
    public Button quit;
    private final OptionInstance<IVoiceType> voiceType = new OptionInstance<>("screen.vocalized.cfg.selection.voice_type", v -> {
        final String key = v.getKey() + ".desc";
        return I18n.exists(key) ? Tooltip.create(Component.translatable(v.getKey() + ".desc")) : null;
    },
            OptionInstance.forOptionEnum(), new OptionInstance.Enum<>(VocalizedRegistry.INSTANCE.getVoiceTypes().values().stream().toList(),
            Codec.STRING.xmap(s -> VocalizedRegistry.INSTANCE.getVoiceType(ResourceLocation.tryParse(s)), v -> v.getIdentifier().toString())), ClientConfig.getVoiceType(), r -> {
        interruptBio();
        ClientConfig._voiceType.set(r.getIdentifier().toString());
        save();
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
        if (playing == null) return;
        ClientUtilities.getVoiceMessageText(playing.getLocation(), voiceType.get().getIdentifier(), BuiltInVoiceMessage.BIO.get().getId(), voiceType.get().getName())
                .ifPresent(c -> {
                    final List<FormattedCharSequence> seq = font.split(c, 150);
                    for (int l = 0; l < seq.size(); ++l) {
                        FormattedCharSequence formattedcharsequence = seq.get(l);
                        g.drawCenteredString(this.font, formattedcharsequence, width / 2, BIO_TEXT_TOP_HEIGHT + l * 9, 0xFFFFFF);
                    }
                });
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
                        final SimpleSoundInstance sound = ClientUtilities.getUISound(s, voice.getVolume(bio), voice.getPitch(bio));
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
                    save();
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
        if (voiceType.get().equals(BuiltInVoiceType.MUTE.get())) {
            playBio.active = false;
            return;
        }
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
        save();
    }

    @Override
    public void onClose() {
        getMinecraft().setScreen(parent instanceof PauseScreen ? null : parent);
    }

    private void interruptBio() {
        if (playing != null) {
            getMinecraft().getSoundManager().stop(playing);
            playing = null;
        }
    }

    private void save() {
        ClientConfig.CFG.save();
        if (Minecraft.getInstance().getConnection() == null) return;
        NetworkHandler.getInstance().channel.send(PacketDistributor.SERVER.noArg(), new C2SVocalizedVoiceChangedMsg(
                ClientConfig.getVoiceType().getIdentifier(),
                ClientConfig.getVoiceType().getDefaultVoiceType().getIdentifier()
        ));
    }

    public static Supplier<ConfigScreenHandler.ConfigScreenFactory> getFactory() {
        return () -> new ConfigScreenHandler.ConfigScreenFactory((mc, s) -> new ClientConfigScreen(s));
    }
}
