package committee.nova.vocalized.client.voice;

import net.minecraft.resources.ResourceLocation;

public record PlayingVoiceMessage(ResourceLocation msg, long startTime) {
}
