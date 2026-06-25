package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataMap;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.MoveActorAbsoluteData;
import org.cloudburstmc.protocol.bedrock.data.payload.boss.BossBarOverlay;
import org.cloudburstmc.protocol.bedrock.data.payload.boss.BossEventUpdateType;
import org.cloudburstmc.protocol.bedrock.packet.AddActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.BossEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.MoveActorAbsolutePacket;
import org.cloudburstmc.protocol.bedrock.packet.RemoveActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetActorDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateAttributesPacket;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;

/**
 * @author boybook (Nukkit Project)
 */
@Getter
public class DummyBossBar {
    private static final BossBarColor DEFAULT_NETWORK_COLOR = BossBarColor.PINK;
    private static final BossBarOverlay DEFAULT_OVERLAY = BossBarOverlay.PROGRESS;
    private static final float ENTITY_Y = -74f;

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
        private float length = 100f;
        private BossBarColor color = null;

        public Builder(Player player) {
            this.player = player;
            // Unique ID in a safe runtime-ID range
            this.bossBarId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
        }

        public Builder text(String text) {
            this.text = text != null ? text : "";
            return this;
        }

        /**
         * @param length value in [0, 100]
         */
        public Builder length(float length) {
            if (length >= 0f && length <= 100f) this.length = length;
            return this;
        }

        public Builder color(@Nullable BossBarColor color) {
            this.color = color;
            return this;
        }

