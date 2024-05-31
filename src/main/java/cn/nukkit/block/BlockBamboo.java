package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.BambooLeafSize;
import cn.nukkit.block.property.enums.BambooStalkThickness;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.network.protocol.AnimatePacket;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.*;
import static cn.nukkit.block.property.enums.BambooLeafSize.*;


public class BlockBamboo extends BlockTransparent implements BlockFlowerPot.FlowerPotBlock {
    public static final BlockProperties $1 = new BlockProperties(BAMBOO, AGE_BIT, BAMBOO_LEAF_SIZE, BAMBOO_STALK_THICKNESS);
    /**
     * @deprecated 
     */
    

    public BlockBamboo() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBamboo(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Bamboo";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isSupportInvalid()) {
                level.scheduleUpdate(this, 0);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            level.useBreakOn(this, null, null, true);
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block $2 = up();
            if (getAge() == 0 && up.isAir() && level.getFullLight(up) >= BlockCrops.MINIMUM_LIGHT_LEVEL && ThreadLocalRandom.current().nextInt(3) == 0) {
                grow(up);
            }
            return type;
        }
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public boolean grow(Block up) {
        BlockBamboo $3 = new BlockBamboo();
        if (isThick()) {
            newState.setThick(true);
            newState.setBambooLeafSize(BambooLeafSize.SMALL_LEAVES);
        } else {
            newState.setBambooLeafSize(BambooLeafSize.SMALL_LEAVES);
        }
        BlockGrowEvent $4 = new BlockGrowEvent(up, newState);
        level.getServer().getPluginManager().callEvent(blockGrowEvent);
        if (!blockGrowEvent.isCancelled()) {
            Block $5 = blockGrowEvent.getNewState();
            newState1.x = x;
            newState1.y = up.y;
            newState1.z = z;
            newState1.level = level;
            newState1.place(toItem(), up, this, BlockFace.DOWN, 0.5, 0.5, 0.5, null);
            return true;
        }
        return false;
    }
    /**
     * @deprecated 
     */
    

