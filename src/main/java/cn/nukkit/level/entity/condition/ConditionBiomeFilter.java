package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;
import cn.nukkit.registry.Registries;

import java.util.Arrays;

public class ConditionBiomeFilter extends Condition {

    public final String[] biomeTag;

    public ConditionBiomeFilter(String... biomeTag) {
        super("minecraft:biome_filter");
        this.biomeTag = biomeTag;
    }

    @Override
    public boolean evaluate(Block block) {
        int biomeId = block.getLevel().getBiomeId(block.getFloorX(), block.getFloorY(), block.getFloorZ());
        return Arrays.stream(biomeTag).anyMatch(tag -> Registries.BIOME.get(biomeId).getTags().contains(tag));
    }
}
