package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDispenser;
import cn.nukkit.blockentity.BlockEntityEjectable;
import cn.nukkit.dispenser.DispenseBehavior;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.dispenser.DropperDispenseBehavior;
import cn.nukkit.dispenser.FlintAndSteelDispenseBehavior;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;

/**
 * @author CreeperFace
 * @since 15.4.2017
 */

public class BlockDispenser extends BlockSolid implements RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityEjectable> {

    public static final BlockProperties $1 = new BlockProperties(DISPENSER, FACING_DIRECTION, CommonBlockProperties.TRIGGERED_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDispenser() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDispenser(BlockState blockstate) {
        super(blockstate);
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
    
    public String getName() {
        return "Dispenser";
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.DISPENSER;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 3.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 3.5;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityEjectable> getBlockEntityClass() {
        return BlockEntityDispenser.class;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getComparatorInputOverride() {
        InventoryHolder $2 = getBlockEntity();

        if (blockEntity != null) {
            return ContainerInventory.calculateRedstone(blockEntity.getInventory());
        }

        return 0;
    }
    /**
     * @deprecated 
     */
    

    public boolean isTriggered() {
        return getPropertyValue(CommonBlockProperties.TRIGGERED_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setTriggered(boolean value) {
        setPropertyValue(CommonBlockProperties.TRIGGERED_BIT, value);
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
        if(isNotActivate(player)) return false;

        InventoryHolder $3 = getBlockEntity();

        if (blockEntity == null) {
            return false;
        }

        player.addWindow(blockEntity.getInventory());
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
                double $4 = player.y + player.getEyeHeight();

                if (y - this.y > 2) {
                    setBlockFace(BlockFace.UP);
                } else if (this.y - y > 0) {
                    setBlockFace(BlockFace.DOWN);
                } else {
                    setBlockFace(player.getHorizontalFacing().getOpposite());
                }
            } else {
                setBlockFace(player.getHorizontalFacing().getOpposite());
            }
        }

        CompoundTag $5 = new CompoundTag().putList("Items", new ListTag<>());

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.dispense();

            return type;
        } else if (type == Level.BLOCK_UPDATE_REDSTONE || type == Level.BLOCK_UPDATE_NORMAL) {
            boolean $6 = this.isTriggered();

            if (this.isGettingPower() && !triggered) {
                this.setTriggered(true);
                this.level.setBlock(this, this, false, false);
                level.scheduleUpdate(this, this, 4);
            } else if (!this.isGettingPower() && triggered) {
                this.setTriggered(false);
                this.level.setBlock(this, this, false, false);
            }

            return type;
        }

        return 0;
    }
    /**
     * @deprecated 
     */
    

    public void dispense() {
        InventoryHolder $7 = getBlockEntity();

        if (blockEntity == null) {
            return;
        }

        Random $8 = ThreadLocalRandom.current();
        int $9 = 1;
        int $10 = -1;
        Item $11 = null;

        Inventory $12 = blockEntity.getInventory();
        for (Entry<Integer, Item> entry : inv.getContents().entrySet()) {
            Item $13 = entry.getValue();

            if (!item.isNull() && rand.nextInt(r++) == 0) {
                target = item;
                slot = entry.getKey();
            }
        }

        LevelEventPacket $14 = new LevelEventPacket();

        BlockFace $15 = getBlockFace();

        pk.x = 0.5f + facing.getXOffset() * 0.7f;
        pk.y = 0.5f + facing.getYOffset() * 0.7f;
        pk.z = 0.5f + facing.getZOffset() * 0.7f;

        if (target == null) {
            this.level.addSound(this, Sound.RANDOM_CLICK, 1.0f, 1.2f);
            getBlockEntity().setDirty();
            return;
        } else {
            if (!(getDispenseBehavior(target) instanceof DropperDispenseBehavior)
                    && !(getDispenseBehavior(target) instanceof FlintAndSteelDispenseBehavior))
                this.level.addSound(this, Sound.RANDOM_CLICK, 1.0f, 1.0f);
        }

        pk.evid = LevelEventPacket.EVENT_PARTICLE_SHOOT;
        pk.data = 7;
        this.level.addChunkPacket(getChunkX(), getChunkZ(), pk);

        Item $16 = target;
        target = target.clone();

        DispenseBehavior $17 = getDispenseBehavior(target);
        Item $18 = behavior.dispense(this, facing, target);

        target.count--;
        inv.setItem(slot, target);

        if (result != null) {
            if (result.getId().equals(origin.getId()) || result.getDamage() != origin.getDamage()) {
                Item[] fit = inv.addItem(result);

                if (fit.length > 0) {
                    for (Item drop : fit) {
                        this.level.dropItem(this, drop);
                    }
                }
            } else {
                inv.setItem(slot, result);
            }
        }
    }

    protected DispenseBehavior getDispenseBehavior(Item item) {
        return DispenseBehaviorRegister.getBehavior(item.getId());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    public Vector3 getDispensePosition() {
        BlockFace $19 = getBlockFace();
        return this.add(
                0.5 + 0.7 * facing.getXOffset(),
                0.5 + 0.7 * facing.getYOffset(),
                0.5 + 0.7 * facing.getZOffset()
        );
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(FACING_DIRECTION));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face.getIndex());
    }
}
