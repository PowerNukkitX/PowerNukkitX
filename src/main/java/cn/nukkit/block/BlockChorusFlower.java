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

    public static final BlockProperties PROPERTIES = new BlockProperties(CHORUS_FLOWER, AGE_6);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChorusFlower() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChorusFlower(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Chorus Flower";
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 0.4;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    private boolean isPositionValid() {
        // Chorus flowers must be above end stone or chorus plant, or be above air and horizontally adjacent to exactly one chorus plant.
        // If these conditions are not met, the block breaks without dropping anything.
        Block down = down();
        if (down.getId().equals(CHORUS_PLANT) || down.getId().equals(END_STONE)) {
            return true;
        }
        if (!down.isAir()) {
            return false;
        }
        boolean foundPlant = false;
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block side = getSide(face);
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
                    boolean growUp = false; // Grow upward?
                    boolean ground = false; // Is on the ground directly?
                    if (this.down().isAir() || this.down().getId().equals(END_STONE)) {
                        growUp = true;
                    } else if (this.down().getId().equals(CHORUS_PLANT)) {
                        int height = 1;
                        for (int y = 2; y < 6; y++) {
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
                        BlockChorusFlower block = (BlockChorusFlower) this.clone();
                        block.y = this.y + 1;
                        BlockGrowEvent ev = new BlockGrowEvent(this, block);
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
                        for (int i = 0; i < ThreadLocalRandom.current().nextInt(ground ? 5 : 4); i++) {
                            BlockFace face = BlockFace.Plane.HORIZONTAL.random();
                            Block check = this.getSide(face);
                            if (check.isAir() && check.down().isAir() && isHorizontalAirExcept(check, face.getOpposite())) {
                                BlockChorusFlower block = (BlockChorusFlower) this.clone();
                                block.x = check.x;
                                block.y = check.y;
                                block.z = check.z;
                                block.setAge(getAge() + 1);
                                BlockGrowEvent ev = new BlockGrowEvent(this, block);
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
                        BlockChorusFlower block = (BlockChorusFlower) this.clone();
                        block.setAge(getMaxAge());
                        BlockGrowEvent ev = new BlockGrowEvent(this, block);
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
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public  boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        if (projectile instanceof EntityArrow || projectile instanceof EntitySnowball || projectile instanceof EntitySmallFireball) {
            this.getLevel().useBreakOn(this);
            return true;
        }
        return super.onProjectileHit(projectile, position, motion);
    }

    public int getMaxAge() {
        return AGE_6.getMax();
    }

    public int getAge() {
        return getPropertyValue(AGE_6);
    }

    public void setAge(int age) {
        setPropertyValue(AGE_6, age);
    }

    public boolean isFullyAged() {
        return getAge() >= getMaxAge();
    }

    private boolean isHorizontalAir(Block block) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (!block.getSide(face).isAir()) {
                return false;
            }
        }
        return true;
    }

    private boolean isHorizontalAirExcept(Block block, BlockFace except) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (face != except && !block.getSide(face).isAir()) {
                return false;
            }
        }
        return true;
    }
}
