package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockFlowingLava;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLava;
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
        super(BUCKET, meta, count, getName(meta));
    }

    public ItemBucket(String id) {
        super(id);
    }

    public ItemBucket(String id, int count) {
        super(id, 0, count);
    }

    public ItemBucket(String id, int count, String name) {
        super(id, 0, count, name);
    }

    protected static String getName(int meta) {
        return switch (meta) {
            case 1 -> "Milk";
            case 2 -> "Bucket of Cod";
            case 3 -> "Bucket of Salmon";
            case 4 -> "Bucket of Tropical Fish";
            case 5 -> "Bucket of Pufferfish";
            case 8 -> "Water Bucket";
            case 10 -> "Lava Bucket";
            case 11 -> "Powder Snow Bucket";
            case 12 -> "Bucket of Axolotl";
            case 13 -> "Bucket of Tadpole";
            default -> "Bucket";
        };
    }

    public int getBucketType() {
        return this.meta;
    }

    public static String getDamageByTarget(int target) {
        return switch (target) {
            case 2, 3, 4, 5, 8, 9, 12, 13 -> BlockID.FLOWING_WATER;
            case 10 -> BlockID.FLOWING_LAVA;
            case 11 -> BlockID.POWDER_SNOW;
            default -> BlockID.AIR;
        };
    }

    public boolean isEmpty() {
        return Objects.equals(getId(), BUCKET) && getBucketType() == 0;
    }

    public boolean isWater() {
        return getTargetBlock().getId().equals(BlockID.FLOWING_WATER);
    }

    public boolean isLava() {
        return getTargetBlock().getId().equals(BlockID.FLOWING_LAVA);
    }

    public boolean isPowderSnow() {
        return getTargetBlock().getId().equals(BlockID.POWDER_SNOW);
    }

    public @Nullable String getFishEntityId() {
        return switch (this.getBucketType()) {
            case 2 -> EntityID.COD;
            case 3 -> EntityID.SALMON;
            case 4 -> EntityID.TROPICALFISH;
            case 5 -> EntityID.PUFFERFISH;
            case 12 -> EntityID.AXOLOTL;
            case 13 -> EntityID.TADPOLE;
            default -> null;
        };
    }

    @Override
    public int getMaxStackSize() {
        return getBucketType() == 0 && Objects.equals(getId(), BUCKET) ? 16 : 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    /**
     * get the placed block for this bucket
     */
    public Block getTargetBlock() {
        return Block.get(getDamageByTarget(getBucketType()));
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
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
                if (target instanceof BlockPowderSnow) {
                    result = Item.get(BUCKET, 11, 1);
                } else if (target instanceof BlockLava) {
                    result = Item.get(BUCKET, 10, 1);
                } else {
                    result = Item.get(BUCKET, 8, 1);
                }
                PlayerBucketFillEvent ev;
                player.getServer().getPluginManager().callEvent(ev = new PlayerBucketFillEvent(player, block, face, target, this, result));
                if (!ev.isCancelled()) {
                    player.getLevel().setBlock(target, target.layer, Block.get(BlockID.AIR), true, true);

                    level.getVibrationManager().callVibrationEvent(new VibrationEvent(player, target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PICKUP));

                    // When water is removed ensure any adjacent still water is
                    // replaced with water that can flow.
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
                nether = getBucketType() != 10;
            }

            player.getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                player.getLevel().setBlock(placementBlock, placementBlock.layer, targetBlock, true, true);
                target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(player, target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PLACE));
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

                afterUse(level, block);

                return true;
            } else if (nether) {
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

                target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(player, target.add(0.5, 0.5, 0.5), VibrationType.BLOCK_PLACE));
            }
        }

        return false;
    }

    protected boolean canBeUsedOnDimension(int dimension) {
        if (!Objects.equals(getId(), BUCKET)) {
            return true;
        }

        return dimension != Level.DIMENSION_NETHER || (getBucketType() == 10 || getBucketType() == 1);
    }

    protected void afterUse(Level level, Block block) {
        if (this.getBucketType() == 10) {
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
        return Objects.equals(getId(), BUCKET) && this.getBucketType() == 1; // Milk
    }
}
