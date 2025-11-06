package cn.nukkit.level.generator.stages.nether;

import cn.nukkit.level.generator.populator.generic.PopulatorRuinedPortal;
import cn.nukkit.level.generator.populator.nether.*;
import cn.nukkit.level.generator.populator.nether.basalt_delta.BasaltDeltaLavaPopulator;
import cn.nukkit.level.generator.populator.nether.basalt_delta.BasaltDeltaMagmaPopulator;
import cn.nukkit.level.generator.populator.nether.basalt_delta.BasaltDeltaPillarPopulator;
import cn.nukkit.level.generator.populator.nether.crimson.CrimsonFungiTreePopulator;
import cn.nukkit.level.generator.populator.nether.crimson.CrimsonGrassesPopulator;
import cn.nukkit.level.generator.populator.nether.crimson.CrimsonWeepingVinesPopulator;
import cn.nukkit.level.generator.populator.nether.soulsand_valley.NetherFossilPopulator;
import cn.nukkit.level.generator.populator.nether.warped.WarpedFungiTreePopulator;
import cn.nukkit.level.generator.populator.nether.warped.WarpedGrassesPopulator;
import cn.nukkit.level.generator.populator.nether.warped.WarpedTwistingVinesPopulator;
import cn.nukkit.level.generator.stages.PopulatorStage;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

public class NetherPopulatorStage extends PopulatorStage {

    public static final String NAME = "nether_populator";

    public static final ObjectArraySet<String> POPULATORS = new ObjectArraySet<>(new String[] {
            GlowstonePopulator.NAME,
            SoulsandPopulator.NAME,
            MagmaPopulator.NAME,
            LavaOrePopulator.NAME,
            FirePopulator.NAME,
            LavaPopulator.NAME,
            NetherGoldOrePopulator.NAME,
            AncientDebrisSmallPopulator.NAME,
            AncientDebrisLargePopulator.NAME,
            NetherQuartzPopulator.NAME,
            BasaltDeltaLavaPopulator.NAME,
            BasaltDeltaPillarPopulator.NAME,
            BasaltDeltaMagmaPopulator.NAME,
            CrimsonFungiTreePopulator.NAME,
            CrimsonGrassesPopulator.NAME,
            CrimsonWeepingVinesPopulator.NAME,
            WarpedFungiTreePopulator.NAME,
            WarpedGrassesPopulator.NAME,
            WarpedTwistingVinesPopulator.NAME,
            NetherBlackstonePopulator.NAME,
            NetherGravelPopulator.NAME,
            NetherFortressPopulator.NAME,
            PopulatorRuinedPortal.NAME,
            NetherFossilPopulator.NAME
    });

    @Override
    public ObjectArraySet<String> populators() {
        return POPULATORS;
    }

    @Override
    public String name() {
        return NAME;
    }
}
