package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.components.RideableComponent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.MinecartType;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * @author Snake1999
 * @since 2016/1/30
 */
// TODO: physics, movement and speed still do not match BDS
public class EntityMinecart extends EntityMinecartAbstract {

    @Override
    @NotNull public String getIdentifier() {
        return MINECART;
    }

    public EntityMinecart(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return getType().getName();
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("minecart", "inanimate");
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(0);
    }

    @Override
    public String getInteractButtonText(Player player) {
        return "action.interact.ride.minecart";
    }

    @Override
    public @Nullable RideableComponent getComponentRideable() {
        return new RideableComponent(
            0,
            true,
            RideableComponent.DismountMode.DEFAULT,
            Set.of(),
            "action.interact.ride.minecart",
            1.375f,
            true,
            false,
            1,
            List.of(
                new RideableComponent.Seat(0, 1, new Vector3f(0.0f, -0.2f, 0.0f), null, null, null, null)
            )
        );
    }

    @Override
    public Vector3f getMountedOffset(Entity passenger) {
        Vector3f base = super.getMountedOffset(passenger);

        if (isOnRailForMountOffset()) {
            return new Vector3f(base.x, base.y - 0.5f, base.z);
        }

        return base;
    }

    @Override
    protected void activate(int x, int y, int z, boolean flag) {
        if (flag && this.getHealthCurrent() > 15
                && this.attack(new EntityDamageByBlockEvent(this.level.getBlock(x, y, z), this, DamageCause.CONTACT, 1))
                && !this.passengers.isEmpty()) {
            this.dismountEntity(this.getPassenger());
        }
    }

    @Override
    public boolean onRiderInput(Player rider, PlayerAuthInputPacket pk) {
        double inputY = pk.getMoveVector().getY();
        if (inputY >= -1.001 && inputY <= 1.001) {
            this.setCurrentSpeed(inputY);
        }

        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean update = super.onUpdate(currentTick);

        if (this.passengers.isEmpty()) {
            for (Entity entity : this.level.getCollidingEntities(this.boundingBox.grow(0.20000000298023224, 0.0D, 0.20000000298023224), this)) {
                if (entity.riding != null || !(entity instanceof EntityLiving) || entity instanceof Player || entity instanceof EntitySwimmable) {
                    continue;
                }

                this.mountEntity(entity, false);
                update = true;
                break;
            }
        }

        return update;
    }
}
