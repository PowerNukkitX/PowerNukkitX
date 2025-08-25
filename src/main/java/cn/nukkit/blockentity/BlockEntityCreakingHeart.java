package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCreakingHeart;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityCreaking;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import lombok.Getter;

public class BlockEntityCreakingHeart extends BlockEntitySpawnable {

    @Getter
    private EntityCreaking linkedCreaking;

    public double spawnRangeHorizontal = 16.5d;
    public double spawnRangeVertical = 8.5d;

    public BlockEntityCreakingHeart(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        if(getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
            scheduleUpdate();
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId().equals(Block.CREAKING_HEART);
    }

    @Override
    public String getName() {
        return "Creaking Heart";
    }

    public BlockCreakingHeart getHeart() {
        return (BlockCreakingHeart) getBlock();
    }

    public void setLinkedCreaking(EntityCreaking creaking) {
        if(getLinkedCreaking() != null) {
            getLinkedCreaking().setCreakingHeart(null);
        }
        if(creaking != null) {
            creaking.setCreakingHeart(this);
        }
        linkedCreaking = creaking;
    }

    @Override
    public boolean onUpdate() {
        if(getLevel().getTick() % 40 == 0 && isBlockEntityValid() && getHeart().isActive()) {
            getLevel().addSound(this, Sound.BLOCK_CREAKING_HEART_AMBIENT);
        }
        if((getLinkedCreaking() == null || !getLinkedCreaking().isAlive()) && isBlockEntityValid() && getHeart().isActive() && (!getLevel().isDay() || getLevel().isRaining() || getLevel().isThundering())) {
            Position pos = new Position(
                            this.x + Utils.rand(-this.spawnRangeHorizontal, this.spawnRangeHorizontal),
                            this.y,
                            this.z + Utils.rand(-this.spawnRangeHorizontal, this.spawnRangeHorizontal),
                            this.level
                    );

            height:
            for(double i = -spawnRangeVertical; i < spawnRangeVertical; i++) {
                Position newPos = pos.add(0, i, 0);
                if(!newPos.getLevelBlock().isAir()) {
                    for(int j = 1; j < 3; j++) {
                        if(!getSide(BlockFace.UP, j).getLevelBlock().isAir()) continue height;
                    }
                    pos.y = i;
                    break;
                }
            }

            Entity ent = Entity.createEntity(Entity.CREAKING, pos);
            if(ent != null) {
                CreatureSpawnEvent ev = new CreatureSpawnEvent(ent.getNetworkId(), pos, new CompoundTag(), CreatureSpawnEvent.SpawnReason.CREAKING_HEART);
                level.getServer().getPluginManager().callEvent(ev);
                if(ev.isCancelled()) {
                    ent.close();
                } else {
                    setLinkedCreaking((EntityCreaking) ent);
                    this.getLevel().addSound(this, Sound.BLOCK_CREAKING_HEART_MOB_SPAWN, 1, 1);
                    ent.spawnToAll();
                }
            }
        }
        return true;
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        if(getLinkedCreaking() != null) {
            getLinkedCreaking().kill();
        }
        super.onBreak(isSilkTouch);
    }
}
