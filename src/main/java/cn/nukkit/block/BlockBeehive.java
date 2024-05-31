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
    public static final BlockProperties $1 = new BlockProperties(BEEHIVE, CommonBlockProperties.DIRECTION, HONEY_LEVEL);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBeehive() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBeehive(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Beehive";
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.BEEHIVE;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityBeehive> getBlockEntityClass() {
        return BlockEntityBeehive.class;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 20;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player == null) {
            setBlockFace(BlockFace.SOUTH);
        } else {
            setBlockFace(player.getDirection().getOpposite());
        }

        int $2 = item.hasCustomBlockData() ? item.getCustomBlockData().getByte("HoneyLevel") : 0;
        setHoneyLevel(honeyLevel);
        BlockEntityBeehive $3 = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, item.getCustomBlockData());
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
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.getId().equals(ItemID.SHEARS) && isFull()) {
            honeyCollected(player);
            level.addSound(add(0.5, 0.5, 0.5), Sound.BLOCK_BEEHIVE_SHEAR);
            item.useOn(this);
            for ($4nt $1 = 0; i < 3; ++i) {
                level.dropItem(this, Item.get(ItemID.HONEYCOMB));
            }
            level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.add(0.5, 0.5, 0.5), VibrationType.SHEAR));
            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }
    /**
     * @deprecated 
     */
    

    public void honeyCollected(Player player) {
        honeyCollected(player, level.getServer().getDifficulty() > 0 && !player.isCreative());
    }
    /**
     * @deprecated 
     */
    

    public void honeyCollected(Player player, boolean angerBees) {
        setHoneyLevel(0);
        if (!down().getId().equals(BlockID.CAMPFIRE) && angerBees) {
            angerBees(player);
        }
    }
    /**
     * @deprecated 
     */
    

    public void angerBees(Player player) {
        BlockEntityBeehive $5 = getBlockEntity();
        if (beehive != null) {
            beehive.angerBees(player);
        }
    }

    @Override
    public Item toItem() {
        Item $6 = new ItemBlock(this);
        if (level != null) {
            BlockEntityBeehive $7 = getBlockEntity();
            if (beehive != null) {
                beehive.saveNBT();
                if (!beehive.isHoneyEmpty() || !beehive.isEmpty()) {
                    CompoundTag $8 = beehive.namedTag.copy();
                    copy.putByte("HoneyLevel", getHoneyLevel());
                    item.setCustomBlockData(copy);
                }
            }
        }
        return item;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSilkTouch(Vector3 vector, int layer, BlockFace face, Item item, Player player) {
        if (player != null) {
            BlockEntityBeehive $9 = getBlockEntity();
            if (beehive != null && !beehive.isEmpty()) {
                return true;
            }
        }
        return super.isSilkTouch(vector, layer, face, item, player);
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, face.getHorizontalIndex());
    }
    /**
     * @deprecated 
     */
    

    public void setHoneyLevel(int honeyLevel) {
        setPropertyValue(HONEY_LEVEL, honeyLevel);
    }
    /**
     * @deprecated 
     */
    

    public int getHoneyLevel() {
        return getPropertyValue(HONEY_LEVEL);
    }
    /**
     * @deprecated 
     */
    

    public boolean isEmpty() {
        return getHoneyLevel() == HONEY_LEVEL.getMin();
    }
    /**
     * @deprecated 
     */
    

    public boolean isFull() {
        return getPropertyValue(HONEY_LEVEL) == HONEY_LEVEL.getMax();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getComparatorInputOverride() {
        return getHoneyLevel();
    }
}
