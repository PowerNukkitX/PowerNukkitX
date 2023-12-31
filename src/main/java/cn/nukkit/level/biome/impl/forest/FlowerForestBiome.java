package cn.nukkit.level.biome.impl.forest;

import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.block.BlockRedFlower;
import cn.nukkit.level.generator.populator.impl.PopulatorFlower;

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
public class FlowerForestBiome extends ForestBiome {
    public FlowerForestBiome() {
        this(TYPE_NORMAL);
    }

    public FlowerForestBiome(int type) {
        super(type);

        //see https://minecraft.wiki/w/Flower#Flower_biomes
        PopulatorFlower flower = new PopulatorFlower();
        flower.setBaseAmount(10);
        flower.addType(DANDELION, 0);
        flower.addType(RED_FLOWER, BlockRedFlower.TYPE_POPPY);
        flower.addType(RED_FLOWER, BlockRedFlower.TYPE_ALLIUM);
        flower.addType(RED_FLOWER, BlockRedFlower.TYPE_AZURE_BLUET);
        flower.addType(RED_FLOWER, BlockRedFlower.TYPE_RED_TULIP);
        flower.addType(RED_FLOWER, BlockRedFlower.TYPE_ORANGE_TULIP);
        flower.addType(RED_FLOWER, BlockRedFlower.TYPE_WHITE_TULIP);
        flower.addType(RED_FLOWER, BlockRedFlower.TYPE_PINK_TULIP);
        flower.addType(RED_FLOWER, BlockRedFlower.TYPE_OXEYE_DAISY);
        flower.addType(RED_FLOWER, BlockRedFlower.TYPE_CORNFLOWER);
        flower.addType(RED_FLOWER, BlockRedFlower.TYPE_LILY_OF_THE_VALLEY);
        flower.addType(DOUBLE_PLANT, BlockDoublePlant.LILAC);
        flower.addType(DOUBLE_PLANT, BlockDoublePlant.ROSE_BUSH);
        flower.addType(DOUBLE_PLANT, BlockDoublePlant.PEONY);
        this.addPopulator(flower);

        this.setHeightVariation(0.4f);
    }

    @Override
    public String getName() {
        return this.type == TYPE_BIRCH ? "Birch Forest" : "Forest";
    }
}