        public DummyBossBar build() {
            return new DummyBossBar(this);
        }
    }

    /**
     * Update the boss-bar title. No-ops if the text is unchanged.
     */
    public void setText(String text) {
        final String safe = text != null ? text : "";
        if (!this.text.equals(safe)) {
            this.text = safe;
            updateBossEntityNameTag();
            sendSetBossBarTitle();
        }
    }

    /**
     * Update the boss-bar fill percentage. No-ops if the value is unchanged.
     *
     * @param length value in [0, 100]
     */
    public void setLength(float length) {
        if (Float.compare(this.length, length) != 0) {
            this.length = length;
            sendAttributes();
            sendSetBossBarLength();
        }
    }

    public void setColor(@Nullable BossBarColor color) {
        final BossBarColor current = this.color;
        if (current != color && (current == null || !current.equals(color))) {
            this.color = color;
            sendSetBossBarTexture();
        }
    }

    @Nullable
    public BossBarColor getColor() {
        return color;
    }

    /**
     * Spawn the boss bar for the player. Call once after constructing.
     */
    public void create() {
        createBossEntity();
        sendAttributes();
        sendShowBossBar();
        sendSetBossBarLength();
        sendSetBossBarTexture();
    }

    /**
     * Re-display the boss bar after the player has teleported.
     * The dummy entity must stay close to the player or the bar disappears.
     */
    public void reshow() {
        updateBossEntityPosition();
        sendShowBossBar();
        sendSetBossBarLength();
        sendSetBossBarTexture();
    }

    /**
     * Remove the boss bar and the underlying dummy entity.
     */
    public void destroy() {
        sendHideBossBar();
        removeBossEntity();
    }

    /**
     * Keep the dummy entity near the player.
     * Call this on teleport and periodically (e.g. every 5 s).
     */
    public void updateBossEntityPosition() {
        final MoveActorAbsoluteData moveData = new MoveActorAbsoluteData();
        moveData.setActorRuntimeID(this.bossBarId);
        moveData.setPos(entityPos());
        moveData.setRotation(Vector3f.ZERO);
        moveData.setOnGround(false);
        moveData.setTeleported(false);
        moveData.setForceMove(false);

        final MoveActorAbsolutePacket packet = new MoveActorAbsolutePacket();
        packet.setMoveData(moveData);
        player.sendPacket(packet);
    }

    private void createBossEntity() {
        final ActorDataMap actorDataMap = new ActorDataMap();
        actorDataMap.put(ActorDataTypes.AIR_SUPPLY, (short) 400);
        actorDataMap.put(ActorDataTypes.AIR_SUPPLY_MAX, (short) 400);
        actorDataMap.put(ActorDataTypes.LEASH_HOLDER, -1L);
        actorDataMap.put(ActorDataTypes.NAME, this.text);
        actorDataMap.put(ActorDataTypes.NAMETAG_ALWAYS_SHOW, (byte) 0);
        actorDataMap.put(ActorDataTypes.SCALE, 0f);

        final AddActorPacket packet = new AddActorPacket();
        packet.setActorData(actorDataMap);
        packet.setTargetActorID(this.bossBarId);
        packet.setTargetRuntimeID(this.bossBarId);
        packet.setPosition(entityPos());
        packet.setActorType("minecraft:creeper");
        packet.setVelocity(Vector3f.ZERO);
        packet.setRotation(Vector2f.ZERO);
        player.sendPacket(packet);
    }

    private void sendAttributes() {
        final Attribute attr = Attribute.getAttribute(Attribute.HEALTH);
        attr.setMaxValue(100f);
        attr.setValue(Math.max(0f, Math.min(100f, this.length)));

        final UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.setRuntimeID(this.bossBarId);
        packet.getAttributeList().add(attr.toNetwork());
        player.sendPacket(packet);
    }

    private void sendShowBossBar() {
        final BossEventPacket packet = new BossEventPacket();
        packet.setTargetActorID(this.bossBarId);
        packet.setEventType(BossEventUpdateType.ADD);
        packet.setName(this.text);
        packet.setHealthPercent(this.length / 100f);
        packet.setOverlay(DEFAULT_OVERLAY);
        packet.setColor(networkColor());
        packet.setDarkenScreen(0);
        packet.setPlayerID(this.player.getId());
        player.sendPacket(packet);
    }

    private void sendHideBossBar() {
        final BossEventPacket packet = new BossEventPacket();
        packet.setTargetActorID(this.bossBarId);
        packet.setEventType(BossEventUpdateType.REMOVE);
        packet.setName("");
        packet.setHealthPercent(0f);
        packet.setOverlay(DEFAULT_OVERLAY);
        packet.setColor(networkColor());
        packet.setDarkenScreen(0);
        packet.setPlayerID(this.player.getId());
        player.sendPacket(packet);
    }

    private void sendSetBossBarTexture() {
        final BossEventPacket packet = new BossEventPacket();
        packet.setTargetActorID(this.bossBarId);
        packet.setEventType(BossEventUpdateType.UPDATE_STYLE);
        packet.setName(this.text);
        packet.setHealthPercent(this.length / 100f);
        packet.setOverlay(DEFAULT_OVERLAY);
        packet.setColor(networkColor());
        packet.setDarkenScreen(0);
        packet.setPlayerID(this.player.getId());
        player.sendPacket(packet);
    }

    private void sendSetBossBarTitle() {
        final BossEventPacket packet = new BossEventPacket();
        packet.setTargetActorID(this.bossBarId);
        packet.setEventType(BossEventUpdateType.UPDATE_NAME);
        packet.setName(this.text);
        packet.setHealthPercent(this.length / 100f);
        packet.setOverlay(DEFAULT_OVERLAY);
        packet.setColor(networkColor());
        packet.setDarkenScreen(0);
        packet.setPlayerID(this.player.getId());
        player.sendPacket(packet);
    }

    private void sendSetBossBarLength() {
        final BossEventPacket packet = new BossEventPacket();
        packet.setTargetActorID(this.bossBarId);
        packet.setEventType(BossEventUpdateType.UPDATE_PERCENT);
        packet.setName(this.text);
        packet.setHealthPercent(this.length / 100f);
        packet.setOverlay(DEFAULT_OVERLAY);
        packet.setColor(networkColor());
        packet.setDarkenScreen(0);
        packet.setPlayerID(this.player.getId());
        player.sendPacket(packet);
    }

    private void updateBossEntityNameTag() {
        final SetActorDataPacket packet = new SetActorDataPacket();
        packet.getActorData().put(ActorDataTypes.NAME, this.text);
        packet.setTargetRuntimeID(this.bossBarId);
        player.sendPacket(packet);
    }

    private void removeBossEntity() {
        final RemoveActorPacket packet = new RemoveActorPacket();
        packet.setTargetActorID(this.bossBarId);
        player.sendPacket(packet);
    }

    /** Resolve the current color to its network representation, never null. */
    private org.cloudburstmc.protocol.bedrock.data.payload.boss.BossBarColor networkColor() {
        return color != null ? color.toNetwork() : DEFAULT_NETWORK_COLOR.toNetwork();
    }

    /** Entity position: same X/Z as the player but far below so it stays invisible. */
    private Vector3f entityPos() {
        return Vector3f.from(this.player.getX(), ENTITY_Y, this.player.getZ());
    }
}