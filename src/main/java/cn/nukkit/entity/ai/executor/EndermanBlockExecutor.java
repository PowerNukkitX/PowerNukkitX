package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCactus;
import cn.nukkit.block.BlockClay;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.Natural;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.mob.EntityEnderman;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class EndermanBlockExecutor implements IBehaviorExecutor {

    public boolean execute(EntityIntelligent entity) {
        if(entity instanceof EntityEnderman enderman) {
            if(enderman.getItemInHand().isNull()) {
                Optional<Block> optionalBlock = Arrays.stream(entity.level.getCollisionBlocks(entity.getBoundingBox().grow(3.7f, 0, 3.7f))).filter(block -> block instanceof Natural natural && natural.canBePickedUp()).findAny();
                if(optionalBlock.isPresent()) {
                    Block block = optionalBlock.get();
                    enderman.setItemInHand(block.toItem());
                    enderman.setDataProperty(EntityDataTypes.CARRY_BLOCK_STATE, block);
                    enderman.getLevel().setBlock(block, Block.get(Block.AIR));
                }
            } else {
                if(enderman.getItemInHand().isBlock()) {
                    Optional<Block> optionalBlock = Arrays.stream(entity.level.getCollisionBlocks(entity.getBoundingBox().addCoord(0.7f, -1, 0.7f))).filter(block -> block.isSolid() && block.up().canBeReplaced()).findAny();
                    if(optionalBlock.isPresent()) {
                        Block block = optionalBlock.get();
                        block.getLevel().setBlock(block.up(), enderman.getItemInHand().getBlock());
                        enderman.setItemInHand(Item.AIR);
                        enderman.setDataProperty(EntityDataTypes.CARRY_BLOCK_STATE, Item.AIR.getBlock());
                    }
                }   
            }
        }
        return true;
    }

}
