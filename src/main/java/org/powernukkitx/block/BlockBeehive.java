package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityBeehive;
import org.powernukkitx.event.player.PlayerInteractEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.HONEY_LEVEL;


public class BlockBeehive extends BlockSolid implements Faceable, BlockEntityHolder<BlockEntityBeehive> {
    public static final BlockProperties PROPERTIES = new BlockProperties(BEEHIVE, CommonBlockProperties.DIRECTION, HONEY_LEVEL);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBeehive() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBeehive(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Beehive";
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.BEEHIVE;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityBeehive> getBlockEntityClass() {
        return BlockEntityBeehive.class;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player == null) {
            setBlockFace(BlockFace.SOUTH);
        } else {
            setBlockFace(player.getDirection().getOpposite());
        }

        int honeyLevel = item.hasCustomBlockData() ? item.getCustomBlockData().getByte("HoneyLevel") : 0;
        setHoneyLevel(honeyLevel);
        BlockEntityBeehive beehive = BlockEntityHolder.setBlockAndCreateEntity(this, false, true, item.getCustomBlockData());
        if (beehive == null) {
            return false;
        }

        // Stagger their exit by giving each occupant a different ticksLeftToStay
        // and let BlockEntityBeehive.onUpdate() handle spawning them over time.
        if (beehive.getNbt().getByte("ShouldSpawnBees") > 0) {
            BlockEntityBeehive.Occupant[] occupants = beehive.getOccupants();
            int delayPerBee = 40; // 40 ticks
            int baseDelay = 20;   // small initial delay so they dont pop instantly on place

            int index = 0;
            for (BlockEntityBeehive.Occupant occupant : occupants) {
                int ticks = baseDelay + (index * delayPerBee);
                occupant.setTicksLeftToStay(ticks);
                index++;
            }

            beehive.getNbt().putByte("ShouldSpawnBees", (byte) 0);
            beehive.scheduleUpdate();
        }

        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.getId().equals(ItemID.SHEARS) && isFull()) {
            honeyCollected(player);
            level.addSound(add(0.5, 0.5, 0.5), Sound.BLOCK_BEEHIVE_SHEAR);
            item.useOn(this);
            for (int i = 0; i < 3; ++i) {
                level.dropItem(this, Item.get(ItemID.HONEYCOMB));
            }
            level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.add(0.5, 0.5, 0.5), VibrationType.SHEAR));
            return true;
        }
        return false;
    }

    @Override
    public void onTouch(@NotNull Vector3 vector, @NotNull Item item, @NotNull BlockFace face, float fx, float fy, float fz, @Nullable Player player, PlayerInteractEvent.@NotNull Action action) {
        if(player != null) this.getOrCreateBlockEntity().setInteractingEntity(player);
        super.onTouch(vector, item, face, fx, fy, fz, player, action);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public void honeyCollected(Player player) {
        honeyCollected(player, level.getServer().getDifficulty() > 0 && !player.isCreative());
    }

    public void honeyCollected(Player player, boolean angerBees) {
        getOrCreateBlockEntity().setHoneyLevel(0);
        if (!down().getId().equals(BlockID.CAMPFIRE) && angerBees) {
            angerBees(player);
        }
    }

    public void angerBees(Player player) {
        BlockEntityBeehive beehive = getBlockEntity();
        if (beehive != null) {
            beehive.angerBees(player);
        }
    }

    @Override
    public Item toItem() {
        Item item = new ItemBlock(this);
        if (level != null) {
            BlockEntityBeehive beehive = getBlockEntity();
            if (beehive != null) {
                beehive.saveNBT();
                if (!beehive.isHoneyEmpty() || !beehive.isEmpty()) {
                    CompoundTag copy = new CompoundTag();
                    copy.putByte("HoneyLevel", getHoneyLevel());
                    item.setCustomBlockData(copy);
                }
            }
        }
        return item;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getPropertyValue(CommonBlockProperties.DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, face.getHorizontalIndex());
    }

    public void setHoneyLevel(int honeyLevel) {
        setPropertyValue(HONEY_LEVEL, honeyLevel);
    }

    public int getHoneyLevel() {
        return getPropertyValue(HONEY_LEVEL);
    }

    public boolean isEmpty() {
        return getHoneyLevel() == HONEY_LEVEL.getMin();
    }

    public boolean isFull() {
        return getPropertyValue(HONEY_LEVEL) == HONEY_LEVEL.getMax();
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        return getHoneyLevel();
    }
}