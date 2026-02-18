package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectJungleBush;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class JungleBushFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:jungle_bush";

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectJungleBush();
    }

    @Override
    public boolean canSpawnHere(BiomeDefinitionData definition) {
        return definition.getTags().contains(BiomeTags.JUNGLE);
    }


    @Override
    public String name() {
        return NAME;
    }
}
