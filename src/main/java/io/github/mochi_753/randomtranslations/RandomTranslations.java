package io.github.mochi_753.randomtranslations;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = RandomTranslations.MOD_ID)
public class RandomTranslations {
    public static final String MOD_ID = "randomtranslations";

    @SuppressWarnings("removal")
    public RandomTranslations() {
        this(FMLJavaModLoadingContext.get());
    }

    public RandomTranslations(final FMLJavaModLoadingContext context) {
    }
}
