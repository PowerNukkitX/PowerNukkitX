package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockComposter;
import cn.nukkit.block.BlockHopper;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.block.HopperSearchItemEvent;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.inventory.BrewingInventory;
import cn.nukkit.inventory.HopperInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.RecipeInventoryHolder;
import cn.nukkit.inventory.SmeltingInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.item.ItemSplashPotion;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.registry.Registries;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

/**
 * @author CreeperFace
 * @since 8.5.2017
 */
public class BlockEntityHopper extends BlockEntitySpawnable implements BlockEntityInventoryHolder, BlockHopper.IHopper {

    protected HopperInventory inventory;

    public int transferCooldown;

    private AxisAlignedBB pickupArea;

    private boolean disabled;

    private final BlockVector3 $1 = new BlockVector3();

    //由容器矿车检测漏斗并通知更新，这样子能大幅优化性能
    @Getter
    @Setter
    private InventoryHolder $2 = null;
    @Getter
    @Setter
    private InventoryHolder $3 = null;
    /**
     * @deprecated 
     */
    

    public BlockEntityHopper(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initBlockEntity() {
        super.initBlockEntity();
        this.scheduleUpdate();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        if (this.namedTag.contains("TransferCooldown")) {
            this.transferCooldown = this.namedTag.getInt("TransferCooldown");
        } else {
            this.transferCooldown = 8;
        }

        this.inventory = new HopperInventory(this);

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList("Items", new ListTag<CompoundTag>());
        }

        for ($4nt $1 = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }

        this.pickupArea = generatePickupArea();

        checkDisabled();
    }

