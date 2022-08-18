package cn.nukkit.level.generator.populator.type;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.impl.structure.village.populator.PopulatorVillage;
import cn.nukkit.math.NukkitRandom;

import java.util.ArrayList;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public abstract class PopulatorStructure extends Populator {

    protected static List<PopulatorStructure> STRUCTURE_POPULATORS = new ArrayList<>();

    static{
        STRUCTURE_POPULATORS.add(new PopulatorVillage());
    }

    public static List<PopulatorStructure> getPopulators() {
        return STRUCTURE_POPULATORS;
    }

    public static void addPopulator(PopulatorStructure populator) {
        STRUCTURE_POPULATORS.add(populator);
    }

    public static void removePopulator(PopulatorStructure populator) {
        STRUCTURE_POPULATORS.remove(populator);
    }

    public static void clearPopulators() {
        STRUCTURE_POPULATORS.clear();
    }

    public static void populateAll(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        for (PopulatorStructure populator : STRUCTURE_POPULATORS) {
            populator.populate(level, chunkX, chunkZ, random, chunk);
        }
    }

    public boolean isAsync() {
        return false;
    }
}
