package org.powernukkitx.entity.item;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.components.RideableComponent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemCushion;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.DyeColor;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * A dyed seat that a player can sit on. The client only knows it as an entity, there is no cushion block.
 */
public class EntityCushion extends EntityVehicle {
    private static final String TAG_COLOR = "Color";

    /**
     * Vanilla only revalidates the block under a cushion every few seconds instead of every tick.
     */
    private static final int SUPPORT_CHECK_PERIOD = 100;

    private int supportCheckTicks;

    public EntityCushion(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.setHealthMax(1);
        this.setHealthCurrent(1);
        this.setCollisionEnabled(false);
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return CUSHION;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        final CompoundTag nbtMap = this.getNbt();
        if (nbtMap.containsByte(TAG_COLOR)) {
            setColor(DyeColor.getByDyeData(nbtMap.getByte(TAG_COLOR)));
        }

        this.setDataFlag(ActorFlags.COLLIDABLE);
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    public float getWidth() {
        return 1.0f;
    }

    @Override
    protected float getDefaultGravity() {
        return 0f;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    protected int getColorCode() {
        return getDataProperty(ActorDataTypes.VARIANT, 0);
    }

    public DyeColor getColor() {
        return DyeColor.getByDyeData(getColorCode());
    }

    public void setColor(DyeColor color) {
        this.setDataProperty(ActorDataTypes.VARIANT, color.getDyeData());
    }

    @Override
    public @Nullable RideableComponent getComponentRideable() {
        return new RideableComponent(
            0,
            true,
            RideableComponent.DismountMode.DEFAULT,
            Set.of(),
            "action.interact.sit",
            1.375f,
            false,
            true,
            1,
            List.of(new RideableComponent.Seat(0, 1, new Vector3f(0f, 0.1f, 0f), null, null, null, null))
        );
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (!this.passengers.isEmpty()) {
            return false;
        }

        return mountEntity(player, true);
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (++this.supportCheckTicks >= SUPPORT_CHECK_PERIOD) {
            this.supportCheckTicks = 0;

            if (this.level != null && !hasSupport()) {
                this.kill();
                return false;
            }
        }

        return super.onUpdate(currentTick);
    }

    private boolean hasSupport() {
        return getLevel().getCollisionBlocks(
            this.boundingBox.shrink(0.01, 0.01, 0.01).offset(0, -0.02, 0),
            true,
            true
        ).length != 0;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        source.setDamage(1);
        return super.attack(source);
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }

        super.kill();
        level.getVibrationManager().callVibrationEvent(new VibrationEvent(null, this.getVector3(), VibrationType.ENTITY_DIE));

        boolean drop = this.level != null && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS);

        if (this.lastDamageCause instanceof EntityDamageByEntityEvent damageByEntity) {
            Entity damager = damageByEntity.getDamager();
            if (damager instanceof Player player && player.isCreative()) {
                drop = false;
            }
        }

        if(drop && this.level != null) {
            this.level.dropItem(this, ItemCushion.of(getColor()));
        }
        this.close();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putByte(TAG_COLOR, getColorCode());
    }

    @Override
    public String getOriginalName() {
        return "Cushion";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("cushion", "inanimate");
    }
}
