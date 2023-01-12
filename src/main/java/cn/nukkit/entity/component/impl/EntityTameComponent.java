package cn.nukkit.entity.component.impl;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.component.AbstractEntityComponent;
import cn.nukkit.entity.data.LongEntityData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class EntityTameComponent extends AbstractEntityComponent {

    protected String ownerName;
    protected Player owner;

    public EntityTameComponent(Entity entity) {
        super(entity);
    }

    @Override
    public void onInitEntity() {
        if (entity.namedTag.contains("OwnerName")) {
            this.ownerName = entity.namedTag.getString("OwnerName");
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_TAMED, true);
        }
    }

    @Override
    public void onSaveNBT() {
        if (this.ownerName != null)
            entity.namedTag.putString("OwnerName", this.ownerName);
    }

    public boolean hasOwner() {
        return hasOwner(true);
    }

    /**
     * @param checkOnline 是否要求主人在线
     * @return 有没有主人
     */
    public boolean hasOwner(boolean checkOnline) {
        if (checkOnline) {
            return getOwner() != null;
        } else {
            return ownerName != null;
        }
    }

    /**
     * @return 这个实体主人的名字<br>The name of the owner of this entity
     */
    public String getOwnerName() {
        return this.ownerName;
    }

    /**
     * 设置这个实体主人的名字,相当于设置这个实体的主人<br>The name of the owner of this entity,Equivalent to set the owner of this entity.
     */
    public void setOwnerName(@Nonnull String playerName) {
        if (playerName.isEmpty()) throw new IllegalArgumentException("Owner's name cannot be empty!");
        this.ownerName = playerName;
        var player = entity.getServer().getPlayerExact(playerName);
        if (player == null) return;
        this.owner = player;
        entity.setDataProperty(new LongEntityData(Entity.DATA_OWNER_EID, player.getId()));
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_TAMED, true);
    }

    /**
     * @return 获得这个实体的主人Player实例<br>Get the instance that the owner of entity.
     */
    @Nullable
    public Player getOwner() {
        if (this.owner != null) {
            if (this.owner.isOnline()) return this.owner;
            else this.owner = null;
        }
        if (this.ownerName == null || this.ownerName.isEmpty()) return null;
        var player = entity.getServer().getPlayerExact(this.ownerName);
        if (player == null) return null;
        this.owner = player;
        entity.setDataProperty(new LongEntityData(Entity.DATA_OWNER_EID, player.getId()));
        return this.owner;
    }
}
