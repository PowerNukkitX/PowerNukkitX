package cn.nukkit.item;

import java.util.ArrayList;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityQueryOptions;
import cn.nukkit.entity.passive.EntityHappyGhast;
import cn.nukkit.level.Dimension;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemSnowball extends ProjectileItem {

    public ItemSnowball() {
        this(0, 1);
    }

    public ItemSnowball(Integer meta) {
        this(meta, 1);
    }

    public ItemSnowball(Integer meta, int count) {
        super(SNOWBALL, 0, count, "Snowball");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public String getProjectileEntityType() {
        return SNOWBALL;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }

    @Override
    public boolean useOn(Entity entity) {
        return entity instanceof EntityHappyGhast;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (isInteractingWithHappyGhast(player, 8)) {
            if (!player.isCreative()) player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            return false;
        }
        return super.onClickAir(player, directionVector);
    }

    @Override
    protected Entity correctProjectile(Player player, Entity projectile) {
        if (isInteractingWithHappyGhast(player, 8)) {
            return null;
        }
        return projectile;
    }

    public boolean isInteractingWithHappyGhast(Player player, double maxDistance) {
        Dimension level = player.getLevel();
        Vector3 start = player.add(0, player.getEyeHeight(), 0);
        Vector3 dir   = player.getDirectionVector().normalize();
        Vector3 end   = start.add(dir.multiply(maxDistance));

        List<Entity> buffer = new ArrayList<>();
        EntityQueryOptions opts = new EntityQueryOptions()
                .location(start)
                .maxDistance(maxDistance);

        level.getEntities(opts, buffer);

        Entity closest = null;
        double best = Double.MAX_VALUE;

        for (Entity e : buffer) {
            if (e == player) continue;
            if (!(e instanceof EntityHappyGhast)) continue;

            AxisAlignedBB box = e.getBoundingBox();
            if (box == null) continue;

            // expand a little to make aiming detection more consistent
            AxisAlignedBB grown = box.grow(0.3, 0.3, 0.3);

            if (grown.calculateIntercept(start, end) != null) {
                double d2 = e.distanceSquared(start);
                if (d2 < best) {
                    best = d2;
                    closest = e;
                }
            }
        }

        if (closest == null) return false;
        return closest instanceof EntityHappyGhast;
    }
}
