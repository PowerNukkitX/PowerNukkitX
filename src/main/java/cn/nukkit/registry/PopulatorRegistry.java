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
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class PopulatorRegistry implements IRegistry<String, Populator, Class<? extends Populator>> {
    private static final Object2ObjectOpenHashMap<String, Class<? extends Populator>> REGISTRY = new Object2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        this.register0(ObsidianPillarPopulator.NAME, ObsidianPillarPopulator.class);
        this.register0(EnderDragonPopulator.NAME, EnderDragonPopulator.class);
        this.register0(ExitPortalPopulator.NAME, ExitPortalPopulator.class);
        this.register0(ChorusFlowerPopulator.NAME, ChorusFlowerPopulator.class);
        this.register0(EndGatewayPopulator.NAME, EndGatewayPopulator.class);
        this.register0(EndIslandPopulator.NAME, EndIslandPopulator.class);
        this.register0(GlowstonePopulator.NAME, GlowstonePopulator.class);
        this.register0(BasaltDeltaLavaPopulator.NAME, BasaltDeltaLavaPopulator.class);
        this.register0(BasaltDeltaMagmaPopulator.NAME, BasaltDeltaMagmaPopulator.class);
        this.register0(BasaltDeltaPillarPopulator.NAME, BasaltDeltaPillarPopulator.class);
        this.register0(CrimsonFungiTreePopulator.NAME, CrimsonFungiTreePopulator.class);
        this.register0(CrimsonGrassesPopulator.NAME, CrimsonGrassesPopulator.class);
        this.register0(CrimsonWeepingVinesPopulator.NAME, CrimsonWeepingVinesPopulator.class);
        this.register0(WarpedFungiTreePopulator.NAME, WarpedFungiTreePopulator.class);
        this.register0(WarpedGrassesPopulator.NAME, WarpedGrassesPopulator.class);
        this.register0(WarpedTwistingVinesPopulator.NAME, WarpedTwistingVinesPopulator.class);
        this.register0(NetherGoldOrePopulator.NAME, NetherGoldOrePopulator.class);
        this.register0(NetherQuartzPopulator.NAME, NetherQuartzPopulator.class);
        this.register0(SoulsandPopulator.NAME, SoulsandPopulator.class);
        this.register0(LavaOrePopulator.NAME, LavaOrePopulator.class);
        this.register0(MagmaPopulator.NAME, MagmaPopulator.class);
        this.register0(AncientDebrisSmallPopulator.NAME, AncientDebrisSmallPopulator.class);
        this.register0(AncientDebrisLargePopulator.NAME, AncientDebrisLargePopulator.class);
        this.register0(FirePopulator.NAME, FirePopulator.class);
        this.register0(LavaPopulator.NAME, LavaPopulator.class);
        this.register0(NetherGravelPopulator.NAME, NetherGravelPopulator.class);
        this.register0(NetherBlackstonePopulator.NAME, NetherBlackstonePopulator.class);
        this.register0(DesertWellPopulator.NAME, DesertWellPopulator.class);
        this.register0(DesertPyramidPopulator.NAME, DesertPyramidPopulator.class);
        this.register0(FossilPopulator.NAME, FossilPopulator.class);
        this.register0(IglooPopulator.NAME, IglooPopulator.class);
        this.register0(MineshaftPopulator.NAME, MineshaftPopulator.class);
        this.register0(JungleTemplePopulator.NAME, JungleTemplePopulator.class);
        this.register0(NetherFortressPopulator.NAME, NetherFortressPopulator.class);
        this.register0(OceanMonumentPopulator.NAME, OceanMonumentPopulator.class);
        this.register0(OceanRuinPopulator.NAME, OceanRuinPopulator.class);
        this.register0(PillagerOutpostPopulator.NAME, PillagerOutpostPopulator.class);
        this.register0(ShipwreckPopulator.NAME, ShipwreckPopulator.class);
        this.register0(StrongholdPopulator.NAME, StrongholdPopulator.class);
        this.register0(SwampHutPopulator.NAME, SwampHutPopulator.class);
        this.register0(PopulatorRuinedPortal.NAME, PopulatorRuinedPortal.class);
        this.register0(NetherFossilPopulator.NAME, NetherFossilPopulator.class);
    }

    public Populator get(Class<? extends Populator> c) {
        for (var entry : REGISTRY.entrySet()) {
            if (entry.getValue().equals(c)) {
                try {
                    return entry.getValue().getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    log.error("Error getting Populator: ", e);
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

    private void register0(String key, Class<? extends Populator> value) {
        try {
            register(key, value);
        } catch (RegisterException e) {
            log.error("Error registering Block: ", e);
        }
    }
}
