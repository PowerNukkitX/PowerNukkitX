package cn.nukkit.entity.item;

import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityBlockChangeEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX
 */
public class EntityFallingBlock extends Entity {
    @Override
    @NotNull
    public String getIdentifier() {
        return FALLING_BLOCK;
    }

    protected BlockState blockState;
    protected boolean breakOnLava;
    protected boolean breakOnGround;

    public EntityFallingBlock(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public float getLength() {
        return 0.98f;
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.49f;
    }

    @Override
    public boolean canCollide() {
        return blockState.getIdentifier() == BlockID.ANVIL;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (namedTag != null) {
            if (namedTag.contains("Block")) {
                BlockState blockState = NBTIO.getBlockStateHelper(namedTag.getCompound("Block"));
                if (blockState == null) {
                    close();
                    return;
                } else this.blockState = blockState;
            }

            breakOnLava = namedTag.getBoolean("BreakOnLava");
            breakOnGround = namedTag.getBoolean("BreakOnGround");
            this.fireProof = true;
            this.setDataFlag(EntityFlag.FIRE_IMMUNE, true);

            setDataProperty(VARIANT, blockState.blockStateHash());
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return blockState.getIdentifier() == BlockID.ANVIL;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {

        if (closed) {
            return false;
        }

        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0 && !justCreated) {
            return true;
        }

        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {
            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

            motionX *= friction;
            motionY *= 1 - getDrag();
            motionZ *= friction;

            Vector3 pos = (new Vector3(x - 0.5, y, z - 0.5)).round();

            if (breakOnLava && level.getBlock(pos.subtract(0, 1, 0)) instanceof BlockFlowingLava) {
                close();
                if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                    dropItems();
                }
                level.addParticle(new DestroyBlockParticle(pos, Block.get(blockState)));
                return true;
            }

            if (onGround) {
                close();
                Block block = level.getBlock(pos);

                Vector3 floorPos = (new Vector3(x - 0.5, y, z - 0.5)).floor();
                Block floorBlock = this.level.getBlock(floorPos);
                if (this.getBlock().getId().equals(Block.SNOW_LAYER) &&
                        floorBlock.getId().equals(Block.SNOW_LAYER) && floorBlock.getPropertyValue(CommonBlockProperties.HEIGHT) != 7) {
                    int mergedHeight = floorBlock.getPropertyValue(CommonBlockProperties.HEIGHT) + 1 + this.blockState.getPropertyValue(CommonBlockProperties.HEIGHT) + 1;
                    if (mergedHeight > 8) {
                        EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, floorBlock, Block.get(Block.SNOW_LAYER).setPropertyValue(CommonBlockProperties.HEIGHT, 7));
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            this.level.setBlock(floorPos, event.getTo(), true);

                            Vector3 abovePos = floorPos.up();
                            Block aboveBlock = this.level.getBlock(abovePos);
                            if (aboveBlock.isAir()) {
                                EntityBlockChangeEvent event2 = new EntityBlockChangeEvent(this, aboveBlock, Block.get(Block.SNOW_LAYER).setPropertyValue(CommonBlockProperties.HEIGHT, mergedHeight - 8 - 1));
                                this.server.getPluginManager().callEvent(event2);
                                if (!event2.isCancelled()) {
                                    this.level.setBlock(abovePos, event2.getTo(), true);
                                }
                            }
                        }
                    } else {
                        EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, floorBlock,
                                Block.get(Block.SNOW_LAYER).setPropertyValue(CommonBlockProperties.HEIGHT, mergedHeight - 1));
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            this.level.setBlock(floorPos, event.getTo(), true);
                        }
                    }
                } else if (!block.isAir() && block.isTransparent() && !block.canBeReplaced() || this.getBlock().getId() == Block.SNOW_LAYER && block instanceof BlockLiquid) {
                    if (this.getBlock().getId() != Block.SNOW_LAYER ? this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS) : this.level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                        dropItems();
                    }
                } else {
                    EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, block, Block.get(blockState));
                    server.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        if (!breakOnGround)
                            getLevel().setBlock(pos, event.getTo(), true);
                        else {
                            if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                                dropItems();
                            }
                            level.addParticle(new DestroyBlockParticle(pos, Block.get(blockState)));
                        }

                        if (event.getTo().getId() == Block.ANVIL) {
                            getLevel().addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_FALL);

                            Entity[] e = level.getCollidingEntities(this.getBoundingBox(), this);
                            for (Entity entity : e) {
                                if (entity instanceof EntityLiving && fallDistance > 0) {
                                    entity.attack(new EntityDamageByBlockEvent(event.getTo(), entity, DamageCause.FALLING_BLOCK, Math.min(40f, Math.max(0f, fallDistance * 2f))));
                                }
                            }
                        }
                        if (event.getTo().getId() == BlockID.POINTED_DRIPSTONE) {
                            getLevel().addLevelEvent(block, LevelEventPacket.EVENT_SOUND_POINTED_DRIPSTONE_LAND);

                            Entity[] e = level.getCollidingEntities(new SimpleAxisAlignedBB(pos, pos.add(1, 1, 1)));
                            for (Entity entity : e) {
                                if (entity instanceof EntityLiving && fallDistance > 0) {
                                    entity.attack(new EntityDamageByBlockEvent(event.getTo(), entity, DamageCause.FALLING_BLOCK, Math.min(40f, Math.max(0f, fallDistance * 2f))));
                                }
                            }
                        }
                    }
                }
                hasUpdate = true;
            }

            updateMovement();
        }

        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    public Block getBlock() {
        return blockState.toBlock();
    }

    @Override
    public void saveNBT() {
        namedTag.putCompound("Block", blockState.getBlockStateTag());
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return false;
    }

    @Override
    public void resetFallDistance() {
        if (!this.closed) { // For falling anvil: do not reset fall distance before dealing damage to entities
            this.highestPosition = this.y;
        }
    }

    @Override
    public String getOriginalName() {
        return "Falling Block";
    }

    private void dropItems() {
        for (var i : Block.get(blockState).getDrops(Item.AIR)) {
            getLevel().dropItem(this, i);
        }
    }
}
