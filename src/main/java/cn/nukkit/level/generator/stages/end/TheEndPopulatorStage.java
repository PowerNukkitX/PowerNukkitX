package cn.nukkit.level.generator.stages.end;

import cn.nukkit.level.generator.populator.the_end.ChorusFlowerPopulator;
import cn.nukkit.level.generator.populator.the_end.EndGatewayPopulator;
import cn.nukkit.level.generator.populator.the_end.EndIslandPopulator;
import cn.nukkit.level.generator.populator.the_end.EnderDragonPopulator;
import cn.nukkit.level.generator.populator.the_end.ExitPortalPopulator;
import cn.nukkit.level.generator.populator.the_end.ObsidianPillarPopulator;
import cn.nukkit.level.generator.stages.PopulatorStage;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

public class TheEndPopulatorStage extends PopulatorStage {

    public static final String NAME = "the_end_populator";

    public static final ObjectArraySet<String> POPULATORS = new ObjectArraySet<>(new String[] {
            ObsidianPillarPopulator.NAME,
            EnderDragonPopulator.NAME,
            ExitPortalPopulator.NAME,
            ChorusFlowerPopulator.NAME,
            EndGatewayPopulator.NAME,
            EndIslandPopulator.NAME
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