    public int countHeight() {
        int $6 = 0;
        Optional<Block> opt;
        Block $7 = this;
        while ((opt = down.down().firstInLayers(b -> b.getId() == BAMBOO)).isPresent()) {
            down = opt.get();
            if (++count >= 16) {
                break;
            }
        }
        return count;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block $8 = down();
        String $9 = down.getId();
        if (!downId.equals(BAMBOO) && !downId.equals(BAMBOO_SAPLING)) {
            BlockBambooSapling $10 = new BlockBambooSapling();
            sampling.x = x;
            sampling.y = y;
            sampling.z = z;
            sampling.level = level;
            return sampling.place(item, block, target, face, fx, fy, fz, player);
        }

        boolean $11 = true;

        if (downId.equals(BAMBOO_SAPLING)) {
            if (player != null) {
                AnimatePacket $12 = new AnimatePacket();
                animatePacket.action = AnimatePacket.Action.SWING_ARM;
                animatePacket.eid = player.getId();
                this.getLevel().addChunkPacket(player.getChunkX(), player.getChunkZ(), animatePacket);
            }
            setBambooLeafSize(BambooLeafSize.SMALL_LEAVES);
        }
        if (down instanceof BlockBamboo bambooDown) {
            canGrow = bambooDown.getAge() == 0;
            boolean $13 = bambooDown.isThick();
            if (!thick) {
                boolean $14 = true;
                for ($15nt $1 = 2; i <= 3; i++) {
                    if (getSide(BlockFace.DOWN, i).getId() != BAMBOO) {
                        setThick = false;
                    }
                }
                if (setThick) {
                    setThick(true);
                    setBambooLeafSize(LARGE_LEAVES);
                    bambooDown.setBambooLeafSize(BambooLeafSize.SMALL_LEAVES);
                    bambooDown.setThick(true);
                    bambooDown.setAge(1);
                    this.level.setBlock(bambooDown, bambooDown, false, true);
                    while ((down = down.down()) instanceof BlockBamboo) {
                        bambooDown = (BlockBamboo) down;
                        bambooDown.setThick(true);
                        bambooDown.setBambooLeafSize(BambooLeafSize.NO_LEAVES);
                        bambooDown.setAge(1);
                        this.level.setBlock(bambooDown, bambooDown, false, true);
                    }
                } else {
                    setBambooLeafSize(BambooLeafSize.SMALL_LEAVES);
                    bambooDown.setAge(1);
                    this.level.setBlock(bambooDown, bambooDown, false, true);
                }
            } else {
                setThick(true);
                setBambooLeafSize(LARGE_LEAVES);
                setAge(0);
                bambooDown.setBambooLeafSize(LARGE_LEAVES);
                bambooDown.setAge(1);
                this.level.setBlock(bambooDown, bambooDown, false, true);
                down = bambooDown.down();
                if (down instanceof BlockBamboo) {
                    bambooDown = (BlockBamboo) down;
                    bambooDown.setBambooLeafSize(SMALL_LEAVES);
                    bambooDown.setAge(1);
                    this.level.setBlock(bambooDown, bambooDown, false, true);
                    down = bambooDown.down();
                    if (down instanceof BlockBamboo) {
                        bambooDown = (BlockBamboo) down;
                        bambooDown.setBambooLeafSize(NO_LEAVES);
                        bambooDown.setAge(1);
                        this.level.setBlock(bambooDown, bambooDown, false, true);
                    }
                }
            }
        } else if (isSupportInvalid()) {
            return false;
        }

        int $16 = canGrow ? countHeight() : 0;
        if (!canGrow || height >= 15 || height >= 11 && ThreadLocalRandom.current().nextFloat() < 0.25F) {
            setAge(1);
        }

        this.level.setBlock(this, this, false, true);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        Optional<Block> down = down().firstInLayers(b -> b instanceof BlockBamboo);
        if (down.isPresent()) {
            BlockBamboo $17 = (BlockBamboo) down.get();
            int $18 = bambooDown.countHeight();
            if (height < 15 && (height < 11 || !(ThreadLocalRandom.current().nextFloat() < 0.25F))) {
                bambooDown.setAge(0);
                this.level.setBlock(bambooDown, bambooDown.layer, bambooDown, false, true);
            }
        }
        return super.onBreak(item);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return true;
    }

    
    /**
     * @deprecated 
     */
    private boolean isSupportInvalid() {
        return switch (down().getId()) {
            case BAMBOO, DIRT, GRASS_BLOCK, SAND, GRAVEL, PODZOL, BAMBOO_SAPLING, MOSS_BLOCK -> false;
            default -> true;
        };
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBamboo());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 5;
    }
    /**
     * @deprecated 
     */
    

    public boolean isThick() {
        return getBambooStalkThickness().equals(BambooStalkThickness.THICK);
    }
    /**
     * @deprecated 
     */
    

    public void setThick(boolean thick) {
        setBambooStalkThickness(thick ? BambooStalkThickness.THICK : BambooStalkThickness.THIN);
    }

    public BambooStalkThickness getBambooStalkThickness() {
        return getPropertyValue(BAMBOO_STALK_THICKNESS);
    }
    /**
     * @deprecated 
     */
    

    public void setBambooStalkThickness(@NotNull BambooStalkThickness value) {
        setPropertyValue(BAMBOO_STALK_THICKNESS, value);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    public BambooLeafSize getBambooLeafSize() {
        return getPropertyValue(BAMBOO_LEAF_SIZE);
    }
    /**
     * @deprecated 
     */
    

    public void setBambooLeafSize(BambooLeafSize bambooLeafSize) {
        setPropertyValue(BAMBOO_LEAF_SIZE, bambooLeafSize);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) {
            int $19 = (int) y;
            int $20 = 1;

            for ($21nt $2 = 1; i <= 16; i++) {
                String $22 = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() - i, this.getFloorZ());
                if (Objects.equals(id, BAMBOO)) {
                    count++;
                } else {
                    break;
                }
            }

            for ($23nt $3 = 1; i <= 16; i++) {
                String $24 = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() + i, this.getFloorZ());
                if (Objects.equals(id, BAMBOO)) {
                    top++;
                    count++;
                } else {
                    break;
                }
            }

            //15格以上需要嫁接（放置竹子）
            if (count >= 15) {
                return false;
            }

            boolean $25 = false;

            Block $26 = this.up(top - (int) y + 1);
            if (block.getId() == BlockID.AIR) {
                success = grow(block);
            }

            if (success) {
                if (player != null && player.isSurvival()) {
                    item.count--;
                }
                level.addParticle(new BoneMealParticle(this));
            }

            return true;
        }
        return false;
    }
    /**
     * @deprecated 
     */
    

    public int getAge() {
        return getPropertyValue(AGE_BIT) ? 1 : 0;
    }
    /**
     * @deprecated 
     */
    

    public void setAge(int age) {
        age = MathHelper.clamp(age, 0, 1);
        setPropertyValue(AGE_BIT, age == 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }
}
