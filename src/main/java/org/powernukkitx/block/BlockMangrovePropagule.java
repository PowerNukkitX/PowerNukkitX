package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.event.level.StructureGrowEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectMangroveTree;
import org.powernukkitx.level.particle.BoneMealParticle;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.tags.BlockTags;
import org.powernukkitx.utils.random.Xoroshiro128;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

import static org.powernukkitx.block.property.CommonBlockProperties.HANGING;
import static org.powernukkitx.block.property.CommonBlockProperties.PROPAGULE_STAGE;

public class BlockMangrovePropagule extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock, Pollinable, Supportable {

    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_PROPAGULE, HANGING, PROPAGULE_STAGE);
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .canBeActivated(true)
            .isFertilizable(true)
            .waterloggingLevel(1)
            .build();
    private static final int FULLY_GROWN_HANGING_STAGE = 4;
    private static final int SAPLING_TREE_STAGE = 1;

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangrovePropagule() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangrovePropagule(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Mangrove Propagule";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    public boolean isHanging() {
        return getPropertyValue(HANGING);
    }

    public void setHanging(boolean hanging) {
        setPropertyValue(HANGING, hanging);
    }

    public int getStage() {
        return getPropertyValue(PROPAGULE_STAGE);
    }

    public void setStage(int stage) {
        setPropertyValue(PROPAGULE_STAGE, Math.max(0, Math.min(FULLY_GROWN_HANGING_STAGE, stage)));
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (!item.isFertilizer() || !canStay()) {
            return false;
        }

        if (player != null && !player.isCreative()) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
        }

        this.level.addParticle(new BoneMealParticle(this));
        if (isHanging()) {
            if (getStage() < FULLY_GROWN_HANGING_STAGE) {
                setStage(getStage() + 1);
                this.level.setBlock(this, this, true, true);
            }
            return true;
        }

        if (ThreadLocalRandom.current().nextFloat() < 0.45F) {
            advanceSaplingGrowth();
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canStay()) {
                this.getLevel().useBreakOn(this);
            }
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (!canStay()) {
                return Level.BLOCK_UPDATE_RANDOM;
            }

            if (isHanging()) {
                if (getStage() < FULLY_GROWN_HANGING_STAGE && ThreadLocalRandom.current().nextInt(7) == 0) {
                    setStage(getStage() + 1);
                    this.level.setBlock(this, this, true, true);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }

            if (ThreadLocalRandom.current().nextInt(7) == 0 && getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                advanceSaplingGrowth();
            }
            return Level.BLOCK_UPDATE_RANDOM;
        }

        return Level.BLOCK_UPDATE_NORMAL;
    }

    private void advanceSaplingGrowth() {
        if (getStage() >= SAPLING_TREE_STAGE) {
            grow();
            return;
        }

        setStage(SAPLING_TREE_STAGE);
        this.level.setBlock(this, this, true, true);
    }

    protected void grow() {
        if (isHanging() || !canStay()) {
            return;
        }

        Block original = this.clone();
        BlockManager chunkManager = new BlockManager(this.level);
        Vector3 groundPos = new Vector3(this.x, this.y - 1, this.z);
        ObjectMangroveTree tree = new ObjectMangroveTree(ThreadLocalRandom.current().nextFloat() > 0.15F);
        tree.setWithBeenest(ThreadLocalRandom.current().nextFloat() < 0.01F);
        tree.setBeeCount(ThreadLocalRandom.current().nextInt(2, 4));

        this.level.setBlock(this, Block.get(BlockID.AIR), true, false);
        boolean success = tree.generate(chunkManager, new Xoroshiro128(), this);
        StructureGrowEvent ev = new StructureGrowEvent(this, chunkManager.getBlocks());
        this.level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled() || !success) {
            this.level.setBlock(this, original, true, false);
            return;
        }

        chunkManager.applySubChunkUpdate(ev.getBlockList());
        if (this.level.getBlock(groundPos).getId().equals(BlockID.DIRT_WITH_ROOTS)) {
            this.level.setBlock(groundPos, Block.get(BlockID.DIRT));
        }
    }

    private boolean canStay() {
        return isHanging() ? up() instanceof BlockMangroveLeaves : isSupportValid(down());
    }

    boolean isSupportValid(Block block) {
        String id = block.getId();
        return isSupportDirt(block)
                || BlockID.MOSS_BLOCK.equals(id)
                || BlockID.MUD.equals(id)
                || BlockID.CLAY.equals(id);
    }

    @Override
    public Item[] getDrops(Item item) {
        return !isHanging() || getStage() >= FULLY_GROWN_HANGING_STAGE ? super.getDrops(item) : Item.EMPTY_ARRAY;
    }

    
    }
