package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.HashUtils;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 * @since 11.4.2017
 */
@Slf4j
public class BlockEntityMovingBlock extends BlockEntitySpawnable {
    protected Block block;
    protected BlockVector3 piston;
    //true if the piston is extending instead of withdrawing.
    protected boolean expanding;

    public BlockEntityMovingBlock(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    /**
     * {
     * expanding: 0b,
     * id: MovingBlock,
     * isMovable: 1b,
     * movingBlock: {
     * name: "minecraft:air",
     * states: {
     * },
     * version: 18100737
     * },
     * movingBlockExtra: {
     * name: "minecraft:air",
     * states: {
     * },
     * version: 18100737
     * },
     * pistonPosX: 0,
     * pistonPosY: -1,
     * pistonPosZ: 0,
     * x: 0,
     * y: 100,
     * z: 0
     * }
     */
    @Override
    public void loadNBT() {
        super.loadNBT();
        if (namedTag.containsKey("movingBlock")) {
            NbtMap movingBlock = namedTag.getCompound("movingBlock");
            int blockhash = HashUtils.fnv1a_32_nbt_palette(movingBlock);
            BlockState blockState = Registries.BLOCKSTATE.get(blockhash);
            if(blockState==null){
                log.error("Can't load moving block {}",movingBlock);
            }else{
                this.block = blockState.toBlock();
            }
            this.block.x = x;
            this.block.y = y;
            this.block.z = z;
        } else {
            this.close();
        }

        if (namedTag.containsKey("pistonPosX") && namedTag.containsKey("pistonPosY") && namedTag.containsKey("pistonPosZ")) {
            this.piston = new BlockVector3(namedTag.getInt("pistonPosX"), namedTag.getInt("pistonPosY"), namedTag.getInt("pistonPosZ"));
        } else {
            this.piston = new BlockVector3(0, -1, 0);
        }
    }

    public @Nullable NbtMap getMovingBlockEntityCompound() {
        if (this.namedTag.containsKey("movingEntity")) {
            return this.namedTag.getCompound("movingEntity");
        }

        return null;
    }

    public Block getMovingBlock() {
        return this.block;
    }

    public void moveCollidedEntities(BlockEntityPistonArm piston, BlockFace moveDirection) {
        var bb = block.getBoundingBox();
        if (bb == null)
            return;
        bb = bb.getOffsetBoundingBox(
                this.x + (piston.progress * moveDirection.getXOffset()) - moveDirection.getXOffset(),
                this.y + (piston.progress * moveDirection.getYOffset()) - moveDirection.getYOffset(),
                this.z + (piston.progress * moveDirection.getZOffset()) - moveDirection.getZOffset()
                //带动站在移动方块上的实体
        ).addCoord(0, moveDirection.getAxis().isHorizontal() ? 0.25 : 0, 0);
        for (Entity entity : this.level.getCollidingEntities(bb))
            piston.moveEntity(entity, moveDirection);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == BlockID.MOVING_BLOCK;
    }
}
