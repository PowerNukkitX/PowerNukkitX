package cn.nukkit.level.generator.populator.type;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.generator.populator.impl.structure.desertpyramid.PopulatorDesertPyramid;
import cn.nukkit.level.generator.populator.impl.structure.fossil.PopulatorFossil;
import cn.nukkit.level.generator.populator.impl.structure.igloo.PopulatorIgloo;
import cn.nukkit.level.generator.populator.impl.structure.jungletemple.PopulatorJungleTemple;
import cn.nukkit.level.generator.populator.impl.structure.mineshaft.PopulatorMineshaft;
import cn.nukkit.level.generator.populator.impl.structure.netherfortress.populator.PopulatorNetherFortress;
import cn.nukkit.level.generator.populator.impl.structure.oceanmonument.populator.PopulatorOceanMonument;
import cn.nukkit.level.generator.populator.impl.structure.oceanruin.PopulatorOceanRuin;
import cn.nukkit.level.generator.populator.impl.structure.pillageroutpost.PopulatorPillagerOutpost;
import cn.nukkit.level.generator.populator.impl.structure.quasi.populator.PopulatorDesertWell;
import cn.nukkit.level.generator.populator.impl.structure.quasi.populator.PopulatorDungeon;
import cn.nukkit.level.generator.populator.impl.structure.shipwreck.PopulatorShipwreck;
import cn.nukkit.level.generator.populator.impl.structure.stronghold.populator.PopulatorStronghold;
import cn.nukkit.level.generator.populator.impl.structure.swamphut.PopulatorSwampHut;
import cn.nukkit.level.generator.populator.impl.structure.village.populator.PopulatorVillage;

import java.util.ArrayList;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public abstract class PopulatorStructure extends Populator {

    protected static List<Class<? extends PopulatorStructure>> STRUCTURE_POPULATORS = new ArrayList<>();

    static{
        STRUCTURE_POPULATORS.add(PopulatorVillage.class);
        STRUCTURE_POPULATORS.add(PopulatorOceanMonument.class);
        STRUCTURE_POPULATORS.add(PopulatorDesertWell.class);
        STRUCTURE_POPULATORS.add(PopulatorDungeon.class);
        STRUCTURE_POPULATORS.add(PopulatorStronghold.class);
        STRUCTURE_POPULATORS.add(PopulatorNetherFortress.class);
        STRUCTURE_POPULATORS.add(PopulatorShipwreck.class);
        STRUCTURE_POPULATORS.add(PopulatorFossil.class);
        STRUCTURE_POPULATORS.add(PopulatorDesertPyramid.class);
        STRUCTURE_POPULATORS.add(PopulatorJungleTemple.class);
        STRUCTURE_POPULATORS.add(PopulatorSwampHut.class);
        STRUCTURE_POPULATORS.add(PopulatorMineshaft.class);
        STRUCTURE_POPULATORS.add(PopulatorIgloo.class);
        STRUCTURE_POPULATORS.add(PopulatorOceanRuin.class);
        STRUCTURE_POPULATORS.add(PopulatorPillagerOutpost.class);
    }

    public static List<Class<? extends PopulatorStructure>> getPopulators() {
        return STRUCTURE_POPULATORS;
    }

    public static void addPopulator(Class<? extends PopulatorStructure> populator) {
        STRUCTURE_POPULATORS.add(populator);
    }

    public static void removePopulator(Class<? extends PopulatorStructure> populator) {
        STRUCTURE_POPULATORS.remove(populator);
    }

    public static void clearPopulators() {
        STRUCTURE_POPULATORS.clear();
    }

    /**
     * 若返回值为true,则将会使用{@link cn.nukkit.level.generator.task.ChunkPopulationTask}异步生成结构
     * @return boolean
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    public boolean isAsync() {
        return false;
    }
}
