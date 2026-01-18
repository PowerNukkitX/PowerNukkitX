package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerSpearStabEvent;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginManager;
import lombok.Getter;

public abstract class ItemSpear extends ItemTool {

    @Getter
    public float minimumSpeed = 0.13f;

    public ItemSpear(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public boolean isSpear() {
        return true;
    }

    public void onSpearStab(Player player, float movementSpeed) {
        if (!player.isItemCoolDownEnd(this.getIdentifier())) {
            return;
        }
        player.setItemCoolDown(20, this.getIdentifier());

        PlayerSpearStabEvent event = new PlayerSpearStabEvent(player, this, movementSpeed);
        PluginManager pluginManager = player.getServer().getPluginManager();

        pluginManager.callEvent(event);

        if (!event.isCancelled()){
            if (movementSpeed >= getMinimumSpeed() && player.isSprinting()) {
                Level level = player.getLevel();
                Location playerLoc = player.getLocation();
                Vector3 direction = player.getDirectionVector().normalize();

                double maxDistance = 5.0;
                double minDot = 0.866;
                Entity targetEntity = null;

                AxisAlignedBB bb = player.getBoundingBox().grow(maxDistance, maxDistance, maxDistance);

                Vector3 eyePos = playerLoc.add(0, player.getEyeHeight(), 0);

                for (Entity entity : level.getNearbyEntities(bb, player)) {
                    if (!(entity instanceof EntityLiving living) || !living.isAlive()) continue;

                    Vector3 targetPos = entity.getPosition().add(0, entity.getEyeHeight() / 2, 0);
                    Vector3 toEntity = targetPos.subtract(eyePos).normalize();

                    double dot = direction.dot(toEntity);
                    if (dot < minDot) continue;

                    targetEntity = entity;
                }

                if (targetEntity != null) {
                    EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(
                            player,
                            targetEntity,
                            EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                            getJabDamage()
                    );

                    targetEntity.attack(damageEvent);
                    level.addSound(player.getPosition(), Sound.ITEM_SPEAR_ATTACK_HIT);
                } else level.addSound(player.getPosition(), Sound.ITEM_SPEAR_ATTACK_MISS);
            } else player.getLevel().addSound(player.getPosition(), Sound.ITEM_SPEAR_ATTACK_MISS);
        }
    }

    public float getChargeDamage(boolean fullCharge) {
        float damage = getAttackDamage();

        if (fullCharge) {
            damage *= 0.5f;
        }

        return damage;
    }

    public float getJabDamage() {
        float damage = getAttackDamage();

        int level = getEnchantmentLevel(Enchantment.ID_LUNGE);
        damage += level * 1.5f;

        return damage;
    }

    public void applyLunge(Player player) {
        int level = getEnchantmentLevel(Enchantment.ID_LUNGE);

        if (level > 0) {
            Vector3 dir = player.getDirectionVector().multiply(0.8 + (level * 0.4));
            player.setMotion(player.getMotion().add(dir));

            player.getFoodData().setFood(Math.max(0, player.getFoodData().getFood() - level));
        }
    }
}
