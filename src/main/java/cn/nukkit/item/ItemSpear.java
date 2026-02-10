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
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * @author Buddelbubi
 * @author xRookieFight
 * @since 16/31/2025
 */
public abstract class ItemSpear extends ItemTool {

    @Getter
    public float minimumSpeed = 0.13f;
    public int minimumLungeFood = 6;
    public int baseLungeExhaust = 4;

    public ItemSpear(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public boolean isSpear() {
        return true;
    }

    public void onSpearStab(Player player, float movementSpeed) {
        if (!player.isItemCoolDownEnd(this.getIdentifier())) return;

        player.setItemCoolDown(20, this.getIdentifier());

        PlayerSpearStabEvent event = new PlayerSpearStabEvent(player, this, movementSpeed);
        player.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        applyLunge(player);

        if (movementSpeed < getMinimumSpeed() || !player.isSprinting()) {
            Sound missSound = getMissSound();
            if (missSound != null) {
                player.getLevel().addSound(player.getPosition(), missSound);
            }
            return;
        }

        Level level = player.getLevel();
        Location loc = player.getLocation();

        Vector3 eyePos = loc.add(0, player.getEyeHeight(), 0);
        Vector3 direction = player.getDirectionVector().normalize();

        double maxDistance = 5.0;
        double minDot = 0.866;
        double bestScore = -1;

        Entity target = null;

        AxisAlignedBB searchBox = player.getBoundingBox().grow(maxDistance, maxDistance, maxDistance);

        for (Entity entity : level.getNearbyEntities(searchBox, player)) {
            if (!(entity instanceof EntityLiving living) || !living.isAlive()) continue;

            Vector3 targetPos = entity.getPosition().add(0, living.getEyeHeight() * 0.5, 0);

            double distance = eyePos.distance(targetPos);
            if (distance > maxDistance) continue;

            Vector3 toEntity = targetPos.subtract(eyePos).normalize();
            double dot = direction.dot(toEntity);

            if (dot < minDot) continue;

            double score = dot - (distance / maxDistance) * 0.1;
            if (score <= bestScore) continue;

            bestScore = score;
            target = entity;
        }

        if (target != null) {
            EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(
                    player,
                    target,
                    EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                    getJabDamage()
            );

            target.attack(damageEvent);
            Sound hitSound = getHitSound();
            if (hitSound != null) {
                level.addSound(player.getPosition(), hitSound);
            }
        } else {
            Sound missSound = getMissSound();
            if (missSound != null) {
                level.addSound(player.getPosition(), missSound);
            }
        }
    }

    public float getJabDamage() {
        float damage = getAttackDamage();

        int level = getEnchantmentLevel(Enchantment.ID_LUNGE);
        damage += level * 1.5f;

        return damage;
    }

    public void applyLunge(Player player) {
        if(this.canLunge(player)){
            int lungeLevel = getEnchantmentLevel(Enchantment.ID_LUNGE);
            Vector3 dir = player.getDirectionVector();
            dir.y = 0;

            if (dir.lengthSquared() == 0) return;

            dir = dir.normalize().multiply(0.5 + (lungeLevel * 0.4));

            player.setMotion(player.getMotion().add(dir));
            player.getLevel().addSound(player.getPosition(), Sound.ITEM_SPEAR_LUNGE);
            player.getFoodData().exhaust(baseLungeExhaust * lungeLevel);
        }
    }

    public boolean canLunge(Player player) {
        int playerGamemode = player.getGamemode();
        int enchantmentLevel = getEnchantmentLevel(Enchantment.ID_LUNGE);

        if (player.isGliding() || player.isSwimming() || player.isInsideOfWater() || player.isTouchingWater()) {
            return false;
        }

        if ((playerGamemode == Player.SURVIVAL || playerGamemode == Player.ADVENTURE) && player.getFoodData().getFood() < minimumLungeFood) {
            return false;
        }
        return enchantmentLevel > 0;
    }

    @Override
    public int getUsingTicks() {
        return 72000;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        Sound useSound = getUseSound();
        if (useSound != null) {
            player.getLevel().addSound(player.getPosition(), useSound);
        }
        return true;
    }

    @Override
    public void whileUsing(Player player) {
        if (!player.isItemCoolDownEnd(this.getIdentifier())) return;

        player.setItemCoolDown(5, this.getIdentifier());
        float playerSpeed = player.getMovementSpeed();

        if (playerSpeed < minimumSpeed) {
            return;
        }

        Level level = player.getLevel();
        Vector3 dir = player.getDirectionVector().normalize().multiply(1.5);

        AxisAlignedBB hitBox = player.getBoundingBox()
                .grow(1.5, 1.0, 1.5)
                .offset(dir.x, dir.y, dir.z);

        float damage = getAttackDamage() * 1.5f;

        EntityLiving closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : level.getNearbyEntities(hitBox, player)) {
            if (!(entity instanceof EntityLiving living) || !living.isAlive()) {
                continue;
            }

            double dist = living.distanceSquared(player);

            if (dist < closestDistance) {
                closestDistance = dist;
                closest = living;
            }
        }

        if (closest != null) {
            float finalDamage = (float) (damage + (playerSpeed * 3.0));

            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                    player,
                    closest,
                    EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                    finalDamage
            );

            closest.attack(event);
            Sound hitSound = getHitSound();
            if (hitSound != null) {
                level.addSound(player.getPosition(), hitSound);
            }
        }
    }

    private @Nullable String getSoundPrefix() {
        return switch (this.getTier()) {
            case TIER_DIAMOND -> "item.diamond_spear";
            case TIER_GOLD -> "item.golden_spear";
            case TIER_COPPER -> "item.copper_spear";
            case TIER_IRON -> "item.iron_spear";
            case TIER_NETHERITE -> "item.netherite_spear";
            case TIER_WOODEN -> "item.wooden_spear";
            default -> null;
        };
    }

    private @Nullable Sound getHitSound() {
        return getSoundBySuffix(".attack_hit");
    }

    private @Nullable Sound getMissSound() {
        return getSoundBySuffix(".attack_miss");
    }

    private @Nullable Sound getUseSound() {
        return getSoundBySuffix(".use");
    }

    private @Nullable Sound getSoundBySuffix(String suffix) {
        String prefix = getSoundPrefix();
        if (prefix == null) return null;

        try {
            return Sound.valueOf(prefix + suffix);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
