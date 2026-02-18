package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket;
import org.cloudburstmc.protocol.bedrock.packet.BossEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetEntityDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateAttributesPacket;
import cn.nukkit.registry.Registries;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.AttributeData;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;

import javax.annotation.Nullable;
import java.util.Collections;
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

        private String text = "";
        private float length = 100;
        private BossBarColor color = null;

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

    public long getBossBarId() {
        return bossBarId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            this.updateBossEntityNameTag();
            this.sendSetBossBarTitle();
        }
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        if (this.length != length) {
            this.length = length;
            this.sendAttributes();
            this.sendSetBossBarLength();
        }
    }

    public void setColor(@Nullable BossBarColor color) {
        final BossBarColor currentColor = this.color;
        if (currentColor == null || !currentColor.equals(color)) {
            this.color = color;
            this.sendSetBossBarTexture();
        }
    }

    public @Nullable BossBarColor getColor() {
        return this.color;
    }

    private void createBossEntity() {
        AddEntityPacket pkAdd = new AddEntityPacket();
        pkAdd.setEntityType(Registries.ENTITY.getEntityNetworkId(EntityID.CREEPER));
        pkAdd.setUniqueEntityId(bossBarId);
        pkAdd.setRuntimeEntityId(bossBarId);
        pkAdd.setPosition(Vector3f.from((float) player.x, -74f, (float) player.z));
        pkAdd.setMotion(Vector3f.ZERO);
        pkAdd.setRotation(Vector2f.ZERO);
        pkAdd.setHeadRotation(0f);
        pkAdd.setBodyRotation(0f);
        pkAdd.getMetadata().put(EntityDataTypes.NAME, text);
        pkAdd.getMetadata().put(EntityDataTypes.SCALE, 0f);
        player.dataPacket(pkAdd);
    }

    private void sendAttributes() {
        UpdateAttributesPacket pkAttributes = new UpdateAttributesPacket();
        pkAttributes.setRuntimeEntityId(bossBarId);
        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH);
        attr.setMaxValue(100); // Max value - We need to change the max value first, or else the "setValue" will return a IllegalArgumentException
        attr.setValue(length); // Entity health
        pkAttributes.getAttributes().add(new AttributeData(
                attr.getName(),
                attr.getMinValue(),
                attr.getMaxValue(),
                attr.getValue(),
                attr.getDefaultValue(),
                Collections.emptyList()
        ));
        player.dataPacket(pkAttributes);
    }

    private void sendShowBossBar() {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.setBossUniqueEntityId(bossBarId);
        pkBoss.setAction(BossEventPacket.Action.CREATE);
        pkBoss.setTitle(text);
        pkBoss.setHealthPercentage(this.length / 100);
        player.dataPacket(pkBoss);
    }

    private void sendHideBossBar() {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.setBossUniqueEntityId(bossBarId);
        pkBoss.setAction(BossEventPacket.Action.REMOVE);
        player.dataPacket(pkBoss);
    }

    private void sendSetBossBarTexture() {
        BossEventPacket pk = new BossEventPacket();
        pk.setBossUniqueEntityId(this.bossBarId);
        pk.setAction(BossEventPacket.Action.UPDATE_STYLE);
        pk.setColor(color != null ? color.ordinal() : 0);
        player.dataPacket(pk);
    }

    private void sendSetBossBarTitle() {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.setBossUniqueEntityId(bossBarId);
        pkBoss.setAction(BossEventPacket.Action.UPDATE_NAME);
        pkBoss.setTitle(text);
        pkBoss.setHealthPercentage(this.length / 100);
        player.dataPacket(pkBoss);
    }

    private void sendSetBossBarLength() {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.setBossUniqueEntityId(bossBarId);
        pkBoss.setAction(BossEventPacket.Action.UPDATE_PERCENTAGE);
        pkBoss.setHealthPercentage(this.length / 100);
        player.dataPacket(pkBoss);
    }

    /**
     * Don't let the entity go too far from the player, or the BossBar will disappear.
     * Update boss entity's position when teleport and each 5s.
     */
    public void updateBossEntityPosition() {
        MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
        pk.setRuntimeEntityId(this.bossBarId);
        pk.setPosition(Vector3f.from((float) this.player.x, -74f, (float) this.player.z));
        pk.setRotation(Vector3f.ZERO);
        player.dataPacket(pk);
    }

    private void updateBossEntityNameTag() {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.setRuntimeEntityId(this.bossBarId);
        pk.getMetadata().put(EntityDataTypes.NAME, text);
        player.dataPacket(pk);
    }

    private void removeBossEntity() {
        RemoveEntityPacket pkRemove = new RemoveEntityPacket();
        pkRemove.setUniqueEntityId(bossBarId);
        player.dataPacket(pkRemove);
    }

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
    public void reshow() {
        updateBossEntityPosition();
        sendShowBossBar();
        sendSetBossBarLength();
    }

    public void destroy() {
        sendHideBossBar();
        removeBossEntity();
    }

}
