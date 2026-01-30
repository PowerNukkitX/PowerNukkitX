package cn.nukkit.registry;

import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.feature.decoration.*;
import cn.nukkit.level.generator.feature.multi.JunglePlantFeature;
import cn.nukkit.level.generator.feature.multi.OverworldCaveCarverFeature;
import cn.nukkit.level.generator.feature.multi.RandomRoofedForestFeatureWithDecorationFeature;
import cn.nukkit.level.generator.feature.multi.SwampFoliageFeature;
import cn.nukkit.level.generator.feature.ore.*;
import cn.nukkit.level.generator.feature.river.ClayGenerateFeature;
import cn.nukkit.level.generator.feature.river.GravelGenerateFeature;
import cn.nukkit.level.generator.feature.river.SandGenerateFeature;
import cn.nukkit.level.generator.feature.terrain.CaveGenerateFeature;
import cn.nukkit.level.generator.feature.tree.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.SneakyThrows;
import me.sunlan.fastreflection.FastConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GenerateFeatureRegistry implements IRegistry<String, GenerateFeature, Class<? extends GenerateFeature>> {
    private static final Object2ObjectOpenHashMap<String, FastConstructor<? extends GenerateFeature>> REGISTRY = new Object2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try {
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
            this.register(JungleTreeFeature.NAME, JungleTreeFeature.class);
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
            this.register(ForestFlowerFoliageFeature.NAME, ForestFlowerFoliageFeature.class);
            this.register(SeagrassRiverGenerateFeature.NAME, SeagrassRiverGenerateFeature.class);
            this.register(MushroomIslandMushroomFeature.NAME, MushroomIslandMushroomFeature.class);
            this.register(ReedsFeature.NAME, ReedsFeature.class);
            this.register(PlainsTreeFeature.NAME, PlainsTreeFeature.class);
            this.register(RoofedForestTreeFeature.NAME, RoofedForestTreeFeature.class);
            this.register(DesertCactusFeature.NAME, DesertCactusFeature.class);
            this.register(DeadBushFeature.NAME, DeadBushFeature.class);
            this.register(TallGrassGenerateFeature.NAME, TallGrassGenerateFeature.class);
            this.register(TallGrassPatchFeature.NAME, TallGrassPatchFeature.class);
            this.register(ScatterDryGrassFeature.NAME, ScatterDryGrassFeature.class);
            this.register(MeadowTreeFeature.NAME, MeadowTreeFeature.class);
            this.register(IceSurfaceTreeFeature.NAME, IceSurfaceTreeFeature.class);
            this.register(MegaTaigaTreeFeature.NAME, MegaTaigaTreeFeature.class);
            this.register(CherryTreeFeature.NAME, CherryTreeFeature.class);
            this.register(BirchForestTreeFeature.NAME, BirchForestTreeFeature.class);
            this.register(ForestFoliageFeature.NAME, ForestFoliageFeature.class);
            this.register(PinkPetalsFeature.NAME, PinkPetalsFeature.class);
            this.register(AmethystGeodeFeature.NAME, AmethystGeodeFeature.class);
            this.register(GroveTreeFeature.NAME, GroveTreeFeature.class);
            this.register(BirchForestMutatedTreeFeature.NAME, BirchForestMutatedTreeFeature.class);
            this.register(BirchForestWildflowersFeature.NAME, BirchForestWildflowersFeature.class);
            this.register(JungleGrassFeature.NAME, JungleGrassFeature.class);
            this.register(TaigaGrassFeature.NAME, TaigaGrassFeature.class);
            this.register(TallFernPatchFeature.NAME, TallFernPatchFeature.class);
            this.register(BambooForestBambooFeature.NAME, BambooForestBambooFeature.class);
            this.register(PumpkinGenerateFeature.NAME, PumpkinGenerateFeature.class);
            this.register(ForestRockFeature.NAME, ForestRockFeature.class);
            this.register(PaleGardenTreeFeature.NAME, PaleGardenTreeFeature.class);
            this.register(PaleMossPatchFeature.NAME, PaleMossPatchFeature.class);
            this.register(EyeBlossomFeature.NAME, EyeBlossomFeature.class);
            this.register(HugeMushroomFeature.NAME, HugeMushroomFeature.class);
            this.register(RandomRoofedForestFeatureWithDecorationFeature.NAME, RandomRoofedForestFeatureWithDecorationFeature.class);
            this.register(BushFeature.NAME, BushFeature.class);
            this.register(JungleBushFeature.NAME, JungleBushFeature.class);
            this.register(JunglePlantFeature.NAME, JunglePlantFeature.class);
            this.register(SwampTreeFeature.NAME, SwampTreeFeature.class);
            this.register(SwampFoliageFeature.NAME, SwampFoliageFeature.class);
            this.register(SwampSeagrassFeature.NAME, SwampSeagrassFeature.class);
            this.register(OceanSeagrassFeature.NAME, OceanSeagrassFeature.class);
            this.register(WaterlilyFeature.NAME, WaterlilyFeature.class);
            this.register(FireflyBushClusterFeature.NAME, FireflyBushClusterFeature.class);
            this.register(SwampFlowerFeature.NAME, SwampFlowerFeature.class);
            this.register(ScatterRedMushroomFeature.NAME, ScatterRedMushroomFeature.class);
            this.register(ScatterBrownMushroomFeature.NAME, ScatterBrownMushroomFeature.class);
            this.register(ScatterPlainsFlowerFeature.NAME, ScatterPlainsFlowerFeature.class);
            this.register(SunflowerDouplePlantPatchFeature.NAME, SunflowerDouplePlantPatchFeature.class);
            this.register(SavannaMutatedTreeFeature.NAME, SavannaMutatedTreeFeature.class);
            this.register(KelpFeature.NAME, KelpFeature.class);
            this.register(WarmOceanSeagrassFeature.NAME, WarmOceanSeagrassFeature.class);
            this.register(MesaPlateauStoneTreeFeature.NAME, MesaPlateauStoneTreeFeature.class);
            this.register(MesaTreeFeature.NAME, MesaTreeFeature.class);
            this.register(MesaFoliageFeature.NAME, MesaFoliageFeature.class);
            this.register(TaigaTreeFeature.NAME, TaigaTreeFeature.class);
            this.register(ScatterSweetBerryBushFeature.NAME, ScatterSweetBerryBushFeature.class);
            this.register(FireflyBushWaterClusterFeature.NAME, FireflyBushWaterClusterFeature.class);
            this.register(OverworldCaveCarverFeature.NAME, OverworldCaveCarverFeature.class);
            this.register(MonsterRoomFeature.NAME, MonsterRoomFeature.class);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public GenerateFeature get(Class<? extends GenerateFeature> c) {
        for (Map.Entry<String, FastConstructor<? extends GenerateFeature>> entry : REGISTRY.entrySet()) {
            if (entry.getValue().getDeclaringClass().getRawClass().equals(c)) {
                try {
                    return (GenerateFeature) entry.getValue().invoke();
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

    @SneakyThrows
    @Override
    public GenerateFeature get(String key) {
        try {
            return (GenerateFeature) REGISTRY.get(key.toLowerCase(Locale.ENGLISH)).invoke();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public FastConstructor<? extends GenerateFeature> getStageByName(String key) {
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

    @SneakyThrows
    @Override
    public void register(String key, Class<? extends GenerateFeature> value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key.toLowerCase(Locale.ENGLISH), FastConstructor.create(value.getConstructor())) != null) {
            throw new RegisterException("This generator has already been registered with the key: " + key);
        }
    }
}
