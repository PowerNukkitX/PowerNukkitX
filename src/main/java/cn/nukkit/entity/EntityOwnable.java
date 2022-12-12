package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.data.LongEntityData;

import javax.annotation.Nullable;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
@Deprecated
@DeprecationDetails(since = "1.19.30-r1", reason = "统一接口定义", replaceWith = "replace to EntityTamable")
public interface EntityOwnable {
    /**
     * @return 这个实体主人的名字<br>The name of the owner of this entity
     */
    default String getOwnerName() {
        return ((EntityCreature) this).ownerName;
    }

    /**
     * 设置这个实体主人的名字,相当于设置这个实体的主人<br>The name of the owner of this entity,Equivalent to set the owner of this entity.
     */
    default void setOwnerName(String playerName) {
        var entity = (EntityCreature) this;
        entity.ownerName = playerName;
        var player = entity.getServer().getPlayerExact(playerName);
        if (player == null) return;
        entity.owner = player;
        entity.setDataProperty(new LongEntityData(Entity.DATA_OWNER_EID, player.getId()));
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_TAMED, true);
    }

    /**
     * @return 获得这个实体的主人Player实例<br>Get the instance that the owner of entity.
     */
    @Nullable
    default Player getOwner() {
        var entity = (EntityCreature) this;
        if (entity.owner != null) return entity.owner;
        if (entity.ownerName == null) return null;
        var player = entity.getServer().getPlayerExact(entity.ownerName);
        if (player == null) return null;
        entity.owner = player;
        entity.setDataProperty(new LongEntityData(Entity.DATA_OWNER_EID, player.getId()));
        return entity.owner;
    }
}
