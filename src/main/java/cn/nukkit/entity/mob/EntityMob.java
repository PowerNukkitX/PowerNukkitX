package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.inventory.EntityArmorInventory;
import cn.nukkit.inventory.EntityEquipmentInventory;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import lombok.Getter;

import java.util.Collection;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public abstract class EntityMob extends EntityIntelligent implements EntityInventoryHolder {

    private static final String TAG_MAINHAND = "Mainhand";
    private static final String TAG_OFFHAND = "Offhand";
    private static final String TAG_ARMOR = "Armor";

    @Getter
    private EntityEquipmentInventory equipmentInventory;

    @Getter
    private EntityArmorInventory armorInventory;

    public EntityMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.equipmentInventory = new EntityEquipmentInventory(this);
        this.armorInventory = new EntityArmorInventory(this);

        if (this.namedTag.contains(TAG_MAINHAND)) {
            this.equipmentInventory.setItemInHand(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_MAINHAND)), true);
        }

        if (this.namedTag.contains(TAG_OFFHAND)) {
            this.equipmentInventory.setItemInOffhand(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_OFFHAND)), true);
        }

        if (this.namedTag.contains(TAG_ARMOR)) {
            ListTag<CompoundTag> armorList = this.namedTag.getList(TAG_ARMOR, CompoundTag.class);
            for (CompoundTag armorTag : armorList.getAll()) {
                this.armorInventory.setItem(armorTag.getByte("Slot"), NBTIO.getItemHelper(armorTag));
            }
        }
    }

    public void spawnToAll() {
        if (this.chunk != null && !this.closed) {
            Collection<Player> chunkPlayers = this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values();
            for (Player chunkPlayer : chunkPlayers) {
                this.spawnTo(chunkPlayer);
            }
        }
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        this.equipmentInventory.sendContents(player);
        this.armorInventory.sendContents(player);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.put(TAG_MAINHAND, NBTIO.putItemHelper(this.equipmentInventory.getItemInHand()));
        this.namedTag.put(TAG_OFFHAND, NBTIO.putItemHelper(this.equipmentInventory.getItemInOffhand()));

        if (this.armorInventory != null) {
            ListTag<CompoundTag> armorTag = new ListTag<>(TAG_ARMOR);
            for (int i = 0; i < 4; i++) {
                armorTag.add(NBTIO.putItemHelper(this.armorInventory.getItem(i), i));
            }
            this.namedTag.putList(armorTag);
        }
    }

    @Override
    public Inventory getInventory() {
        return this.armorInventory;
    }

    @Override
    public boolean canEquipByDispenser() {
        return true;
    }

}
