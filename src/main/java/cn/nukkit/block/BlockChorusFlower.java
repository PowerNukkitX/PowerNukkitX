package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntitySmallFireBall;
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

public class BlockChorusFlower extends BlockTransparentMeta {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final IntBlockProperty AGE = new IntBlockProperty("age", false, 5);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(AGE);

    public BlockChorusFlower() {
        this(0);
    }
    
    public BlockChorusFlower(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return CHORUS_FLOWER;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
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
        if (down.getId() == CHORUS_PLANT || down.getId() == END_STONE) {
            return true;
        }
        if (down.getId() != AIR) {
            return false;
        }
        boolean foundPlant = false;
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block side = getSide(face);
            if (side.getId() == CHORUS_PLANT) {
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
            if (this.up().getId() == AIR && this.up().getY() < level.getMaxHeight()) {
                if (!isFullyAged()) {
                    boolean growUp = false; // Grow upward?
                    boolean ground = false; // Is on the ground directly?
                    if (this.down().getId() == AIR || this.down().getId() == END_STONE) {
                        growUp = true;
                    } else if (this.down().getId() == CHORUS_PLANT) {
                        int height = 1;
                        for (int y = 2; y < 6; y++) {
                            if (this.down(y).getId() == CHORUS_PLANT) {
                                height++;
                            } else {
                                if (this.down(y).getId() == END_STONE) {
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
                    if (growUp && this.up(2).getId() == AIR && isHorizontalAir(this.up())) {
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
                            if (check.getId() == AIR && check.down().getId() == AIR && isHorizontalAirExcept(check, face.getOpposite())) {
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
    @PowerNukkitOnly
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    @PowerNukkitOnly
    public  boolean sticksToPiston() {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        if (projectile instanceof EntityArrow || projectile instanceof EntitySnowball || projectile instanceof EntitySmallFireBall) {
            this.getLevel().useBreakOn(this);
            return true;
        }
        return super.onProjectileHit(projectile, position, motion);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getMaxAge() {
        return AGE.getMaxValue();
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getAge() {
        return getIntValue(AGE);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setAge(int age) {
        setIntValue(AGE, age);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isFullyAged() {
        return getAge() >= getMaxAge();
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private boolean isHorizontalAir(Block block) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (block.getSide(face).getId() != AIR) {
                return false;
            }
        }
        return true;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private boolean isHorizontalAirExcept(Block block, BlockFace except) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (face != except) {
                if (block.getSide(face).getId() != AIR) {
                    return false;
                }
            }
        }
        return true;
    }
}
