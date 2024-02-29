package committee.nova.vocalized.mixin;

import committee.nova.vocalized.api.IVocal;
import committee.nova.vocalized.common.ref.BuiltInVoiceType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer implements IVocal {
    @Unique
    private ResourceLocation vocalized$voiceId = BuiltInVoiceType.DEFAULT.get().getId();
    @Unique
    private ResourceLocation vocalized$defaultVoiceId = BuiltInVoiceType.DEFAULT.get().getId();

    @Override
    public ResourceLocation vocalized$getVoiceId() {
        return vocalized$voiceId;
    }

    @Override
    public ResourceLocation vocalized$getDefaultVoiceId() {
        return vocalized$defaultVoiceId;
    }

    @Override
    public void vocalized$setVoiceId(ResourceLocation id) {
        this.vocalized$voiceId = id;
    }

    @Override
    public void vocalized$setDefaultVoiceId(ResourceLocation id) {
        this.vocalized$defaultVoiceId = id;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void inject$addAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        final CompoundTag tag = new CompoundTag();
        if (vocalized$voiceId != null) tag.putString("voice", vocalized$voiceId.toString());
        if (vocalized$defaultVoiceId != null) tag.putString("default_voice", vocalized$defaultVoiceId.toString());
        pCompound.put("vocalized", tag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void inject$readAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        if (!pCompound.contains("vocalized")) return;
        final CompoundTag tag = pCompound.getCompound("vocalized");
        if (tag.contains("voice")) vocalized$voiceId = ResourceLocation.tryParse(tag.getString("voice"));
        if (tag.contains("default_voice"))
            vocalized$defaultVoiceId = ResourceLocation.tryParse(tag.getString("default_voice"));
        if (vocalized$voiceId == null) vocalized$voiceId = BuiltInVoiceType.DEFAULT.get().getId();
        if (vocalized$defaultVoiceId == null) vocalized$defaultVoiceId = BuiltInVoiceType.DEFAULT.get().getId();
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void inject$restoreFrom(ServerPlayer pThat, boolean pKeepEverything, CallbackInfo ci) {
        final IVocal thatV = (IVocal) pThat;
        vocalized$voiceId = thatV.vocalized$getVoiceId();
        vocalized$defaultVoiceId = thatV.vocalized$getDefaultVoiceId();
    }
}
