package io.github.mochi_753.randomtranslations.mixin;

import io.github.mochi_753.randomtranslations.TranslationRandomizer;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.resources.language.ClientLanguage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(value = ClientLanguage.class)
public class ClientLanguageMixin {
    @ModifyArg(
        method = "loadFrom",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/language/ClientLanguage;<init>(Ljava/util/Map;Z)V"),
        index = 0
    )
    private static Map<String, String> randomtranslations$modifyTranslationsMap(Map<String, String> storage) {
        return TranslationRandomizer.randomize(storage, ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
    }
}
