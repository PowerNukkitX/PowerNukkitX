package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.inventory.EntityArmorInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.*;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author KeksDev
 */
public class EntityHappyGhast extends EntityAnimal implements EntityFlyable, EntityRideable, InventoryHolder {
    private EntityArmorInventory armorInventory;

    @Override
    @NotNull
    public String getIdentifier() {
        return HAPPY_GHAST;
    }

    public EntityHappyGhast(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
        setDataFlag(EntityFlag.COLLIDABLE, true); //allow standing on them
        setMovementSpeed(0.05f);
        this.armorInventory = new EntityArmorInventory(this);
        if (this.namedTag.contains("Armor")) {
            ListTag<CompoundTag> armorList = this.namedTag.getList("Armor", CompoundTag.class);
            for (CompoundTag armorTag : armorList.getAll()) {
                this.armorInventory.setItem(armorTag.getByte("Slot"), NBTIO.getItemHelper(armorTag));
            }
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (this.armorInventory != null) {
            ListTag<CompoundTag> armorTag = new ListTag<>();
            for (int i = 0; i < 5; i++) {
                armorTag.add(NBTIO.putItemHelper(this.armorInventory.getItem(i), i));
            }
            this.namedTag.putList("Armor", armorTag);
        }
    }

    @Override
    public float getWidth() {
        return isBaby() ? 0.95f : 4f;
    }

    @Override
    public float getHeight() {
        return isBaby() ? 0.95f : 4f;
    }

    @Override
    public String getOriginalName() {
        return "Happy Ghast";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{getHarness()};
    }

    @Override
    public Integer getExperienceDrops() {
        return Utils.rand(1, 3);
    }

    @Override
    public Inventory getInventory() {
        return armorInventory;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId().equals(Item.NAME_TAG) && !player.isAdventure()) {
            return applyNameTag(player, item);
        }

        if (item instanceof ItemHarness armor) {
            if (armorInventory.getBody().isNull()) {
                armorInventory.setBody(armor);
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                armorInventory.sendContents(player);
            }
            return true;
        }

        if(item instanceof ItemShears shears) {
            if(!armorInventory.getBody().isNull()) {
                player.getInventory().addItem(armorInventory.getBody());
                armorInventory.setBody(Item.AIR);
                armorInventory.sendContents(player);
            }
            return true;
        }

        return false;
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        armorInventory.sendContents(player);
    }

    public Item getHarness() {
        return armorInventory.getBody();
    }
}
