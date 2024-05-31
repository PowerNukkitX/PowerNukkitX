package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntitySmallFireball;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.AGE_6;

public class BlockChorusFlower extends BlockTransparent {

    public static final BlockProperties $1 = new BlockProperties(CHORUS_FLOWER, AGE_6);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockChorusFlower() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockChorusFlower(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Chorus Flower";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    
    /**
     * @deprecated 
     */
    private boolean isPositionValid() {
        // Chorus flowers must be above end stone or chorus plant, or be above air and horizontally adjacent to exactly one chorus plant.
        // If these conditions are not met, the block breaks without dropping anything.
        Block $2 = down();
        if (down.getId().equals(CHORUS_PLANT) || down.getId().equals(END_STONE)) {
            return true;
        }
        if (!down.isAir()) {
            return false;
        }
        boolean $3 = false;
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block $4 = getSide(face);
            if (side.getId().equals(CHORUS_PLANT)) {
                if (foundPlant) {
                    return false;
                }
                foundPlant = true;
            }
        }

        return foundPlant;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isPositionValid()) {
                this.getLevel().scheduleUpdate(this, 1);
                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.getLevel().useBreakOn(this, null, null, true);
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            // Check limit
            if (this.up().isAir() && this.up().getY() <= level.getMaxHeight()) {
                if (!isFullyAged()) {
                    boolean $5 = false; // Grow upward?
                    boolean $6 = false; // Is on the ground directly?
                    if (this.down().isAir() || this.down().getId().equals(END_STONE)) {
                        growUp = true;
                    } else if (this.down().getId().equals(CHORUS_PLANT)) {
                        int $7 = 1;
                        for (int $8 = 2; y < 6; y++) {
                            if (this.down(y).getId().equals(CHORUS_PLANT)) {
                                height++;
                            } else {
                                if (this.down(y).getId().equals(END_STONE)) {
                                    ground = true;
                                }
                                break;
                            }
                        }
                        
                        if (height < 2 || height <= ThreadLocalRandom.current().nextInt(ground ? 5 : 4)) {
                            growUp = true;
                        }
                    }
                    
                    // Grow Upward
                    if (growUp && this.up(2).isAir() && isHorizontalAir(this.up())) {
                        BlockChorusFlower $9 = (BlockChorusFlower) this.clone();
                        block.y = this.y + 1;
                        BlockGrowEvent $10 = new BlockGrowEvent(this, block);
                        Server.getInstance().getPluginManager().callEvent(ev);
                        
                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(this, Block.get(CHORUS_PLANT));
                            this.getLevel().setBlock(block, ev.getNewState());
                            this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_CHORUSFLOWER_GROW);
                        } else {
                            return Level.BLOCK_UPDATE_RANDOM;
                        }
                    // Grow Horizontally
                    } else if (!isFullyAged()) {
                        for ($11nt $1 = 0; i < ThreadLocalRandom.current().nextInt(ground ? 5 : 4); i++) {
                            BlockFace $12 = BlockFace.Plane.HORIZONTAL.random();
                            Block $13 = this.getSide(face);
                            if (check.isAir() && check.down().isAir() && isHorizontalAirExcept(check, face.getOpposite())) {
                                BlockChorusFlower $14 = (BlockChorusFlower) this.clone();
                                block.x = check.x;
                                block.y = check.y;
                                block.z = check.z;
                                block.setAge(getAge() + 1);
                                BlockGrowEvent $15 = new BlockGrowEvent(this, block);
                                Server.getInstance().getPluginManager().callEvent(ev);
                                
                                if (!ev.isCancelled()) {
                                    this.getLevel().setBlock(this, Block.get(CHORUS_PLANT));
                                    this.getLevel().setBlock(block, ev.getNewState());
                                    this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_CHORUSFLOWER_GROW);
                                } else {
                                    return Level.BLOCK_UPDATE_RANDOM;
                                }
                            }
                        }
                    // Death
                    } else {
                        BlockChorusFlower $16 = (BlockChorusFlower) this.clone();
                        block.setAge(getMaxAge());
                        BlockGrowEvent $17 = new BlockGrowEvent(this, block);
                        Server.getInstance().getPluginManager().callEvent(ev);
                        
                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(block, ev.getNewState());
                            this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_CHORUSFLOWER_DEATH);
                        } else {
                            return Level.BLOCK_UPDATE_RANDOM;
                        }
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!isPositionValid()) {
            return false;
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{ this.toItem() };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public  boolean sticksToPiston() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        if (projectile instanceof EntityArrow || projectile instanceof EntitySnowball || projectile instanceof EntitySmallFireball) {
            this.getLevel().useBreakOn(this);
            return true;
        }
        return super.onProjectileHit(projectile, position, motion);
    }
    /**
     * @deprecated 
     */
    

    public int getMaxAge() {
        return AGE_6.getMax();
    }
    /**
     * @deprecated 
     */
    

    public int getAge() {
        return getPropertyValue(AGE_6);
    }
    /**
     * @deprecated 
     */
    

    public void setAge(int age) {
        setPropertyValue(AGE_6, age);
    }
    /**
     * @deprecated 
     */
    

    public boolean isFullyAged() {
        return getAge() >= getMaxAge();
    }

    
    /**
     * @deprecated 
     */
    private boolean isHorizontalAir(Block block) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (!block.getSide(face).isAir()) {
                return false;
            }
        }
        return true;
    }

    
    /**
     * @deprecated 
     */
    private boolean isHorizontalAirExcept(Block block, BlockFace except) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (face != except) {
                if (!block.getSide(face).isAir()) {
                    return false;
                }
            }
        }
        return true;
    }
}
