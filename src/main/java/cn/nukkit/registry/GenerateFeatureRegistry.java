package cn.nukkit.registry;

import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.feature.foliage.DesertCactusFeature;
import cn.nukkit.level.generator.feature.foliage.DesertDeadBushFeature;
import cn.nukkit.level.generator.feature.foliage.DesertDryGrassGenerateFeature;
import cn.nukkit.level.generator.feature.foliage.FlowerForestFoliageFeature;
import cn.nukkit.level.generator.feature.foliage.SeagrassRiverGenerateFeature;
import cn.nukkit.level.generator.feature.foliage.SugarcaneFeature;
import cn.nukkit.level.generator.feature.ore.*;
import cn.nukkit.level.generator.feature.river.ClayGenerateFeature;
import cn.nukkit.level.generator.feature.river.GravelGenerateFeature;
import cn.nukkit.level.generator.feature.river.SandGenerateFeature;
import cn.nukkit.level.generator.feature.foliage.TallGrassGenerateFeature;
import cn.nukkit.level.generator.feature.foliage.JungleMelonGenerateFeature;
import cn.nukkit.level.generator.feature.terrain.CaveGenerateFeature;
import cn.nukkit.level.generator.feature.terrain.DeepslateGeneratorFeature;
import cn.nukkit.level.generator.feature.tree.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class GenerateFeatureRegistry implements IRegistry<String, GenerateFeature, Class<? extends GenerateFeature>> {
    private static final Object2ObjectOpenHashMap<String, Class<? extends GenerateFeature>> REGISTRY = new Object2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try {
            this.register(DeepslateGeneratorFeature.NAME, DeepslateGeneratorFeature.class);
            this.register(DirtOreGenerationFeature.NAME, DirtOreGenerationFeature.class);
            this.register(GravelOreGenerationFeature.NAME, GravelOreGenerationFeature.class);
            this.register(GraniteOreUpperGenerationFeature.NAME, GraniteOreUpperGenerationFeature.class);
            this.register(GraniteOreLowerGenerationFeature.NAME, GraniteOreLowerGenerationFeature.class);
            this.register(DioriteOreUpperGenerationFeature.NAME, DioriteOreUpperGenerationFeature.class);
            this.register(DioriteOreLowerGenerationFeature.NAME, DioriteOreLowerGenerationFeature.class);
            this.register(AndesiteOreUpperGenerationFeature.NAME, AndesiteOreUpperGenerationFeature.class);
            this.register(AndesiteOreLowerGenerationFeature.NAME, AndesiteOreUpperGenerationFeature.class);
            this.register(TuffOreGenerationFeature.NAME, TuffOreGenerationFeature.class);
            this.register(CoalOreUpperGenerationFeature.NAME, CoalOreUpperGenerationFeature.class);
            this.register(CoalOreLowerGenerationFeature.NAME, CoalOreLowerGenerationFeature.class);
            this.register(CoalOreMountainsGenerationFeature.NAME, CoalOreMountainsGenerationFeature.class);
            this.register(IronOreUpperGenerationFeature.NAME, IronOreUpperGenerationFeature.class);
            this.register(IronOreMiddleGenerationFeature.NAME, IronOreMiddleGenerationFeature.class);
            this.register(IronOreSmallGenerationFeature.NAME, IronOreSmallGenerationFeature.class);
            this.register(CopperOreGenerationFeature.NAME, CopperOreGenerationFeature.class);
            this.register(CopperOreDripstoneCaveGenerationFeature.NAME, CopperOreDripstoneCaveGenerationFeature.class);
            this.register(RedstoneOreGenerationFeature.NAME, RedstoneOreGenerationFeature.class);
            this.register(RedstoneOreLowerGenerationFeature.NAME, RedstoneOreLowerGenerationFeature.class);
            this.register(LapisOreBuriedGenerationFeature.NAME, LapisOreBuriedGenerationFeature.class);
            this.register(LapisOreGenerationFeature.NAME, LapisOreGenerationFeature.class);
            this.register(GoldOreMesaGenerationFeature.NAME, GoldOreMesaGenerationFeature.class);
            this.register(GoldOreLowerGenerationFeature.NAME, GoldOreLowerGenerationFeature.class);
            this.register(GoldOreGenerationFeature.NAME, GoldOreGenerationFeature.class);
            this.register(DiamondOreSquareGenerationFeature.NAME, DiamondOreSquareGenerationFeature.class);
            this.register(DiamondOreGenerationFeature.NAME, DiamondOreGenerationFeature.class);
            this.register(DiamondOreBuriedGenerationFeature.NAME, DiamondOreBuriedGenerationFeature.class);
            this.register(DiamondOreLargeGenerationFeature.NAME, DiamondOreLargeGenerationFeature.class);
            this.register(EmeraldOreGenerationFeature.NAME, EmeraldOreGenerationFeature.class);
            this.register(EmeraldOreExtremeHillsSurfaceGenerationFeature.NAME, EmeraldOreExtremeHillsSurfaceGenerationFeature.class);
            this.register(InfestedOreGenerationFeature.NAME, InfestedOreGenerationFeature.class);
            this.register(LegacyJungleTreeFeature.NAME, LegacyJungleTreeFeature.class);
            this.register(BambooJungleTreeFeature.NAME, BambooJungleTreeFeature.class);
            this.register(JungleEdgeTreeFeature.NAME, JungleEdgeTreeFeature.class);
            this.register(SavannaTreeFeature.NAME, SavannaTreeFeature.class);
            this.register(ForestTreeFeature.NAME, ForestTreeFeature.class);
            this.register(FlowerForestTreeFeature.NAME, FlowerForestTreeFeature.class);
            this.register(JungleMelonGenerateFeature.NAME, JungleMelonGenerateFeature.class);
            this.register(MangroveTreeFeature.NAME, MangroveTreeFeature.class);
            this.register(ClayGenerateFeature.NAME, ClayGenerateFeature.class);
            this.register(GravelGenerateFeature.NAME, GravelGenerateFeature.class);
            this.register(SandGenerateFeature.NAME, SandGenerateFeature.class);
            this.register(CaveGenerateFeature.NAME, CaveGenerateFeature.class);
            this.register(FlowerForestFoliageFeature.NAME, FlowerForestFoliageFeature.class);
            this.register(SeagrassRiverGenerateFeature.NAME, SeagrassRiverGenerateFeature.class);
            this.register(MushroomIslandMushroomFeature.NAME, MushroomIslandMushroomFeature.class);
            this.register(SugarcaneFeature.NAME, SugarcaneFeature.class);
            this.register(PlainsTreeFeature.NAME, PlainsTreeFeature.class);
            this.register(RoofedForestTreeFeature.NAME, RoofedForestTreeFeature.class);
            this.register(DesertCactusFeature.NAME, DesertCactusFeature.class);
            this.register(DesertDeadBushFeature.NAME, DesertDeadBushFeature.class);
            this.register(TallGrassGenerateFeature.JUNGLE, TallGrassGenerateFeature.class);
            this.register(TallGrassGenerateFeature.PLAINS, TallGrassGenerateFeature.class);
            this.register(TallGrassGenerateFeature.SAVANNA, TallGrassGenerateFeature.class);
            this.register(DesertDryGrassGenerateFeature.NAME, DesertDryGrassGenerateFeature.class);
            this.register(MeadowTreeFeature.NAME, MeadowTreeFeature.class);
            this.register(IceSurfaceTreeFeature.NAME, IceSurfaceTreeFeature.class);
            this.register(MegaTaigaTreeFeature.NAME, MegaTaigaTreeFeature.class);
            this.register(CherryTreeFeature.NAME, CherryTreeFeature.class);
            this.register(BirchForestTreeFeature.NAME, BirchForestTreeFeature.class);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    public GenerateFeature get(Class<? extends GenerateFeature> c) {
        for (var entry : REGISTRY.entrySet()) {
            if (entry.getValue().equals(c)) {
                try {
                    return entry.getValue().getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    public boolean has(String key) {
        return REGISTRY.containsKey(key.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public GenerateFeature get(String key) {
        try {
            return REGISTRY.get(key.toLowerCase(Locale.ENGLISH)).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<? extends GenerateFeature> getStageByName(String key) {
        return REGISTRY.get(key.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public void reload() {
        isLoad.set(false);
        REGISTRY.clear();
        init();
    }

    @Override
    public void register(String key, Class<? extends GenerateFeature> value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key.toLowerCase(Locale.ENGLISH), value) != null) {
            throw new RegisterException("This generator has already been registered with the key: " + key);
        }
    }
}
