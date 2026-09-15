package org.powernukkitx.entity.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.components.RideableComponent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemCushion;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.DyeColor;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
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

    private DyeColor color = DyeColor.WHITE;
    private int supportCheckTicks;

    public EntityCushion(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.setHealthMax(1);
        this.setHealthCurrent(1);
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
        if (nbtMap.contains(TAG_COLOR)) {
            this.color = DyeColor.getByWoolData(nbtMap.getByte(TAG_COLOR));
        }

        this.setImmobile(true);
        this.setDataFlag(ActorFlags.COLLIDABLE);
        this.actorDataMap.put(ActorDataTypes.COLOR_INDEX, (byte) this.color.getWoolData());
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

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
        this.actorDataMap.put(ActorDataTypes.COLOR_INDEX, (byte) color.getWoolData());
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
                this.close();
                return false;
            }
        }

        return super.onUpdate(currentTick);
    }

    private boolean hasSupport() {
        Block below = this.level.getBlock(this.temporalVector.setComponents(
            this.getFloorX(), this.getFloorY() - 1, this.getFloorZ()));
        return below.isSolid();
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }

        super.kill();

        if (this.lastDamageCause instanceof EntityDamageByEntityEvent damageByEntity) {
            Entity damager = damageByEntity.getDamager();

            if (damager instanceof Player player && player.isCreative()) {
                return;
            }
        }

        if (this.level != null && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            this.level.dropItem(this, ItemCushion.of(this.color));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putByte(TAG_COLOR, this.color.getWoolData());
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
