package cn.nukkit.level.generator.stages.end;

import cn.nukkit.level.generator.populator.the_end.EnderDragonPopulator;
import cn.nukkit.level.generator.populator.the_end.ObsidianPillarPopulator;
import cn.nukkit.level.generator.stages.PopulatorStage;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

public class TheEndPopulatorStage extends PopulatorStage {

    public static final String NAME = "the_end_populator";

    public static final ObjectArraySet<String> POPULATORS = new ObjectArraySet<>(new String[] {
            ObsidianPillarPopulator.NAME,
            EnderDragonPopulator.NAME
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
