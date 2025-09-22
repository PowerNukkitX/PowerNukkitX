package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.level.generator.populator.normal.FossilPopulator;
import cn.nukkit.level.generator.populator.normal.IglooPopulator;
import cn.nukkit.level.generator.populator.normal.DesertPyramidPopulator;
import cn.nukkit.level.generator.populator.normal.DesertWellPopulator;
import cn.nukkit.level.generator.populator.normal.JungleTemplePopulator;
import cn.nukkit.level.generator.stages.PopulatorStage;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

public class NormalPopulatorStage extends PopulatorStage {

    public static final String NAME = "normal_populator";

    public static final ObjectArraySet<String> POPULATORS = new ObjectArraySet<>(new String[] {
            DesertWellPopulator.NAME,
            DesertPyramidPopulator.NAME,
            FossilPopulator.NAME,
            IglooPopulator.NAME,
            JungleTemplePopulator.NAME
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
