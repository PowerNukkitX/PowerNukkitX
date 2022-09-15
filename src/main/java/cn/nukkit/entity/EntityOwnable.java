package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;

import javax.annotation.Nullable;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
@Deprecated
@DeprecationDetails(since = "1.19.21-r5", reason = "统一接口定义", replaceWith = "replace to EntityTamable")
public interface EntityOwnable {
    /**
     * @return 这个实体主人的名字<br>The name of the owner of this entity
     */
    String getOwnerName();

    /**
     * 设置这个实体主人的名字,相当于设置这个实体的主人<br>The name of the owner of this entity,Equivalent to set the owner of this entity.
     */
    void setOwnerName(String playerName);

    /**
     * @return 获得这个实体的主人Player实例<br>Get the instance that the owner of entity.
     */
    @Nullable
    Player getOwner();
}
