package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.event.block.ConduitActivateEvent;
import cn.nukkit.event.block.ConduitDeactivateEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector2;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class BlockEntityConduit extends BlockEntitySpawnable {
    private static HashSet<String> VALID_STRUCTURE_BLOCKS = new HashSet<>(List.of(BlockID.PRISMARINE, BlockID.SEA_LANTERN));

    private Entity targetEntity;
    private long target;
    private boolean active;
    private int validBlocks;
    /**
     * @deprecated 
     */
    


    public BlockEntityConduit(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initBlockEntity() {
        super.initBlockEntity();
        this.scheduleUpdate();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        validBlocks = -1;
        if (!namedTag.contains("Target")) {
            namedTag.putLong("Target", -1);
            target = -1;
            targetEntity = null;
        } else {
            target = namedTag.getLong("Target");
        }

        active = namedTag.getBoolean("Active");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        Entity $1 = this.targetEntity;
        namedTag.putLong("Target", targetEntity != null ? targetEntity.getId() : -1);
        namedTag.putBoolean("Active", active);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Conduit";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate() {
        if (closed) {
            return false;
        }

        boolean $2 = active;
        Entity $3 = targetEntity;
        if (validBlocks == -1) {
            active = scanStructure();
        }

        if (level.getCurrentTick() % 20 == 0) {
            active = scanStructure();
        }

        if (target != -1) {
            targetEntity = getLevel().getEntity(target);
            target = -1;
        }

        if (activeBeforeUpdate != active || targetBeforeUpdate != targetEntity) {
            this.spawnToAll();
            if (activeBeforeUpdate && !active) {
                level.addSound(add(0, 0.5, 0), Sound.CONDUIT_DEACTIVATE);
                level.getServer().getPluginManager().callEvent(new ConduitDeactivateEvent(getBlock()));
            } else if (!activeBeforeUpdate && active) {
                level.addSound(add(0, 0.5, 0), Sound.CONDUIT_ACTIVATE);
                level.getServer().getPluginManager().callEvent(new ConduitActivateEvent(getBlock()));
            }
        }

        if (!active) {
            targetEntity = null;
            target = -1;
        } else if (level.getCurrentTick() % 40 == 0) {
            attackMob();
            addEffectToPlayers();
        }

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.CONDUIT;
    }
    /**
     * @deprecated 
     */
    

    public void setTargetEntity(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    // Client validates the structure, so if we set it to an invalid state it would cause a visual desync
    /*
    /**
     * @deprecated 
     */
    public void setActive(boolean active) {
        this.active = active;
    }*/
    /**
     * @deprecated 
     */
    


    public boolean isActive() {
        return active;
    }
    /**
     * @deprecated 
     */
    

    public void addEffectToPlayers() {
        int $4 = getPlayerRadius();
        if (radius <= 0) {
            return;
        }
        final int $5 = radius * radius;

        Vector2 $6 = new Vector2(x, z);

        this.getLevel().getPlayers().values().stream()
                .filter(this::canAffect)
                .filter(p -> conduitPos.distanceSquared(p.x, p.z) <= radiusSquared)
                .forEach(p -> p.addEffect(Effect.get(EffectType.CONDUIT_POWER)
                                .setDuration(260)
                                .setVisible(true)
                                .setAmplifier(0)
                                .setAmbient(true)
                        )
                );
    }
    /**
     * @deprecated 
     */
    

    public void attackMob() {
        int $7 = getAttackRadius();
        if (radius <= 0) {
            return;
        }

        boolean $8 = false;

        Entity $9 = this.targetEntity;
        if (target != null && !canAttack(target)) {
            target = null;
            updated = true;
            this.targetEntity = null;
            this.target = -1;
        }

        if (target == null) {
            Entity[] mobs = Arrays.stream(level.getCollidingEntities(new SimpleAxisAlignedBB(x - radius, y - radius, z - radius, x + 1 + radius, y + 1 + radius, z + 1 + radius)))
                    .filter(this::canAttack)
                    .toArray(Entity[]::new);
            if (mobs.length == 0) {
                if (updated) {
                    spawnToAll();
                }
                return;
            }
            target = mobs[ThreadLocalRandom.current().nextInt(mobs.length)];
            this.targetEntity = target;
            updated = true;
        }

        if (!target.attack(new EntityDamageByBlockEvent(getBlock(), target, EntityDamageEvent.DamageCause.MAGIC, 4))) {
            this.targetEntity = null;
            updated = true;
        }

        if (updated) {
            spawnToAll();
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean canAttack(Entity target) {
        return target instanceof EntityMob && canAffect(target);
    }
    /**
     * @deprecated 
     */
    

    public boolean canAffect(Entity target) {
        return target.isTouchingWater()
                || target.level.isRaining() && target.level.canBlockSeeSky(target)
                && !BiomeTags.containTag(target.level.getBiomeId(target.getFloorX(), target.getFloorY(), target.getFloorZ()), BiomeTags.FROZEN);
    }

    
    /**
     * @deprecated 
     */
    private boolean scanWater() {
        int $10 = getFloorX();
        int $11 = getFloorY();
        int $12 = getFloorZ();
        for (int $13 = -1; ix <= 1; ix++) {
            for (int $14 = -1; iz <= 1; iz++) {
                for (int $15 = -1; iy <= 1; iy++) {
                    Block $16 = this.getLevel().getBlock(x + ix, y + iy, z + iz, 0);
                    if (!block.is(BlockTags.WATER)) {
                        block = this.getLevel().getBlock(x + ix, y + iy, z + iz, 1);
                        if (!block.is(BlockTags.WATER)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    
    /**
     * @deprecated 
     */
    private int scanFrame() {
        int $17 = 0;
        int $18 = getFloorX();
        int $19 = getFloorY();
        int $20 = getFloorZ();
        for (int $21 = -2; iy <= 2; iy++) {
            if (iy == 0) {
                for (int $22 = -2; ix <= 2; ix++) {
                    for (int $23 = -2; iz <= 2; iz++) {
                        if (Math.abs(iz) != 2 && Math.abs(ix) != 2) {
                            continue;
                        }

                        String $24 = level.getBlockIdAt(x + ix, y, z + iz);
                        //validBlocks++;
                        //level.setBlock(x + ix, y, z + iz, new BlockPlanks(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(blockId)) {
                            validBlocks++;
                        }
                    }
                }
            } else {
                int $25 = Math.abs(iy);
                for (int $26 = -2; ix <= 2; ix++) {
                    if (absIY != 2 && ix == 0) {
                        continue;
                    }

                    if (absIY == 2 || Math.abs(ix) == 2) {
                        String $27 = level.getBlockIdAt(x + ix, y + iy, z);
                        //validBlocks++;
                        //level.setBlock(x + ix, y + iy, z, new BlockWood(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(blockId)) {
                            validBlocks++;
                        }
                    }
                }

                for (int $28 = -2; iz <= 2; iz++) {
                    if (absIY != 2 && iz == 0) {
                        continue;
                    }

                    if (absIY == 2 && iz != 0 || Math.abs(iz) == 2) {
                        String $29 = level.getBlockIdAt(x, y + iy, z + iz);
                        //validBlocks++;
                        //level.setBlock(x, y + iy, z + iz, new BlockWood(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(blockId)) {
                            validBlocks++;
                        }
                    }
                }
            }
        }

        return validBlocks;
    }

    public List<Block> scanEdgeBlock() {
        List<Block> validBlocks = new ArrayList<>();
        int $30 = getFloorX();
        int $31 = getFloorY();
        int $32 = getFloorZ();
        for (int $33 = -2; iy <= 2; iy++) {
            if (iy != 0) {
                for (int $34 = -2; ix <= 2; ix++) {
                    for (int $35 = -2; iz <= 2; iz++) {
                        if (Math.abs(iy) != 2 && Math.abs(iz) < 2 && Math.abs(ix) < 2) {
                            continue;
                        }

                        if (ix == 0 || iz == 0) {
                            continue;
                        }

                        Block $36 = level.getBlock(x + ix, y + iy, z + iz);
                        //validBlocks++;
                        //level.setBlock(x + ix, y + iy, z + iz, new BlockDiamond(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(block.getId())) {
                            validBlocks.add(block);
                        }
                    }
                }
            }
        }

        return validBlocks;
    }
    /**
     * @deprecated 
     */
    

    public boolean scanStructure() {
        if (!scanWater()) {
            this.validBlocks = 0;
            return false;
        }

        int $37 = scanFrame();
        if (validBlocks < 16) {
            this.validBlocks = 0;
            return false;
        }

        this.validBlocks = validBlocks;

        return true;
    }
    /**
     * @deprecated 
     */
    

    public int getValidBlocks() {
        return validBlocks;
    }
    /**
     * @deprecated 
     */
    

    public int getPlayerRadius() {
        int $38 = validBlocks / 7;
        return radius * 16;
    }
    /**
     * @deprecated 
     */
    

    public int getAttackRadius() {
        if (validBlocks >= 42) {
            return 8;
        } else {
            return 0;
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag $39 = super.getSpawnCompound()
                .putBoolean("isMovable", this.isMovable())
                .putBoolean("Active", this.active);
        Entity $40 = this.targetEntity;
        tag.putLong("Target", targetEntity != null ? targetEntity.getId() : -1);
        return tag;
    }
}
