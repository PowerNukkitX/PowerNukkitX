package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.BossEventUpdateType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataMap;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.MoveActorAbsoluteData;
import org.cloudburstmc.protocol.bedrock.packet.AddActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.BossEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.MoveActorAbsolutePacket;
import org.cloudburstmc.protocol.bedrock.packet.RemoveActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetActorDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateAttributesPacket;

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
        final ActorDataMap actorDataMap = new ActorDataMap();
        actorDataMap.put(ActorDataTypes.AIR_SUPPLY, (short) 400);
        actorDataMap.put(ActorDataTypes.AIR_SUPPLY_MAX, (short) 400);
        actorDataMap.put(ActorDataTypes.LEASH_HOLDER, -1L);
        actorDataMap.put(ActorDataTypes.NAME, this.text);
        actorDataMap.put(ActorDataTypes.SCALE, 0f);

        final AddActorPacket packet = new AddActorPacket();
        packet.setActorData(actorDataMap);
        packet.setTargetActorID(this.bossBarId);
        packet.setTargetRuntimeID(this.bossBarId);
        packet.setPosition(Vector3f.from(this.player.getX(), -74, this.player.getZ()));
        packet.setActorType("minecraft:creeper");
        packet.setVelocity(Vector3f.ZERO);
        packet.setRotation(Vector2f.ZERO);
        this.player.sendPacket(packet);
    }

    private void sendAttributes() {
        final Attribute attr = Attribute.getAttribute(Attribute.HEALTH);
        attr.setMaxValue(100); // Max value - We need to change the max value first, or else the "setValue" will return a IllegalArgumentException
        attr.setValue(this.length); // Entity health
        final UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.setRuntimeID(this.bossBarId);
        packet.getAttributeList().add(attr.toNetwork());
        player.sendPacket(packet);
    }

    private void sendShowBossBar() {
        final BossEventPacket bossEventPacket = new BossEventPacket();
        bossEventPacket.setTargetActorID(this.bossBarId);
        bossEventPacket.setEventType(BossEventUpdateType.ADD);
        bossEventPacket.setName(this.text);
        bossEventPacket.setHealthPercent(this.length / 100);
        this.player.sendPacket(bossEventPacket);
    }

    private void sendHideBossBar() {
        final BossEventPacket bossEventPacket = new BossEventPacket();
        bossEventPacket.setTargetActorID(this.bossBarId);
        bossEventPacket.setEventType(BossEventUpdateType.REMOVE);
        this.player.sendPacket(bossEventPacket);
    }

    private void sendSetBossBarTexture() {
        final BossEventPacket bossEventPacket = new BossEventPacket();
        bossEventPacket.setTargetActorID(this.bossBarId);
        bossEventPacket.setEventType(BossEventUpdateType.UPDATE_STYLE);
        bossEventPacket.setColor(color != null ? color.ordinal() : 0);
        this.player.sendPacket(bossEventPacket);
    }

    private void sendSetBossBarTitle() {
        final BossEventPacket bossEventPacket = new BossEventPacket();
        bossEventPacket.setTargetActorID(this.bossBarId);
        bossEventPacket.setEventType(BossEventUpdateType.UPDATE_NAME);
        bossEventPacket.setName(this.text);
        bossEventPacket.setHealthPercent(this.length / 100);
        this.player.sendPacket(bossEventPacket);
    }

    private void sendSetBossBarLength() {
        final BossEventPacket bossEventPacket = new BossEventPacket();
        bossEventPacket.setTargetActorID(this.bossBarId);
        bossEventPacket.setEventType(BossEventUpdateType.UPDATE_PERCENT);
        bossEventPacket.setHealthPercent(this.length / 100);
        this.player.sendPacket(bossEventPacket);
    }

    /**
     * Don't let the entity go too far from the player, or the BossBar will disappear.
     * Update boss entity's position when teleport and each 5s.
     */
    public void updateBossEntityPosition() {
        final MoveActorAbsoluteData moveData = new MoveActorAbsoluteData();
        moveData.setPos(Vector3f.from(this.player.getX(), -74, this.player.getZ()));
        moveData.setRotation(Vector3f.ZERO);

        final MoveActorAbsolutePacket packet = new MoveActorAbsolutePacket();
        packet.setMoveData(moveData);

        this.player.sendPacket(packet);
    }

    private void updateBossEntityNameTag() {
        final SetActorDataPacket packet = new SetActorDataPacket();
        packet.getActorData().put(ActorDataTypes.NAME, this.text);
        packet.setTargetRuntimeID(this.bossBarId);
        this.player.sendPacket(packet);
    }

    private void removeBossEntity() {
        final RemoveActorPacket packet = new RemoveActorPacket();
        packet.setTargetActorID(this.bossBarId);
        this.player.sendPacket(packet);
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
