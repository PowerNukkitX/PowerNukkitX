package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nonnull;

/**
 * 实体生物
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements EntityNameable only in PowerNukkit")
public abstract class EntityCreature extends EntityLiving implements EntityNameable, EntityAgeable {

    //以下属性涉及到对应的语义接口
    //若未实现相应接口，对应属性可忽略
    boolean sitting = false;
    String ownerName;
    Player owner;

    public EntityCreature(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        //语义接口逻辑
        if (this instanceof EntityCanSit && this.namedTag.contains("Sitting") && this.namedTag.getBoolean("Sitting")) {
            this.sitting = true;
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SITTING, true);
        }

        if (this instanceof EntityTamable && this.namedTag.contains("OwnerName")) {
            this.ownerName = this.namedTag.getString("OwnerName");
            this.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_TAMED, true);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        //语义接口逻辑
        if (this instanceof EntityCanSit) this.namedTag.putBoolean("Sitting", sitting);
        if (this instanceof EntityTamable && this.ownerName != null) this.namedTag.putString("OwnerName", this.ownerName);
    }

    // Armor stands, when implemented, should also check this.
    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.NAME_TAG && !player.isAdventure()) {
            return applyNameTag(player, item);
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public final boolean playerApplyNameTag(@Nonnull Player player, @Nonnull Item item) {
        return applyNameTag(player, item);
    }

    // Structured like this so I can override nametags in player and dragon classes
    // without overriding onInteract.
    protected boolean applyNameTag(Player player, Item item) {
        if (item.hasCustomName()) {
            this.setNameTag(item.getCustomName());
            this.setNameTagVisible(true);

            if (!player.isCreative()) {
                player.getInventory().removeItem(item);
            }
            // Set entity as persistent.
            return true;
        }
        return false;
    }
}
