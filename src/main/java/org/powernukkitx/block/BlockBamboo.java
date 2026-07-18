package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.enums.BambooLeafSize;
import org.powernukkitx.block.property.enums.BambooStalkThickness;
import org.powernukkitx.event.block.BlockGrowEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.particle.BoneMealParticle;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.MathHelper;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.powernukkitx.block.property.CommonBlockProperties.AGE_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.BAMBOO_LEAF_SIZE;
import static org.powernukkitx.block.property.CommonBlockProperties.BAMBOO_STALK_THICKNESS;
import static org.powernukkitx.block.property.enums.BambooLeafSize.LARGE_LEAVES;
import static org.powernukkitx.block.property.enums.BambooLeafSize.NO_LEAVES;
import static org.powernukkitx.block.property.enums.BambooLeafSize.SMALL_LEAVES;


public class BlockBamboo extends BlockTransparent implements BlockFlowerPot.FlowerPotBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO, AGE_BIT, BAMBOO_LEAF_SIZE, BAMBOO_STALK_THICKNESS);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(2)
            .resistance(5)
            .toolType(ItemTool.TYPE_AXE)
            .canPassThrough(true)
            .breaksWhenMoved(true)
            .canBeActivated(true)
            .isFertilizable(true)
            .build();

    public BlockBamboo() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBamboo(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Bamboo";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isSupportInvalid()) {
                level.scheduleUpdate(this, 0);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            level.useBreakOn(this, null, null, true);
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block up = up();
            if (getAge() == 0 && up.isAir() && level.getFullLight(up) >= BlockCrops.MINIMUM_LIGHT_LEVEL && ThreadLocalRandom.current().nextInt(3) == 0) {
                grow(up);
            }
            return type;
        }
        return 0;
    }

    public boolean grow(Block up) {
        BlockBamboo newState = new BlockBamboo();
        if (isThick()) {
            newState.setThick(true);
            newState.setBambooLeafSize(BambooLeafSize.SMALL_LEAVES);
        } else {
            newState.setBambooLeafSize(BambooLeafSize.SMALL_LEAVES);
        }
        BlockGrowEvent blockGrowEvent = new BlockGrowEvent(up, newState);
        level.getServer().getPluginManager().callEvent(blockGrowEvent);
        if (!blockGrowEvent.isCancelled()) {
            Block newState1 = blockGrowEvent.getNewState();
            newState1.x = x;
            newState1.y = up.y;
            newState1.z = z;
            newState1.level = level;
            newState1.place(toItem(), up, this, BlockFace.DOWN, 0.5, 0.5, 0.5, null);
            return true;
        }
        return false;
    }

    public int countHeight() {
        int count = 0;
        Optional<Block> opt;
        Block down = this;
        while ((opt = down.down().firstInLayers(b -> b.getId() == BAMBOO)).isPresent()) {
            down = opt.get();
            if (++count >= 16) {
                break;
            }
        }
        return count;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = down();
        String downId = down.getId();
        if (!downId.equals(BAMBOO) && !downId.equals(BAMBOO_SAPLING)) {
            BlockBambooSapling sampling = new BlockBambooSapling();
            sampling.x = x;
            sampling.y = y;
            sampling.z = z;
            sampling.level = level;
            return sampling.place(item, block, target, face, fx, fy, fz, player);
        }

        boolean canGrow = true;

        if (downId.equals(BAMBOO_SAPLING)) {
            if (player != null) {
                final AnimatePacket animatePacket = new AnimatePacket();
                animatePacket.setAction(AnimatePacket.Action.SWING);
                animatePacket.setTargetRuntimeID(player.getId());
                this.getLevel().addChunkPacket(player.getChunkX(), player.getChunkZ(), animatePacket);
            }
            setBambooLeafSize(BambooLeafSize.SMALL_LEAVES);
        }
        if (down instanceof BlockBamboo bambooDown) {
            canGrow = bambooDown.getAge() == 0;
            boolean thick = bambooDown.isThick();
            if (!thick) {
                boolean setThick = true;
                for (int i = 2; i <= 3; i++) {
                    if (!getSide(BlockFace.DOWN, i).getId().equals(BAMBOO)) {
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

        int height = canGrow ? countHeight() : 0;
        if (!canGrow || height >= 15 || height >= 11 && ThreadLocalRandom.current().nextFloat() < 0.25F) {
            setAge(1);
        }

        this.level.setBlock(this, this, false, true);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        Optional<Block> down = down().firstInLayers(b -> b instanceof BlockBamboo);
        if (down.isPresent()) {
            BlockBamboo bambooDown = (BlockBamboo) down.get();
            int height = bambooDown.countHeight();
            if (height < 15 && (height < 11 || ThreadLocalRandom.current().nextFloat() >= 0.25F)) {
                bambooDown.setAge(0);
                this.level.setBlock(bambooDown, bambooDown.layer, bambooDown, false, true);
            }
        }
        return super.onBreak(item);
    }

    
    private boolean isSupportInvalid() {
        return switch (down().getId()) {
            case BAMBOO, DIRT, GRASS_BLOCK, SAND, GRAVEL, PODZOL, BAMBOO_SAPLING, MOSS_BLOCK, MUD,
                 MUDDY_MANGROVE_ROOTS -> false;
            default -> true;
        };
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBamboo());
    }

    public boolean isThick() {
        return getBambooStalkThickness().equals(BambooStalkThickness.THICK);
    }

    public void setThick(boolean thick) {
        setBambooStalkThickness(thick ? BambooStalkThickness.THICK : BambooStalkThickness.THIN);
    }

    public BambooStalkThickness getBambooStalkThickness() {
        return getPropertyValue(BAMBOO_STALK_THICKNESS);
    }

    public void setBambooStalkThickness(@NotNull BambooStalkThickness value) {
        setPropertyValue(BAMBOO_STALK_THICKNESS, value);
    }

    
    public BambooLeafSize getBambooLeafSize() {
        return getPropertyValue(BAMBOO_LEAF_SIZE);
    }

    public void setBambooLeafSize(BambooLeafSize bambooLeafSize) {
        setPropertyValue(BAMBOO_LEAF_SIZE, bambooLeafSize);
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) {
            int top = (int) y;
            int count = 1;

            for (int i = 1; i <= 16; i++) {
                String id = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() - i, this.getFloorZ());
                if (Objects.equals(id, BAMBOO)) {
                    count++;
                } else {
                    break;
                }
            }

            for (int i = 1; i <= 16; i++) {
                String id = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() + i, this.getFloorZ());
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

            boolean success = false;

            Block block = this.up(top - (int) y + 1);
            if (block.getId().equals(BlockID.AIR)) {
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

    public int getAge() {
        return getPropertyValue(AGE_BIT) ? 1 : 0;
    }

    public void setAge(int age) {
        age = MathHelper.clamp(age, 0, 1);
        setPropertyValue(AGE_BIT, age == 1);
    }

    
    }
