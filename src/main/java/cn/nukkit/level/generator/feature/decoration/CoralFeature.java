package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.random.RandomSourceProvider;

public class CoralFeature extends AbstractCoralFeature {
    public static final String NAME = "minecraft:coral_feature";

    private static final CoralTreeFeature TREE = new CoralTreeFeature();
    private static final CoralClawFeature CLAW = new CoralClawFeature();
    private static final CoralMushroomFeature MUSHROOM = new CoralMushroomFeature();

    @Override
    public int getBase() {
        return 1;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected boolean placeFeature(IChunk chunk, RandomSourceProvider random, int x, int y, int z, BlockState coralState) {
        int choice = random.nextInt(3);
        if (choice == 0) {
            return TREE.placeFeature(chunk, random, x, y, z, coralState);
        }
        if (choice == 1) {
            return CLAW.placeFeature(chunk, random, x, y, z, coralState);
        }
        return MUSHROOM.placeFeature(chunk, random, x, y, z, coralState);
    }
}
