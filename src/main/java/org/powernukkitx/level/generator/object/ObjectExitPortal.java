package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockBedrock;
import org.powernukkitx.block.BlockEndPortal;
import org.powernukkitx.block.BlockEndStone;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockTorch;
import org.powernukkitx.block.property.enums.TorchFacingDirection;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

import static org.powernukkitx.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;

public class ObjectExitPortal extends ObjectGenerator {

    protected static final BlockState BEDROCK = BlockBedrock.PROPERTIES.getDefaultState();
    protected static final BlockState END_STONE = BlockEndStone.PROPERTIES.getDefaultState();
    protected static final BlockState END_PORTAL = BlockEndPortal.PROPERTIES.getDefaultState();

    private final boolean active;

    public ObjectExitPortal(boolean active) {
        this.active = active;
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 pos) {

        int originX = pos.getFloorX();
        int originY = pos.getFloorY();
        int originZ = pos.getFloorZ();
        for (int x = -4; x <= 4; x++) {
            for (int y = -1; y <= 32; y++) {
                for (int z = -4; z <= 4; z++) {
                    int distanceSquared = x * x + y * y + z * z;
                    if (distanceSquared >= 13) {
                        continue;
                    }
                    if (y < 0) {
                        level.setBlockStateAt(originX + x, originY + y, originZ + z, distanceSquared < 7 ? BEDROCK : END_STONE);
                    } else if (y > 0) {
                        level.setBlockStateAt(originX + x, originY + y, originZ + z, BlockAir.STATE);
                    } else if (distanceSquared >= 7) {
                        level.setBlockStateAt(originX + x, originY, originZ + z, BEDROCK);
                    } else {
                        level.setBlockStateAt(originX + x, originY, originZ + z, active ? END_PORTAL : BlockAir.STATE);
                    }
                }
            }
        }

        for (int y = 0; y < 4; y++) {
            level.setBlockStateAt(originX, originY + y, originZ, BEDROCK);
        }
        for (BlockFace face : BlockFace.getHorizontals()) {
            level.setBlockStateAt(originX + face.getXOffset(), originY + 2, originZ + face.getZOffset(),
                    BlockTorch.PROPERTIES.getBlockState(TORCH_FACING_DIRECTION.createValue(TorchFacingDirection.getByTorchDirection(face))));
        }

        return true;
    }
}
