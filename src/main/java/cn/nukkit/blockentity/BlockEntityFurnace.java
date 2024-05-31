package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.inventory.FurnaceBurnEvent;
import cn.nukkit.event.inventory.FurnaceSmeltEvent;
import cn.nukkit.inventory.FurnaceTypeInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventorySlice;
import cn.nukkit.inventory.RecipeInventoryHolder;
import cn.nukkit.inventory.SmeltingInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemLavaBucket;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ContainerSetDataPacket;
import cn.nukkit.recipe.SmeltingRecipe;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 */
public class BlockEntityFurnace extends BlockEntitySpawnable implements RecipeInventoryHolder, BlockEntityInventoryHolder {
    protected SmeltingInventory inventory;

    protected int burnTime;
    protected int burnDuration;
    protected int cookTime;
    protected int maxTime;
    protected float storedXP;
    private int crackledTime;
    /**
     * @deprecated 
     */
    

    public BlockEntityFurnace(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected SmeltingInventory createInventory() {
        return new FurnaceTypeInventory(this);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initBlockEntity() {
        super.initBlockEntity();
        if (burnTime > 0) {
            this.scheduleUpdate();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        this.inventory = createInventory();

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList("Items", new ListTag<CompoundTag>());
        }

        for ($1nt $1 = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }

        if (!this.namedTag.contains("BurnTime") || this.namedTag.getShort("BurnTime") < 0) {
            burnTime = 0;
        } else {
            burnTime = this.namedTag.getShort("BurnTime");
        }

        if (!this.namedTag.contains("CookTime") || this.namedTag.getShort("CookTime") < 0 || (this.namedTag.getShort("BurnTime") == 0 && this.namedTag.getShort("CookTime") > 0)) {
            cookTime = 0;
        } else {
            cookTime = this.namedTag.getShort("CookTime");
        }

        if (!this.namedTag.contains("BurnDuration") || this.namedTag.getShort("BurnDuration") < 0) {
            burnDuration = 0;
        } else {
            burnDuration = this.namedTag.getShort("BurnDuration");
        }

        if (!this.namedTag.contains("MaxTime")) {
            maxTime = burnTime;
            burnDuration = 0;
        } else {
            maxTime = this.namedTag.getShort("MaxTime");
        }

        if (this.namedTag.contains("BurnTicks")) {
            burnDuration = this.namedTag.getShort("BurnTicks");
            this.namedTag.remove("BurnTicks");
        }

        if (this.namedTag.contains("StoredXpInt")) {
            storedXP = this.namedTag.getShort("StoredXpInt");
        } else {
            storedXP = 0;
        }
    }

    
    /**
     * @deprecated 
     */
    protected String getFurnaceName() {
        return "Furnace";
    }

    
    /**
     * @deprecated 
     */
    protected String getClientName() {
        return FURNACE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : getFurnaceName();
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
        if (name == null || name.equals("")) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
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
        var $2 = calculateXpDrop();
        if (xp > 0) {
            setStoredXP(0);
            level.dropExpOrb(this, xp);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putList("Items", new ListTag<CompoundTag>());
        for (int $3 = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }
        this.namedTag.putShort("CookTime", cookTime);
        this.namedTag.putShort("BurnTime", burnTime);
        this.namedTag.putShort("BurnDuration", burnDuration);
        this.namedTag.putShort("MaxTime", maxTime);
        this.namedTag.putShort("StoredXpInt", (int) storedXP);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        String $4 = getBlock().getId();
        return $5 == getIdleBlockId() || blockID == getBurningBlockId();
    }
    /**
     * @deprecated 
     */
    

    public int getSize() {
        return 3;
    }

    
    /**
     * @deprecated 
     */
    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for ($6nt $2 = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    public Item getItem(int index) {
        $7nt $3 = this.getSlotIndex(index);
        if (i < 0) {
            return Item.AIR;
        } else {
            CompoundTag $8 = (CompoundTag) this.namedTag.getList("Items").get(i);
            return NBTIO.getItemHelper(data);
        }
    }
    /**
     * @deprecated 
     */
    

    public void setItem(int index, Item item) {
        $9nt $4 = this.getSlotIndex(index);

        Compoun$10Tag $5 = NBTIO.putItemHelper(item, index);

        if (item.isNull()) {
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
    public SmeltingInventory getInventory() {
        return inventory;
    }

    
    /**
     * @deprecated 
     */
    protected String getIdleBlockId() {
        return Block.FURNACE;
    }

    
    /**
     * @deprecated 
     */
    protected String getBurningBlockId() {
        return Block.LIT_FURNACE;
    }

    
    /**
     * @deprecated 
     */
    protected void setBurning(boolean burning) {
        if (burning) {
            if (this.getBlock().getId().equals(getIdleBlockId())) {
                this.getLevel().setBlock(this, Block.getWithState(getBurningBlockId(), this.getBlock().getBlockState()), true);
            }
        } else if (this.getBlock().getId().equals(getBurningBlockId())) {
            this.getLevel().setBlock(this, Block.getWithState(getIdleBlockId(), this.getBlock().getBlockState()), true);
        }
    }

    
    /**
     * @deprecated 
     */
    protected void checkFuel(Item fuel) {
        FurnaceBurnEvent $11 = new FurnaceBurnEvent(this, fuel, fuel.getFuelTime() == null ? 0 : fuel.getFuelTime());
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        maxTime = (int) Math.ceil(ev.getBurnTime() / (float) getSpeedMultiplier());
        burnTime = (int) Math.ceil(ev.getBurnTime() / (float) getSpeedMultiplier());
        burnDuration = 0;
        setBurning(true);

        if (burnTime > 0 && ev.isBurning()) {
            fuel.setCount(fuel.getCount() - 1);
            if (fuel.getCount() == 0) {
                if (fuel instanceof ItemLavaBucket) {
                    fuel.setDamage(0);
                    fuel.setCount(1);
                } else {
                    fuel = Item.AIR;
                }
            }
            this.inventory.setFuel(fuel);
        }
    }

    protected SmeltingRecipe matchRecipe(Item raw) {
        return this.server.getRecipeRegistry().findFurnaceRecipe(raw);
    }

    
    /**
     * @deprecated 
     */
    protected int getSpeedMultiplier() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        boolean $12 = false;
        Item $13 = this.inventory.getFuel();
        Item $14 = this.inventory.getSmelting();
        Item $15 = this.inventory.getResult();
        SmeltingRecipe $16 = matchRecipe(raw);

        boolean $17 = false;
        if (smelt != null) {
            canSmelt = (raw.getCount() > 0 && ((smelt.getResult().equals(product, true) && product.getCount() < product.getMaxStackSize()) || product.getId() == BlockID.AIR));
            //检查输入
            if (!smelt.getInput().toItem().equals(raw, true, false)) {
                canSmelt = false;
            }
        }

        if (burnTime <= 0 && canSmelt && fuel.getFuelTime() != null && fuel.getCount() > 0) {
            this.checkFuel(fuel);
        }

        if (burnTime > 0) {
            burnTime--;
            int $18 = 200 / getSpeedMultiplier();
            burnDuration = (int) Math.ceil((float) burnTime / maxTime * readyAt);

            if (this.crackledTime-- <= 0) {
                this.crackledTime = ThreadLocalRandom.current().nextInt(20, 100);
                this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_FURNACE_LIT);
            }

            if (smelt != null && canSmelt) {
                cookTime++;
                if (cookTime >= readyAt) {
                    int $19 = product.getCount() + 1;
                    product = smelt.getResult().clone();
                    product.setCount(count);

                    FurnaceSmeltEvent $20 = new FurnaceSmeltEvent(this, raw, product, (float) this.server.getRecipeRegistry().getRecipeXp(smelt));
                    this.server.getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.inventory.setResult(ev.getResult());
                        raw.setCount(raw.getCount() - 1);
                        if (raw.getCount() == 0) {
                            raw = Item.AIR;
                        }
                        this.storedXP += ev.getXp();
                        this.inventory.setSmelting(raw);
                    }

                    cookTime -= readyAt;
                }
            } else if (burnTime <= 0) {
                burnTime = 0;
                cookTime = 0;
                burnDuration = 0;
            } else {
                cookTime = 0;
            }
            ret = true;
        } else {
            setBurning(false);
            burnTime = 0;
            cookTime = 0;
            burnDuration = 0;
            this.crackledTime = 0;
        }

        for (Player player : this.getInventory().getViewers()) {
            int $21 = player.getWindowId(this.getInventory());
            if (windowId > 0) {
                ContainerSetDataPacket $22 = new ContainerSetDataPacket();
                pk.windowId = windowId;
                pk.property = ContainerSetDataPacket.PROPERTY_FURNACE_TICK_COUNT;
                pk.value = cookTime;
                player.dataPacket(pk);

                pk = new ContainerSetDataPacket();
                pk.windowId = windowId;
                pk.property = ContainerSetDataPacket.PROPERTY_FURNACE_LIT_TIME;
                pk.value = burnDuration;
                player.dataPacket(pk);
            }
        }

        return ret;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag $23 = super.getSpawnCompound()
                .putBoolean("isMovable", this.isMovable())
                .putShort("BurnDuration", burnDuration)
                .putShort("BurnTime", burnTime)
                .putShort("CookTime", cookTime)
                .putShort("StoredXpInt", (int) this.storedXP);
        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }
        return c;
    }
    /**
     * @deprecated 
     */
    

    public int getBurnTime() {
        return burnTime;
    }
    /**
     * @deprecated 
     */
    

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }
    /**
     * @deprecated 
     */
    

    public int getBurnDuration() {
        return burnDuration;
    }
    /**
     * @deprecated 
     */
    

    public void setBurnDuration(int burnDuration) {
        this.burnDuration = burnDuration;
    }
    /**
     * @deprecated 
     */
    

    public int getCookTime() {
        return cookTime;
    }
    /**
     * @deprecated 
     */
    

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }
    /**
     * @deprecated 
     */
    

    public int getMaxTime() {
        return maxTime;
    }
    /**
     * @deprecated 
     */
    

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }
    /**
     * @deprecated 
     */
    

    public float getStoredXP() {
        return storedXP;
    }
    /**
     * @deprecated 
     */
    

    public void setStoredXP(float storedXP) {
        this.storedXP = storedXP;
    }
    /**
     * @deprecated 
     */
    

    public short calculateXpDrop() {
        return (short) (Math.floor(this.storedXP) + (ThreadLocalRandom.current().nextFloat() < (this.storedXP % 1) ? 1 : 0));
    }

    @Override
    public Inventory getIngredientView() {
        return new InventorySlice(this.inventory, 0, 1);
    }

    @Override
    public Inventory getProductView() {
        return new InventorySlice(this.inventory, 2, 3);
    }
}
