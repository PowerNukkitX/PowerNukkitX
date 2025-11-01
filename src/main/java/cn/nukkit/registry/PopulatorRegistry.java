package cn.nukkit.registry;

import cn.nukkit.level.generator.populator.generic.PopulatorRuinedPortal;
import cn.nukkit.level.generator.populator.nether.soulsand_valley.NetherFossilPopulator;
import cn.nukkit.level.generator.populator.normal.*;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.nether.*;
import cn.nukkit.level.generator.populator.nether.basalt_delta.BasaltDeltaLavaPopulator;
import cn.nukkit.level.generator.populator.nether.basalt_delta.BasaltDeltaMagmaPopulator;
import cn.nukkit.level.generator.populator.nether.basalt_delta.BasaltDeltaPillarPopulator;
import cn.nukkit.level.generator.populator.nether.crimson.CrimsonFungiTreePopulator;
import cn.nukkit.level.generator.populator.nether.crimson.CrimsonGrassesPopulator;
import cn.nukkit.level.generator.populator.nether.crimson.CrimsonWeepingVinesPopulator;
import cn.nukkit.level.generator.populator.nether.warped.WarpedFungiTreePopulator;
import cn.nukkit.level.generator.populator.nether.warped.WarpedGrassesPopulator;
import cn.nukkit.level.generator.populator.nether.warped.WarpedTwistingVinesPopulator;
import cn.nukkit.level.generator.populator.the_end.ChorusFlowerPopulator;
import cn.nukkit.level.generator.populator.the_end.EndGatewayPopulator;
import cn.nukkit.level.generator.populator.the_end.EndIslandPopulator;
import cn.nukkit.level.generator.populator.the_end.EnderDragonPopulator;
import cn.nukkit.level.generator.populator.the_end.ExitPortalPopulator;
import cn.nukkit.level.generator.populator.the_end.ObsidianPillarPopulator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class PopulatorRegistry implements IRegistry<String, Populator, Class<? extends Populator>> {
    private static final Object2ObjectOpenHashMap<String, Class<? extends Populator>> REGISTRY = new Object2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try {
            this.register(ObsidianPillarPopulator.NAME, ObsidianPillarPopulator.class);
            this.register(EnderDragonPopulator.NAME, EnderDragonPopulator.class);
            this.register(ExitPortalPopulator.NAME, ExitPortalPopulator.class);
            this.register(ChorusFlowerPopulator.NAME, ChorusFlowerPopulator.class);
            this.register(EndGatewayPopulator.NAME, EndGatewayPopulator.class);
            this.register(EndIslandPopulator.NAME, EndIslandPopulator.class);
            this.register(GlowstonePopulator.NAME, GlowstonePopulator.class);
            this.register(BasaltDeltaLavaPopulator.NAME, BasaltDeltaLavaPopulator.class);
            this.register(BasaltDeltaMagmaPopulator.NAME, BasaltDeltaMagmaPopulator.class);
            this.register(BasaltDeltaPillarPopulator.NAME, BasaltDeltaPillarPopulator.class);
            this.register(CrimsonFungiTreePopulator.NAME, CrimsonFungiTreePopulator.class);
            this.register(CrimsonGrassesPopulator.NAME, CrimsonGrassesPopulator.class);
            this.register(CrimsonWeepingVinesPopulator.NAME, CrimsonWeepingVinesPopulator.class);
            this.register(WarpedFungiTreePopulator.NAME, WarpedFungiTreePopulator.class);
            this.register(WarpedGrassesPopulator.NAME, WarpedGrassesPopulator.class);
            this.register(WarpedTwistingVinesPopulator.NAME, WarpedTwistingVinesPopulator.class);
            this.register(NetherGoldOrePopulator.NAME, NetherGoldOrePopulator.class);
            this.register(NetherQuartzPopulator.NAME, NetherQuartzPopulator.class);
            this.register(SoulsandPopulator.NAME, SoulsandPopulator.class);
            this.register(LavaOrePopulator.NAME, LavaOrePopulator.class);
            this.register(MagmaPopulator.NAME, MagmaPopulator.class);
            this.register(AncientDebrisSmallPopulator.NAME, AncientDebrisSmallPopulator.class);
            this.register(AncientDebrisLargePopulator.NAME, AncientDebrisLargePopulator.class);
            this.register(FirePopulator.NAME, FirePopulator.class);
            this.register(LavaPopulator.NAME, LavaPopulator.class);
            this.register(NetherGravelPopulator.NAME, NetherGravelPopulator.class);
            this.register(NetherBlackstonePopulator.NAME, NetherBlackstonePopulator.class);
            this.register(DesertWellPopulator.NAME, DesertWellPopulator.class);
            this.register(DesertPyramidPopulator.NAME, DesertPyramidPopulator.class);
            this.register(FossilPopulator.NAME, FossilPopulator.class);
            this.register(IglooPopulator.NAME, IglooPopulator.class);
            this.register(MineshaftPopulator.NAME, MineshaftPopulator.class);
            this.register(JungleTemplePopulator.NAME, JungleTemplePopulator.class);
            this.register(NetherFortressPopulator.NAME, NetherFortressPopulator.class);
            this.register(OceanMonumentPopulator.NAME, OceanMonumentPopulator.class);
            this.register(OceanRuinPopulator.NAME, OceanRuinPopulator.class);
            this.register(PillagerOutpostPopulator.NAME, PillagerOutpostPopulator.class);
            this.register(ShipwreckPopulator.NAME, ShipwreckPopulator.class);
            this.register(StrongholdPopulator.NAME, StrongholdPopulator.class);
            this.register(SwampHutPopulator.NAME, SwampHutPopulator.class);
            this.register(PopulatorRuinedPortal.NAME, PopulatorRuinedPortal.class);
            this.register(NetherFossilPopulator.NAME, NetherFossilPopulator.class);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    public Populator get(Class<? extends Populator> c) {
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

    @Override
    public Populator get(String key) {
        try {
            return REGISTRY.get(key.toLowerCase(Locale.ENGLISH)).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<? extends Populator> getStageByName(String key) {
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
    public void register(String key, Class<? extends Populator> value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key.toLowerCase(Locale.ENGLISH), value) != null) {
            throw new RegisterException("This generator has already been registered with the key: " + key);
        }
    }
}