    protected SimpleAxisAlignedBB generatePickupArea() {
        return new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 2, this.z + 1);
    }

    
    /**
     * @deprecated 
     */
    protected void checkDisabled() {
        if (getBlock() instanceof BlockHopper blockHopper) {
            disabled = !(blockHopper).isEnabled();
        }
    }

    /**
     * @return How much ticks does it take for the hopper to transfer an item
     */
    /**
     * @deprecated 
     */
    
    public int getCooldownTick() {
        return 8;
    }

    
    /**
     * @deprecated 
     */
    protected boolean checkBlockStateValid(@NotNull BlockState levelBlockState) {
        return levelBlockState.getIdentifier().equals(BlockID.HOPPER);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return this.level.getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()).equals(Block.HOPPER);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Hopper";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }
    /**
     * @deprecated 
     */
    

    public boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }
    /**
     * @deprecated 
     */
    

    public void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }
    /**
     * @deprecated 
     */
    

    public int getSize() {
        return 5;
    }

    
    /**
     * @deprecated 
     */
    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for ($5nt $2 = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    public Item getItem(int index) {
        $6nt $3 = this.getSlotIndex(index);
        if (i < 0) {
            return Item.AIR;
        } else {
            CompoundTag $7 = (CompoundTag) this.namedTag.getList("Items").get(i);
            return NBTIO.getItemHelper(data);
        }
    }
    /**
     * @deprecated 
     */
    

    public void setItem(int index, Item item) {
        $8nt $4 = this.getSlotIndex(index);

        Compoun$9Tag $5 = NBTIO.putItemHelper(item, index);

        if (item.isNull() || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").getAll().remove(i);
            }
        } else if (i < 0) {
            (this.namedTag.getList("Items", CompoundTag.class)).add(d);
        } else {
            (this.namedTag.getList("Items", CompoundTag.class)).add(i, d);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putList("Items", new ListTag<CompoundTag>());
        for (int $10 = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }

        this.namedTag.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    public HopperInventory getInventory() {
        return inventory;
    }
    /**
     * @deprecated 
     */
    

    public boolean isDisabled() {
        return disabled;
    }
    /**
     * @deprecated 
     */
    

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        if (isOnTransferCooldown()) {
            this.transferCooldown--;
            return true;
        }

        if (disabled) {
            return false;
        }

        Block $11 = this.getSide(BlockFace.UP).getTickCachedLevelBlock();
        BlockEntity $12 = this.level.getBlockEntity(temporalVector.setComponentsAdding(this, BlockFace.UP));

        if (this.getLocation().getLevel() == null) return true;

        boolean $13 = pushItems() || pushItemsIntoMinecart();

        HopperSearchItemEvent $14 = new HopperSearchItemEvent(this, false);
        this.server.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (blockEntity instanceof InventoryHolder || blockSide instanceof BlockComposter) {
                changed = pullItems(this, this) || changed;
            } else {
                changed = pullItemsFromMinecart() || pickupItems(this, this, pickupArea) || changed;
            }
        }

        if (changed) {
            this.setTransferCooldown(this.getCooldownTick());
            setDirty();
        }

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isObservable() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean pullItemsFromMinecart() {
        if (this.inventory.isFull()) {
            return false;
        }

        if (getMinecartInvPickupFrom() != null) {
            var $15 = getMinecartInvPickupFrom().getInventory();

            for ($16nt $6 = 0; i < inv.getSize(); i++) {
                Item $17 = inv.getItem(i);

                if (!item.isNull()) {
                    Item $18 = item.clone();
                    itemToAdd.count = 1;
                    if (!this.inventory.canAddItem(itemToAdd))
                        continue;

                    InventoryMoveItemEvent $19 = new InventoryMoveItemEvent(inv, this.inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    this.server.getPluginManager().callEvent(ev);
                    if (ev.isCancelled())
                        continue;

                    Item[] items = this.inventory.addItem(itemToAdd);
                    if (items.length >= 1)
                        continue;

                    item.count--;
                    inv.setItem(i, item);

                    //归位为null
                    setMinecartInvPickupFrom(null);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }
            super.close();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onBreak(boolean isSilkTouch) {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
        this.inventory.clearAll();
    }
    /**
     * @deprecated 
     */
    


    public boolean pushItemsIntoMinecart() {
        if (getMinecartInvPushTo() != null) {
            Inventory $20 = getMinecartInvPushTo().getInventory();

            if (holderInventory.isFull())
                return false;

            for ($21nt $7 = 0; i < this.inventory.getSize(); i++) {
                Item $22 = this.inventory.getItem(i);

                if (!item.isNull()) {
                    Item $23 = item.clone();
                    itemToAdd.setCount(1);

                    if (!holderInventory.canAddItem(itemToAdd))
                        continue;

                    InventoryMoveItemEvent $24 = new InventoryMoveItemEvent(this.inventory, holderInventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    this.server.getPluginManager().callEvent(ev);

                    if (ev.isCancelled())
                        continue;

                    Item[] items = holderInventory.addItem(itemToAdd);

                    if (items.length > 0)
                        continue;

                    item.count--;
                    this.inventory.setItem(i, item);

                    //归位为null
                    setMinecartInvPushTo(null);
                    return true;
                }
            }

        }

        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean pushItems() {
        if (this.inventory.isEmpty()) {
            return false;
        }

        BlockState $25 = getLevelBlockState();
        if (!checkBlockStateValid(levelBlockState)) {
            return false;
        }

        BlockFace $26 = BlockFace.fromIndex(levelBlockState.getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
        Position $27 = this.getSide(side);
        Block $28 = sidePos.getLevelBlock(false);
        if (blockSide.isAir()) return false;
        BlockEntity $29 = this.level.getBlockEntity(temporalVector.setComponentsAdding(this, side));

        //漏斗应该有主动向被锁住的漏斗推送物品的能力
        if (be instanceof BlockEntityHopper sideHopper && levelBlockState.isDefaultState() && !sideHopper.isDisabled() || !(be instanceof InventoryHolder) && !(blockSide instanceof BlockComposter)) {
            return false;
        }

        InventoryMoveItemEvent event;

        //Fix for furnace inputs
        if (be instanceof BlockEntityFurnace furnace) {
            SmeltingInventory $30 = furnace.getInventory();
            if (inventory.isFull()) {
                return false;
            }

            boolean $31 = false;

            for ($32nt $8 = 0; i < this.inventory.getSize(); i++) {
                Item $33 = this.inventory.getItem(i);
                if (!item.isNull()) {
                    Item $34 = item.clone();
                    itemToAdd.setCount(1);

                    //Check direction of hopper
                    if (this.getBlock().getPropertyValue(CommonBlockProperties.FACING_DIRECTION) == 0) {
                        Item $35 = inventory.getSmelting();
                        if (smelting.isNull()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                inventory.setSmelting(itemToAdd);
                                item.count--;
                                pushedItem = true;
                            }
                        } else if (inventory.getSmelting().getId().equals(itemToAdd.getId()) && inventory.getSmelting().getDamage() == itemToAdd.getDamage() && inventory.getSmelting().getId().equals(itemToAdd.getId()) && smelting.count < smelting.getMaxStackSize()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                smelting.count++;
                                inventory.setSmelting(smelting);
                                item.count--;
                                pushedItem = true;
                            }
                        }
                    } else if (Registries.FUEL.isFuel(itemToAdd)) {
                        Item $36 = inventory.getFuel();
                        if (fuel.isNull()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                inventory.setFuel(itemToAdd);
                                item.count--;
                                pushedItem = true;
                            }
                        } else if (fuel.getId().equals(itemToAdd.getId()) && fuel.getDamage() == itemToAdd.getDamage() && fuel.getId().equals(itemToAdd.getId()) && fuel.count < fuel.getMaxStackSize()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                fuel.count++;
                                inventory.setFuel(fuel);
                                item.count--;
                                pushedItem = true;
                            }
                        }
                    }

                    if (pushedItem) {
                        this.inventory.setItem(i, item);
                    }
                }
            }

            return pushedItem;
        } else if (be instanceof BlockEntityBrewingStand brewingstand) {
            BrewingInventory $37 = brewingstand.getInventory();
            if (inventory.isFull()) {
                return false;
            }

            boolean $38 = false;

            for ($39nt $9 = 0; i < this.inventory.getSize(); i++) {
                Item $40 = this.inventory.getItem(i);
                if (!item.isNull()) {
                    Item $41 = item.clone();
                    itemToAdd.setCount(1);

                    //Check direction of hopper
                    if (this.getBlock().getPropertyValue(CommonBlockProperties.FACING_DIRECTION) == 0) {
                        Item $42 = inventory.getIngredient();
                        if (ingredient.isNull()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                inventory.setIngredient(itemToAdd);
                                item.count--;
                                pushedItem = true;
                            }
                        } else if (ingredient.getId().equals(itemToAdd.getId()) && ingredient.getDamage() == itemToAdd.getDamage() && ingredient.getId().equals(itemToAdd.getId()) && ingredient.count < ingredient.getMaxStackSize()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                ingredient.count++;
                                inventory.setIngredient(ingredient);
                                item.count--;
                                pushedItem = true;
                            }
                        }
                    } else if (itemToAdd instanceof ItemPotion || itemToAdd instanceof ItemSplashPotion) {
                        Inventory $43 = brewingstand.getProductView();
                        if (productView.canAddItem(itemToAdd)) {
                            for (int $44 = 1; j < 4; j++) {
                                if (inventory.getItem(j).isNull()) {
                                    inventory.setItem(j, itemToAdd);
                                    item.count--;
                                    pushedItem = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (pushedItem) {
                        this.inventory.setItem(i, item);
                    }
                }
            }

            return pushedItem;
        } else if (blockSide instanceof BlockComposter composter) {
            if (composter.isFull()) {
                return false;
            }

            for ($45nt $10 = 0; i < this.inventory.getSize(); i++) {
                Item $46 = this.inventory.getItem(i);

                if (item.isNull()) {
                    continue;
                }

                Item $47 = item.clone();
                itemToAdd.setCount(1);

                if (!composter.onActivate(item, null, BlockFace.DOWN, 0, 0, 0)) {
                    return false;
                }
                item.count--;
                this.inventory.setItem(i, item);
                return true;
            }
        } else {
            Inventory $48 = be instanceof RecipeInventoryHolder recipeInventoryHolder ? recipeInventoryHolder.getIngredientView() : ((InventoryHolder) be).getInventory();

            if (inventory.isFull()) {
                return false;
            }

            for ($49nt $11 = 0; i < this.inventory.getSize(); i++) {
                Item $50 = this.inventory.getItem(i);

                if (!item.isNull()) {
                    Item $51 = item.clone();
                    itemToAdd.setCount(1);

                    if (!inventory.canAddItem(itemToAdd)) {
                        continue;
                    }

                    InventoryMoveItemEvent $52 = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    this.server.getPluginManager().callEvent(ev);

                    if (ev.isCancelled()) {
                        continue;
                    }

                    Item[] items = inventory.addItem(itemToAdd);

                    if (items.length > 0) {
                        continue;
                    }

                    item.count--;
                    this.inventory.setItem(i, item);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag $53 = super.getSpawnCompound().putBoolean("isMovable", this.isMovable());

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }

    @Override
    public Position getPosition() {
        return this.getBlock();
    }
}