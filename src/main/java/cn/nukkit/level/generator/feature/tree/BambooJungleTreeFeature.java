package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBamboo;
import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectJungleBigTree;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

public class BambooJungleTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:bamboo_jungle_surface_trees_feature";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectJungleBigTree(10, 20);
    }

    @Override
    public boolean canSpawnHere(BiomeDefinitionData definition) {
        return definition.getTags().contains(BiomeTags.BAMBOO);
    }

    @Override
    public int getMin() {
        return 10;
    }

    @Override
    public int getMax() {
        return 11;
    }

    @Override
    protected boolean checkBlock(Block bl) {
        return super.checkBlock(bl) && !(bl instanceof BlockBamboo);
    }

    @Override
    public String name() {
        return NAME;
    }
}
