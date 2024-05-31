package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockFlowingLava;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockPowderSnow;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.ExplodeParticle;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.utils.Identifier;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBucket extends Item {
    public ItemBucket() {
        this(0, 1);
    }

    public ItemBucket(Integer meta) {
        this(meta, 1);
    }

    public ItemBucket(Integer meta, int count) {
        super(BUCKET, meta, count);
    }

    public ItemBucket(String id) {
        this(id, 0);
    }

    public ItemBucket(String id, int count) {
        super(id, 0, count);
    }

    public ItemBucket(String id, int count, String name) {
        super(id, 0, count, name);
    }

    @Override
    public void internalAdjust() {
        if (this.id.equals(BUCKET)) {
            switch (getDamage()) {
                case 1 -> {
                    this.id = MILK_BUCKET;
                    this.identifier = new Identifier(MILK_BUCKET);
                }
                case 2 -> {
                    this.id = COD_BUCKET;
                    this.identifier = new Identifier(COD_BUCKET);
                }
                case 3 -> {
                    this.id = SALMON_BUCKET;
                    this.identifier = new Identifier(SALMON_BUCKET);
                }
                case 4 -> {
                    this.id = TROPICAL_FISH_BUCKET;
                    this.identifier = new Identifier(TROPICAL_FISH_BUCKET);
                }
                case 5 -> {
                    this.id = PUFFERFISH_BUCKET;
                    this.identifier = new Identifier(PUFFERFISH_BUCKET);
                }
                case 8 -> {
                    this.id = WATER_BUCKET;
                    this.identifier = new Identifier(WATER_BUCKET);
                }
                case 10 -> {
                    this.id = LAVA_BUCKET;
                    this.identifier = new Identifier(LAVA_BUCKET);
                }
                case 11 -> {
                    this.id = POWDER_SNOW_BUCKET;
                    this.identifier = new Identifier(POWDER_SNOW_BUCKET);
                }
                case 12 -> {
                    this.id = AXOLOTL_BUCKET;
                    this.identifier = new Identifier(AXOLOTL_BUCKET);
                }
                case 13 -> {
                    this.id = TADPOLE_BUCKET;
                    this.identifier = new Identifier(TADPOLE_BUCKET);
                }
                default -> {
                    this.id = BUCKET;
                    this.identifier = new Identifier(BUCKET);
                }
            }
            this.meta = 0;
            this.name = null;
        }
    }

    public static String getDamageByTarget(String target) {
        return switch (target) {
            case COD_BUCKET, SALMON_BUCKET, TROPICAL_FISH_BUCKET, PUFFERFISH_BUCKET, WATER_BUCKET, AXOLOTL_BUCKET,
                 TADPOLE_BUCKET -> BlockID.FLOWING_WATER;
            case LAVA_BUCKET -> BlockID.FLOWING_LAVA;
            case POWDER_SNOW_BUCKET -> BlockID.POWDER_SNOW;
            default -> BlockID.AIR;
        };
    }

    public boolean isEmpty() {
        return Objects.equals(getId(), BUCKET) && meta == 0;
    }

    public boolean isWater() {
        return switch (getId()) {
            case COD_BUCKET, SALMON_BUCKET, TROPICAL_FISH_BUCKET, PUFFERFISH_BUCKET, WATER_BUCKET, AXOLOTL_BUCKET,
                 TADPOLE_BUCKET -> true;
            default -> false;
        };
    }

    public boolean isMilk() {
        return getId().equals(MILK_BUCKET);
    }

    public boolean isLava() {
        return getId().equals(LAVA_BUCKET);
    }

    public boolean isPowderSnow() {
        return getId().equals(POWDER_SNOW_BUCKET);
    }

    public @Nullable String getFishEntityId() {
        return switch (this.getId()) {
            case COD_BUCKET -> EntityID.COD;
            case SALMON_BUCKET -> EntityID.SALMON;
            case TROPICAL_FISH_BUCKET -> EntityID.TROPICALFISH;
            case PUFFERFISH_BUCKET -> EntityID.PUFFERFISH;
            case AXOLOTL_BUCKET -> EntityID.AXOLOTL;
            case TADPOLE_BUCKET -> EntityID.TADPOLE;
            default -> null;
        };
    }

    @Override
    public int getMaxStackSize() {
        return meta == 0 && Objects.equals(getId(), BUCKET) ? 16 : 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    /**
     * get the placed block for this bucket
     */
    public Block getTargetBlock() {
        return Block.get(getDamageByTarget(getId()));
    }


    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }
        if (player.isItemCoolDownEnd(BUCKET)) {
            player.setItemCoolDown(5, BUCKET);
        } else {
            return false;
        }

        Block targetBlock = getTargetBlock();

        if (targetBlock instanceof BlockAir) {
            if (!(target instanceof BlockPowderSnow)) {
                //                                       is liquid and state is
                if (!(target instanceof BlockLiquid) || !target.isDefaultState()) {
                    target = target.getLevelBlockAtLayer(1);
                }
                if (!(target instanceof BlockLiquid) || !target.isDefaultState()) {
                    target = block;
                }
                if (!(target instanceof BlockLiquid) || !target.isDefaultState()) {
                    target = block.getLevelBlockAtLayer(1);
                }
            }
            if ((target instanceof BlockLiquid || target instanceof BlockPowderSnow) && target.isDefaultState()) {
                Item result;
                if (player.isCreative()) {
                    result = Item.get(BUCKET, 0, 1);
                } else if (target instanceof BlockPowderSnow) {
                    result = Item.get(BUCKET, 11, 1);
                } else if (target instanceof BlockFlowingLava) {
                    result = Item.get(BUCKET, 10, 1);
                } else {
                    result = Item.get(BUCKET, 8, 1);
                }
                PlayerBucketFillEvent ev;
                player.getServer().getPluginManager().callEvent(ev = new PlayerBucketFillEvent(player, block, face, target, this, result));
                if (!ev.isCancelled()) {
                    player.getLevel().setBlock(target, target.layer, Block.get(BlockID.AIR), true, true);

                    level.getVibrationManager().callVibrationEvent(new VibrationEvent(player, target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PICKUP));

                    // When water is removed ensure any adjacent still water is replaced with water that can flow.
                    for (BlockFace side : Plane.HORIZONTAL) {
                        Block b = target.getSideAtLayer(0, side);
                        if (b.getId().equals(BlockID.WATER)) {
                            level.setBlock(b, Block.get(BlockID.FLOWING_WATER));
                        } else if (b.getId().equals(BlockID.LAVA)) level.setBlock(b, Block.get(BlockID.FLOWING_LAVA));
                    }

                    if (player.isSurvival()) {
                        if (this.getCount() - 1 <= 0) {
                            player.getInventory().setItemInHand(ev.getItem());
                        } else {
                            Item clone = this.clone();
                            clone.setCount(this.getCount() - 1);
                            player.getInventory().setItemInHand(clone);
                            if (player.getInventory().canAddItem(ev.getItem())) {
                                player.getInventory().addItem(ev.getItem());
                            } else {
                                player.dropItem(ev.getItem());
                            }
                            player.getInventory().sendContents(player);
                        }
                    }

                    if (target instanceof BlockFlowingLava) {
                        level.addSound(block, Sound.BUCKET_FILL_LAVA);
                    } else if (target instanceof BlockFlowingWater) {
                        level.addSound(block, Sound.BUCKET_FILL_WATER);
                    } else if (target instanceof BlockPowderSnow) {
                        level.addSound(block, Sound.BUCKET_FILL_POWDER_SNOW);
                    }

                    return true;
                } else {
                    player.getInventory().sendContents(player);
                }
            }
        } else if (targetBlock instanceof BlockLiquid) {
            Item result = Item.get(BUCKET, 0, 1);
            boolean usesWaterlogging = ((BlockLiquid) targetBlock).usesWaterLogging();
            Block placementBlock;
            if (usesWaterlogging) {
                if (block.getId().equals(BlockID.BAMBOO)) {
                    placementBlock = block;
                } else if (target.getWaterloggingLevel() > 0) {
                    placementBlock = target.getLevelBlockAtLayer(1);
                } else if (block.getWaterloggingLevel() > 0) {
                    placementBlock = block.getLevelBlockAtLayer(1);
                } else {
                    placementBlock = block;
                }
            } else {
                placementBlock = block;
            }

            PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, placementBlock, face, target, this, result);
            boolean canBeFlowedInto = placementBlock.canBeFlowedInto() || placementBlock.getId().equals(BlockID.BAMBOO);
            if (usesWaterlogging) {
                ev.setCancelled(placementBlock.getWaterloggingLevel() <= 0 && !canBeFlowedInto);
            } else {
                ev.setCancelled(!canBeFlowedInto);
            }

            boolean nether = false;
            if (!canBeUsedOnDimension(player.getLevel().getDimension())) {
                ev.setCancelled(true);
                nether = !isLava();
            }

            player.getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                player.getLevel().setBlock(placementBlock, placementBlock.layer, targetBlock, true, true);
                player.getLevel().sendBlocks(new Player[]{player}, new Block[]{target.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
                target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(player, target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PLACE));
                updateBucketItem(player, ev);

                afterUse(level, block);

                return true;
            } else if (nether) {//handle the logic that the player can't use water bucket in nether
                if (!player.isCreative()) {
                    this.setDamage(0); // Empty bucket
                    player.getInventory().setItemInHand(this);
                }
                player.getLevel().addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_FIZZ);
                player.getLevel().addParticle(new ExplodeParticle(target.add(0.5, 1, 0.5)));
            } else {
                player.getLevel().sendBlocks(new Player[]{player}, new Block[]{block.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
                player.getInventory().sendContents(player);
            }
        } else if (targetBlock instanceof BlockPowderSnow) {
            Item result = Item.get(BUCKET, 0, 1);
            if (!target.canBeReplaced()) {
                final Block side = target.getSide(face);
                if (side.canBeReplaced()) {
                    target = side;
                }
            }
            PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, targetBlock, face, target, this, result);
            if (!ev.isCancelled()) {
                target.getLevel().setBlock(target, targetBlock, true, true);
                player.getLevel().addSound(target, Sound.BUCKET_FILL_POWDER_SNOW);

                updateBucketItem(player, ev);

                target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(player, target.add(0.5, 0.5, 0.5), VibrationType.BLOCK_PLACE));
            }
        }

        return true;
    }

    /**
     * update the count of bucket and set to inventory
     */
    private void updateBucketItem(Player player, PlayerBucketEmptyEvent ev) {
        if (player.isSurvival()) {
            if (this.getCount() - 1 <= 0) {
                player.getInventory().setItemInHand(ev.getItem());
            } else {
                Item clone = this.clone();
                clone.setCount(this.getCount() - 1);
                player.getInventory().setItemInHand(clone);
                if (player.getInventory().canAddItem(ev.getItem())) {
                    player.getInventory().addItem(ev.getItem());
                } else {
                    player.dropItem(ev.getItem());
                }
            }
        }
    }

    /**
     * whether the bucket can be used in the dimension
     */
    protected boolean canBeUsedOnDimension(int dimension) {
        return dimension != Level.DIMENSION_NETHER || (isEmpty() || isLava() || isMilk());
    }

    /**
     * handle logic after use bucket.
     */
    protected void afterUse(Level level, Block block) {
        if (isLava()) {
            level.addSound(block, Sound.BUCKET_EMPTY_LAVA);
        } else {
            level.addSound(block, Sound.BUCKET_EMPTY_WATER);
        }

        spawnFishEntity(block.add(0.5, 0.5, 0.5));
    }

    public void spawnFishEntity(Position spawnPos) {
        var fishEntityId = getFishEntityId();
        if (fishEntityId != null) {
            var fishEntity = Entity.createEntity(fishEntityId, spawnPos);
            if (fishEntity != null)
                fishEntity.spawnToAll();
        }
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return Objects.equals(getId(), BUCKET) && isMilk(); // Milk
    }
}
