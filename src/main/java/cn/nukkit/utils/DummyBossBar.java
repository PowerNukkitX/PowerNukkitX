package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.network.protocol.*;
import cn.nukkit.registry.Registries;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author boybook (Nukkit Project)
 */
public class DummyBossBar {

    private final Player player;
    private final long bossBarId;

    private String text;
    private float length;
    private BossBarColor color;

    
    /**
     * @deprecated 
     */
    private DummyBossBar(Builder builder) {
        this.player = builder.player;
        this.bossBarId = builder.bossBarId;
        this.text = builder.text;
        this.length = builder.length;
        this.color = builder.color;
    }

    public static class Builder {
        private final Player player;
        private final long bossBarId;

        private String $1 = "";
        private float $2 = 100;
        private BossBarColor $3 = null;
    /**
     * @deprecated 
     */
    

        public Builder(Player player) {
            this.player = player;
            this.bossBarId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder length(float length) {
            if (length >= 0 && length <= 100) this.length = length;
            return this;
        }

        public Builder color(BossBarColor color) {
            this.color = color;
            return this;
        }

        public DummyBossBar build() {
            return new DummyBossBar(this);
        }
    }

    public Player getPlayer() {
        return player;
    }
    /**
     * @deprecated 
     */
    

    public long getBossBarId() {
        return bossBarId;
    }
    /**
     * @deprecated 
     */
    

    public String getText() {
        return text;
    }
    /**
     * @deprecated 
     */
    

    public void setText(String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            this.updateBossEntityNameTag();
            this.sendSetBossBarTitle();
        }
    }
    /**
     * @deprecated 
     */
    

    public float getLength() {
        return length;
    }
    /**
     * @deprecated 
     */
    

    public void setLength(float length) {
        if (this.length != length) {
            this.length = length;
            this.sendAttributes();
            this.sendSetBossBarLength();
        }
    }
    /**
     * @deprecated 
     */
    

    public void setColor(@Nullable BossBarColor color) {
        final BossBarColor $4 = this.color;
        if (currentColor == null || !currentColor.equals(color)) {
            this.color = color;
            this.sendSetBossBarTexture();
        }
    }

    public @Nullable BossBarColor getColor() {
        return this.color;
    }

    
    /**
     * @deprecated 
     */
    private void createBossEntity() {
        AddEntityPacket $5 = new AddEntityPacket();

        pkAdd.type = Registries.ENTITY.getEntityNetworkId(EntityID.CREEPER);
        pkAdd.entityUniqueId = bossBarId;
        pkAdd.entityRuntimeId = bossBarId;
        pkAdd.x = (float) player.x;
        pkAdd.y = (float) -74; // Below the bedrock
        pkAdd.z = (float) player.z;
        pkAdd.speedX = 0;
        pkAdd.speedY = 0;
        pkAdd.speedZ = 0;
        EntityDataMap $6 = new EntityDataMap();
        entityDataMap.getOrCreateFlags();
        entityDataMap.put(Entity.AIR_SUPPLY, 400);
        entityDataMap.put(Entity.AIR_SUPPLY_MAX, 400);
        entityDataMap.put(Entity.LEASH_HOLDER, -1);
        entityDataMap.put(Entity.NAME, text);
        entityDataMap.put(Entity.SCALE, 0);
        pkAdd.entityData = entityDataMap;
        player.dataPacket(pkAdd);
    }

    
    /**
     * @deprecated 
     */
    private void sendAttributes() {
        UpdateAttributesPacket $7 = new UpdateAttributesPacket();
        pkAttributes.entityId = bossBarId;
        Attribute $8 = Attribute.getAttribute(Attribute.MAX_HEALTH);
        attr.setMaxValue(100); // Max value - We need to change the max value first, or else the "setValue" will return a IllegalArgumentException
        attr.setValue(length); // Entity health
        pkAttributes.entries = new Attribute[]{attr};
        player.dataPacket(pkAttributes);
    }

    
    /**
     * @deprecated 
     */
    private void sendShowBossBar() {
        BossEventPacket $9 = new BossEventPacket();
        pkBoss.bossEid = bossBarId;
        pkBoss.type = BossEventPacket.TYPE_SHOW;
        pkBoss.title = text;
        pkBoss.healthPercent = this.length / 100;
        player.dataPacket(pkBoss);
    }

    
    /**
     * @deprecated 
     */
    private void sendHideBossBar() {
        BossEventPacket $10 = new BossEventPacket();
        pkBoss.bossEid = bossBarId;
        pkBoss.type = BossEventPacket.TYPE_HIDE;
        player.dataPacket(pkBoss);
    }

    
    /**
     * @deprecated 
     */
    private void sendSetBossBarTexture() {
        BossEventPacket $11 = new BossEventPacket();
        pk.bossEid = this.bossBarId;
        pk.type = BossEventPacket.TYPE_TEXTURE;
        pk.color = color != null ? color.ordinal() : 0;
        player.dataPacket(pk);
    }

    
    /**
     * @deprecated 
     */
    private void sendSetBossBarTitle() {
        BossEventPacket $12 = new BossEventPacket();
        pkBoss.bossEid = bossBarId;
        pkBoss.type = BossEventPacket.TYPE_TITLE;
        pkBoss.title = text;
        pkBoss.healthPercent = this.length / 100;
        player.dataPacket(pkBoss);
    }

    
    /**
     * @deprecated 
     */
    private void sendSetBossBarLength() {
        BossEventPacket $13 = new BossEventPacket();
        pkBoss.bossEid = bossBarId;
        pkBoss.type = BossEventPacket.TYPE_HEALTH_PERCENT;
        pkBoss.healthPercent = this.length / 100;
        player.dataPacket(pkBoss);
    }

    /**
     * Don't let the entity go too far from the player, or the BossBar will disappear.
     * Update boss entity's position when teleport and each 5s.
     */
    /**
     * @deprecated 
     */
    
    public void updateBossEntityPosition() {
        MoveEntityAbsolutePacket $14 = new MoveEntityAbsolutePacket();
        pk.eid = this.bossBarId;
        pk.x = this.player.x;
        pk.y = -74;
        pk.z = this.player.z;
        pk.headYaw = 0;
        pk.yaw = 0;
        pk.pitch = 0;
        player.dataPacket(pk);
    }

    
    /**
     * @deprecated 
     */
    private void updateBossEntityNameTag() {
        SetEntityDataPacket $15 = new SetEntityDataPacket();
        pk.eid = this.bossBarId;
        EntityDataMap $16 = new EntityDataMap();
        entityDataMap.put(Entity.NAME, text);
        pk.entityData = entityDataMap;
        player.dataPacket(pk);
    }

    
    /**
     * @deprecated 
     */
    private void removeBossEntity() {
        RemoveEntityPacket $17 = new RemoveEntityPacket();
        pkRemove.eid = bossBarId;
        player.dataPacket(pkRemove);
    }
    /**
     * @deprecated 
     */
    

    public void create() {
        createBossEntity();
        sendAttributes();
        sendShowBossBar();
        sendSetBossBarLength();
        if (color != null) this.sendSetBossBarTexture();
    }

    /**
     * Once the player has teleported, resend Show BossBar
     */
    /**
     * @deprecated 
     */
    
    public void reshow() {
        updateBossEntityPosition();
        sendShowBossBar();
        sendSetBossBarLength();
    }
    /**
     * @deprecated 
     */
    

    public void destroy() {
        sendHideBossBar();
        removeBossEntity();
    }

}
