package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.object.legacytree.LegacySpruceTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTreeGenerator;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;

public class IceSurfaceTreeFeature extends PlainsTreeFeature {

    public static final String NAME = "minecraft:ice_surface_trees_feature";

    @Override
    public LegacyTreeGenerator getGenerator(NukkitRandom random) {
        if(random.nextInt(20) < 1) {
            return new LegacySpruceTree();
        } else return null;
    }

    @Override
    public String getRequiredTag() {
        return BiomeTags.FROZEN;
    }

    @Override
    public String name() {
        return NAME;
    }
}
