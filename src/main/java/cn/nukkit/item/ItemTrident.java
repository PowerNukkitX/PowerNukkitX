package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;

/**
 * @author PetteriM1
 */
public class ItemTrident extends ItemTool {

    public ItemTrident() {
        this(0, 1);
    }

    public ItemTrident(Integer meta) {
        this(meta, 1);
    }

    public ItemTrident(Integer meta, int count) {
        super(TRIDENT, meta, count, "Trident");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_TRIDENT;
    }

    @Override
    public int getAttackDamage() {
        return 9;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @Override
    public int getUsingTicks() {
        return 72000;
    }

    @Override
    @SuppressWarnings("java:S3516")
    public boolean onRelease(Player player, int ticksUsed) {
        if (this.hasEnchantment(Enchantment.ID_TRIDENT_RIPTIDE)) {
            return true;
        }

        this.useOn(player);

        final NbtMap nbt = NbtMap.builder()
                .putList("Pos", NbtType.DOUBLE, Arrays.asList(
                                player.x,
                                player.y + player.getEyeHeight(),
                                player.z
                        )
                ).putList("Motion", NbtType.DOUBLE, Arrays.asList(
                                -Math.sin(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI),
                                -Math.sin(player.pitch / 180 * Math.PI),
                                Math.cos(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI)
                        )
                ).putList("Rotation", NbtType.FLOAT, Arrays.asList(
                                (player.yaw > 180 ? 360 : 0) - (float) player.yaw,
                                (float) -player.pitch
                        )
                ).build();

        EntityThrownTrident trident = new EntityThrownTrident(player.chunk, nbt, player);
        trident.setItem(this);

        double p = (double) ticksUsed / 20;
        double f = Math.min((p * p + p * 2) / 3, 1) * 2.5;

        if (player.isCreative()) {
            trident.setPickupMode(EntityProjectile.PICKUP_CREATIVE);
        }

        trident.setFavoredSlot(player.getInventory().getHeldItemIndex());

        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, trident, f);

        if (f < 0.1 || ticksUsed < 5) {
            entityShootBowEvent.setCancelled();
        }

        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().close();
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            ProjectileLaunchEvent ev = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile(), player);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                entityShootBowEvent.getProjectile().close();
            } else {
                entityShootBowEvent.getProjectile().spawnToAll();
                player.getLevel().addSound(player, Sound.ITEM_TRIDENT_THROW);
                if (!player.isCreative()) {
                    this.count--;
                    player.getInventory().setItemInMainHand(this);
                }
            }
        }

        return true;
    }
}
