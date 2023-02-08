package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockComposter;
import cn.nukkit.block.BlockHopper;
import cn.nukkit.block.BlockRailActivator;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.HopperSearchItemEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.MinecartHopperInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.*;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.MinecartType;

public class EntityMinecartHopper extends EntityMinecartAbstract implements InventoryHolder, BlockHopper.IHopper {

    public static final int NETWORK_ID = 96;
    private final BlockVector3 temporalVector = new BlockVector3();
    public int transferCooldown;

    protected MinecartHopperInventory inventory;
    private boolean disabled;
    private AxisAlignedBB pickupArea;

    public EntityMinecartHopper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setDisplayBlock(Block.get(Block.HOPPER_BLOCK), false);
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    @Override
    public boolean onUpdate(int currentTick) {
        if (!super.onUpdate(currentTick)) return false;

        if (isOnTransferCooldown()) {
            this.transferCooldown--;
            return true;
        }

        checkDisabled();

        if (isDisabled()) {
            return false;
        }

        HopperSearchItemEvent event = new HopperSearchItemEvent(this, true);
        this.server.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        this.updatePickupArea();

        Block blockSide = this.getSide(BlockFace.UP).getTickCachedLevelBlock();
        BlockEntity blockEntity = this.level.getBlockEntity(temporalVector.setComponentsAdding(this, BlockFace.UP));

        boolean changed;

        if (blockEntity instanceof InventoryHolder || blockSide instanceof BlockComposter) {
            //从容器中拉取物品
            changed = pullItems(this, this);
        } else {
            //收集掉落物
            changed = pickupItems(this, this, pickupArea);
        }

        if (changed) {
            this.setTransferCooldown(1);
        }

        return true;
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return getType().getName();
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(5);
    }

    @Override
    public boolean isRideable() {
        return false;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void dropItem() {
        for (Item item : this.inventory.getContents().values()) {
            this.level.dropItem(this, item);
        }
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            Entity damager = entityDamageByEntityEvent.getDamager();
            if (damager instanceof Player player && player.isCreative()) {
                return;
            }
        }
        this.level.dropItem(this, Item.get(Item.HOPPER_MINECART));
    }

    @Override
    public void kill() {
        super.kill();
        this.inventory.clearAll();
    }

    @Override
    public boolean mountEntity(Entity entity, byte mode) {
        return false;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        player.addWindow(this.inventory);
        return false; // If true, the count of items player has in hand decreases
    }

    @Override
    public MinecartHopperInventory getInventory() {
        return inventory;
    }

    @Override
    public void initEntity() {
        this.inventory = new MinecartHopperInventory(this);
        if (this.namedTag.contains("Items") && this.namedTag.get("Items") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("Items", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                this.inventory.setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }

        this.dataProperties
                .putByte(DATA_CONTAINER_TYPE, 11)
                .putInt(DATA_CONTAINER_BASE_SIZE, this.inventory.getSize())
                .putInt(DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH, 0);

        this.updatePickupArea();

        this.scheduleUpdate();

        super.initEntity();

        checkDisabled();
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public void updatePickupArea() {
        this.pickupArea = new SimpleAxisAlignedBB(this.x - 0.5, this.y - 0.5, this.z - 0.5, this.x + 1, this.y + 2.5, this.z + 1).expand(0.25, 0, 0.25);
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public void checkDisabled() {
        if (getLevelBlock() instanceof BlockRailActivator rail) {
            setDisabled(rail.isActive());
        }
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public boolean isDisabled() {
        return disabled;
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        if (this.inventory != null) {
            for (int slot = 0; slot < 5; ++slot) {
                Item item = this.inventory.getItem(slot);
                if (item != null && item.getId() != Item.AIR) {
                    this.namedTag.getList("Items", CompoundTag.class)
                            .add(NBTIO.putItemHelper(item, slot));
                }
            }
        }
    }
}
