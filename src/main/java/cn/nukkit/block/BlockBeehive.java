package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static cn.nukkit.block.property.CommonBlockProperties.HONEY_LEVEL;


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
        BlockEntityBeehive beehive = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, item.getCustomBlockData());
        if (beehive == null) {
            return false;
        }

        if (beehive.namedTag.getByte("ShouldSpawnBees") > 0) {
            List<BlockFace> validSpawnFaces = beehive.scanValidSpawnFaces(true);
            for (BlockEntityBeehive.Occupant occupant : beehive.getOccupants()) {
                beehive.spawnOccupant(occupant, validSpawnFaces);
            }

            beehive.namedTag.putByte("ShouldSpawnBees", 0);
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
    public boolean canBeActivated() {
        return true;
    }

    public void honeyCollected(Player player) {
        honeyCollected(player, level.getServer().getDifficulty() > 0 && !player.isCreative());
    }

    public void honeyCollected(Player player, boolean angerBees) {
        setHoneyLevel(0);
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
                    CompoundTag copy = beehive.namedTag.copy();
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
    public boolean isSilkTouch(Vector3 vector, int layer, BlockFace face, Item item, Player player) {
        if (player != null) {
            BlockEntityBeehive beehive = getBlockEntity();
            if (beehive != null && !beehive.isEmpty()) {
                return true;
            }
        }
        return super.isSilkTouch(vector, layer, face, item, player);
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(BlockID.BEEHIVE)};
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
