package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.MinecartType;

/**
 * @author Snake1999
 * @since 2016/1/30
 */
public class EntityMinecartEmpty extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 84;

    public EntityMinecartEmpty(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return getType().getName();
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
    public boolean isRideable() {
        return true;
    }

    @Override
    protected void activate(int x, int y, int z, boolean flag) {
        if (flag && this.getHealth() > 15
                && this.attack(new EntityDamageByBlockEvent(this.level.getBlock(x, y, z), this, DamageCause.CONTACT, 1))
                && !this.passengers.isEmpty()) {
            this.dismountEntity(this.getPassenger());
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean update = super.onUpdate(currentTick);

        if (this.passengers.isEmpty()) {
            for (Entity entity : this.level.getCollidingEntities(this.boundingBox.grow(0.20000000298023224, 0.0D, 0.20000000298023224), this)) {
                if (entity.riding != null || !(entity instanceof EntityLiving) || entity instanceof Player || entity instanceof EntitySwimmable) {
                    continue;
                }

                this.mountEntity(entity);
                update = true;
                break;
            }
        }

        return update;
    }
}
