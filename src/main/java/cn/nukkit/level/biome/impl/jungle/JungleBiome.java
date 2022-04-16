package cn.nukkit.level.biome.impl.jungle;

import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorVines;
import cn.nukkit.level.generator.populator.impl.tree.JungleBigTreePopulator;
import cn.nukkit.level.generator.populator.impl.tree.JungleTreePopulator;

/**
 * @author DaPorkchop_
 */
public class JungleBiome extends GrassyBiome {

    public JungleBiome() {
        super();

        JungleTreePopulator trees = new JungleTreePopulator();
        trees.setBaseAmount(10);
        this.addPopulator(trees);

        JungleBigTreePopulator bigTrees = new JungleBigTreePopulator();
        bigTrees.setBaseAmount(6);
        this.addPopulator(bigTrees);

        PopulatorVines vines = new PopulatorVines();
        vines.setBaseAmount(5);
        vines.setRandomAmount(3);
        this.addPopulator(vines);
    }

    @Override
    public String getName() {
        return "Jungle";
    }
}
