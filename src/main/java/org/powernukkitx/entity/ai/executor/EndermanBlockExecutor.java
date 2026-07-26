package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.Natural;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.mob.EntityEnderman;
import org.powernukkitx.item.Item;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;

import java.util.Arrays;
import java.util.Optional;

public class EndermanBlockExecutor implements IBehaviorExecutor {

    public boolean execute(EntityIntelligent entity) {
        if(entity instanceof EntityEnderman enderman) {
            if(enderman.getItemInHand().isNull()) {
                Optional<Block> optionalBlock = Arrays.stream(entity.level.getCollisionBlocks(entity.getBoundingBox().grow(3.7f, 0, 3.7f))).filter(block -> block instanceof Natural natural && natural.canBePickedUp()).findAny();
                if(optionalBlock.isPresent()) {
                    Block block = optionalBlock.get();
                    enderman.setItemInHand(block.toItem());
                    enderman.setDataProperty(ActorDataTypes.CARRY_BLOCK_RUNTIME_ID, block);
                    enderman.getLevel().setBlock(block, Block.get(Block.AIR));
                }
            } else {
                if(enderman.getItemInHand().isBlock()) {
                    Optional<Block> optionalBlock = Arrays.stream(entity.level.getCollisionBlocks(entity.getBoundingBox().addCoord(0.7f, -1, 0.7f))).filter(block -> block.isSolid() && block.up().canBeReplaced()).findAny();
                    if(optionalBlock.isPresent()) {
                        Block block = optionalBlock.get();
                        block.getLevel().setBlock(block.up(), enderman.getItemInHand().getBlock());
                        enderman.setItemInHand(Item.AIR);
                        enderman.setDataProperty(ActorDataTypes.CARRY_BLOCK_RUNTIME_ID, Item.AIR.getBlock());
                    }
                }   
            }
        }
        return true;
    }

}
