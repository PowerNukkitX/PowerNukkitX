package cn.nukkit.level.biome.impl.jungle;

import cn.nukkit.level.generator.populator.impl.PopulatorBamboo;
import cn.nukkit.level.generator.populator.impl.PopulatorVines;
import cn.nukkit.level.generator.populator.impl.tree.JungleBigTreePopulator;

/**
 * @author GoodLucky777
 */
public class BambooJungleBiome extends JungleBiome {

    public BambooJungleBiome() {
        PopulatorBamboo bamboo = new PopulatorBamboo();
        bamboo.setBaseAmount(80);
        bamboo.setRandomAmount(30);
        this.addPopulator(bamboo);
        
        JungleBigTreePopulator jungleBigTrees = new JungleBigTreePopulator();
        jungleBigTrees.setBaseAmount(-1);
        jungleBigTrees.setRandomAmount(2);
        this.addPopulator(jungleBigTrees);
        
        /*PopulatorVines vines = new PopulatorVines();
        vines.setBaseAmount(7);
        vines.setRandomAmount(4);
        this.addPopulator(vines);*/
    }
    
    @Override
    public String getName() {
        return "Bamboo Jungle";
    }
}
