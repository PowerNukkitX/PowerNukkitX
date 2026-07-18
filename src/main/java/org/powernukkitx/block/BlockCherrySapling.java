package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.event.level.StructureGrowEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectCherryTree;
import org.powernukkitx.level.particle.BoneMealParticle;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static org.powernukkitx.block.property.CommonBlockProperties.AGE_BIT;


public class BlockCherrySapling extends BlockSapling implements BlockFlowerPot.FlowerPotBlock {

    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_SAPLING, AGE_BIT);
    public static final BlockDefinition DEFINITION = BlockSapling.DEFINITION.toBuilder()
            .canBeActivated(true)
            .isFertilizable(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherrySapling() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherrySapling(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.CHERRY;
    }

    @Override
    public String getName() {
        return "Cherry Sapling";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isSupportInvalid()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { //Growth
            if (getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                if (isAged()) {
                    this.grow();
                } else {
                    setAged(true);
                    this.getLevel().setBlock(this, this, true);
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return Level.BLOCK_UPDATE_NORMAL;
    }

    private void grow() {
        BlockManager blockManager = new BlockManager(this.level);
        Vector3 vector3 = new Vector3(this.x, this.y - 1, this.z);
        var objectCherryTree = new ObjectCherryTree();
        boolean generate = objectCherryTree.generate(blockManager, RandomSourceProvider.create(), this);
        if (generate) {
            StructureGrowEvent ev = new StructureGrowEvent(this, blockManager.getBlocks());
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return;
            }
            if (this.level.getBlock(vector3).getId().equals(BlockID.DIRT_WITH_ROOTS)) {
                this.level.setBlock(vector3, Block.get(BlockID.DIRT));
            }
            blockManager.applySubChunkUpdate(ev.getBlockList(), block -> !block.isAir());
        }
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (isSupportInvalid()) {
            return false;
        }

        if (this.getLevelBlock() instanceof BlockLiquid || this.getLevelBlockAtLayer(1) instanceof BlockLiquid) {
            return false;
        }

        this.level.setBlock(this, this, true, true);
        return true;
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) { // BoneMeal and so on
            if (player != null && !player.isCreative()) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));
            if (ThreadLocalRandom.current().nextFloat() >= 0.45) {
                return true;
            }

            this.grow();

            return true;
        }
        return false;
    }

    private boolean isSupportInvalid() {
        String downId = down().getId();
        return !(downId.equals(DIRT) || downId.equals(GRASS_BLOCK) || downId.equals(SAND) || downId.equals(GRAVEL) || downId.equals(PODZOL));
    }

    public boolean isAge() {
        return this.getPropertyValue(AGE_BIT);
    }

    public void setAge(boolean age) {
        this.setPropertyValue(AGE_BIT, age);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockCherrySapling());
    }

    }
