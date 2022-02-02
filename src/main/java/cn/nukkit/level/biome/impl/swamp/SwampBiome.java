package cn.nukkit.level.biome.impl.swamp;

import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.MushroomPopulator;
import cn.nukkit.level.generator.populator.impl.PopulatorDisk;
import cn.nukkit.level.generator.populator.impl.PopulatorFlower;
import cn.nukkit.level.generator.populator.impl.PopulatorLilyPad;
import cn.nukkit.level.generator.populator.impl.PopulatorSeagrass;
import cn.nukkit.level.generator.populator.impl.PopulatorSmallMushroom;
import cn.nukkit.level.generator.populator.impl.tree.SwampTreePopulator;

import java.util.Arrays;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class SwampBiome extends GrassyBiome {

    public SwampBiome() {
        super();
        
        PopulatorDisk populatorDiskClay = new PopulatorDisk(1.0, BlockState.of(CLAY_BLOCK), 1, 2, 1, Arrays.asList(BlockState.of(DIRT), BlockState.of(CLAY_BLOCK)));
        populatorDiskClay.setBaseAmount(1);
        addPopulator(populatorDiskClay);
        
        PopulatorLilyPad lilypad = new PopulatorLilyPad();
        lilypad.setBaseAmount(4);
        lilypad.setRandomAmount(2);
        this.addPopulator(lilypad);

        SwampTreePopulator trees = new SwampTreePopulator();
        trees.setBaseAmount(2);
        this.addPopulator(trees);
        
        PopulatorSeagrass populatorSeagrass = new PopulatorSeagrass(0.6);
        populatorSeagrass.setBaseAmount(32);
        populatorSeagrass.setBaseAmount(32);
        addPopulator(populatorSeagrass);

        PopulatorFlower flower = new PopulatorFlower();
        flower.setBaseAmount(2);
        flower.addType(Block.RED_FLOWER, BlockFlower.TYPE_BLUE_ORCHID);
        this.addPopulator(flower);

        MushroomPopulator mushroom = new MushroomPopulator(1);
        mushroom.setBaseAmount(-5);
        mushroom.setRandomAmount(7);
        this.addPopulator(mushroom);

        PopulatorSmallMushroom smallMushroom = new PopulatorSmallMushroom();
        smallMushroom.setBaseAmount(0);
        smallMushroom.setRandomAmount(2);
        this.addPopulator(smallMushroom);

        this.setBaseHeight(-0.2f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Swamp";
    }
}
