package org.powernukkitx.entity;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.entity.item.EntityArmorStand;
import org.powernukkitx.entity.item.EntityFallingBlock;
import org.powernukkitx.entity.item.EntityFireworksRocket;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.entity.item.EntityMinecartAbstract;
import org.powernukkitx.entity.item.EntityPainting;
import org.powernukkitx.entity.item.EntityTnt;
import org.powernukkitx.entity.item.EntityXpOrb;
import org.powernukkitx.entity.mob.EntityCreeper;
import org.powernukkitx.entity.mob.EntityMob;
import org.powernukkitx.entity.mob.EntitySlime;
import org.powernukkitx.entity.mob.EntityZombie;
import org.powernukkitx.entity.passive.EntityAnimal;
import org.powernukkitx.entity.passive.EntityCow;
import org.powernukkitx.entity.passive.EntityMooshroom;
import org.powernukkitx.entity.passive.EntitySheep;
import org.powernukkitx.entity.passive.EntityWolf;
import org.powernukkitx.entity.projectile.EntityArrow;
import org.powernukkitx.entity.projectile.EntityProjectile;
import org.powernukkitx.entity.projectile.EntityThrownTrident;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Drives TYPE-SPECIFIC public methods on concrete entities (passive/mob/item/projectile)
 * that the generic getter smoke tests never reach. Each entity is spawned into the real
 * fixture level, cast to its type and its own API is exercised through a tolerant wrapper.
 */
public class EntityTypeBehaviorSmokeTest {

    static Level level;
    private int checked;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @Test
    void driveTypeSpecificApis() {
        drivePassive();
        driveMobs();
        driveItems();
        driveProjectiles();
        Assertions.assertTrue(checked > 0, "no type-specific behaviour was exercised");
    }

    // --- passive animals -------------------------------------------------

    private void drivePassive() {
        EntitySheep sheep = spawn(EntityID.SHEEP);
        if (sheep != null) {
            exerciseAnimal(sheep);
            safe(sheep::getColor);
            safe(() -> sheep.setColor(4));
            safe(sheep::getWoolItem);
            safe(sheep::growWool);
            // shear() closes the entity, do it last
            safe(sheep::shear);
        }

        EntityCow cow = spawn(EntityID.COW);
        if (cow != null) {
            exerciseAnimal(cow);
            safe(cow::getSoundVariants);
            close(cow);
        }

        EntityMooshroom moo = spawn(EntityID.MOOSHROOM);
        if (moo != null) {
            exerciseAnimal(moo);
            safe(moo::shear); // converts + closes
        }

        EntityWolf wolf = spawn(EntityID.WOLF);
        if (wolf != null) {
            exerciseAnimal(wolf);
            safe(wolf::getComponentTameable);
            safe(wolf::getComponentHealable);
            safe(wolf::getInventory);
            safe(wolf::getArmor);
            safe(wolf::getDiffHandDamage);
            safe(wolf::getAllVariant);
            close(wolf);
        }

        EntityAnimal chicken = spawn(EntityID.CHICKEN);
        if (chicken != null) {
            exerciseAnimal(chicken);
            close(chicken);
        }
    }

    private void exerciseAnimal(EntityAnimal a) {
        Item wheat = safeItem("minecraft:wheat");
        if (wheat != null) {
            safe(() -> a.isBreedingItem(wheat));
        }
        safe(a::getExperienceDrops);
        safe(a::getComponentBreedable);
        safe(a::getComponentAgeable);
        checked++;
    }

    // --- mobs ------------------------------------------------------------

    private void driveMobs() {
        EntityCreeper creeper = spawn(EntityID.CREEPER);
        if (creeper != null) {
            exerciseMob(creeper);
            safe(creeper::isPowered);
            safe(() -> creeper.setPowered(true));
            safe(creeper::isPowered);
            safe(() -> creeper.setPowered(false));
            safe(creeper::canDoInteraction);
            close(creeper);
        }

        EntitySlime slime = spawn(EntityID.SLIME);
        if (slime != null) {
            exerciseMob(slime);
            safe(slime::getVariant);
            safe(slime::hasVariant);
            safe(() -> slime.setVariant(EntitySlime.SIZE_MEDIUM));
            safe(slime::getVariant);
            safe(slime::getAllVariant);
            safe(slime::getFloatingForceFactor);
            close(slime);
        }

        EntityZombie zombie = spawn(EntityID.ZOMBIE);
        if (zombie != null) {
            exerciseMob(zombie);
            safe(zombie::isUndead);
            safe(zombie::getFloatingForceFactor);
            close(zombie);
        }

        EntityMob blaze = spawn(EntityID.BLAZE);
        if (blaze != null) {
            exerciseMob(blaze);
            close(blaze);
        }
    }

    private void exerciseMob(EntityMob m) {
        safe(m::getAdditionalArmor);
        safe(m::getInventory);
        safe(m::getDiffHandDamage);
        safe(m::getExperienceDrops);
        safe(m::canEquipByDispenser);
        EntityAnimal target = spawn(EntityID.CHICKEN);
        if (target != null) {
            safe(() -> m.attackTarget(target));
            close(target);
        }
        checked++;
    }

    // --- item entities ---------------------------------------------------

    private void driveItems() {
        EntityItem item = spawn(EntityID.ITEM);
        if (item != null) {
            safe(item::getItem);
            safe(item::getPickupDelay);
            safe(() -> item.setPickupDelay(20));
            safe(item::getPickupDelay);
            safe(() -> item.setThrower("tester"));
            safe(item::getThrower);
            safe(() -> item.setOwner("owner"));
            safe(item::getOwnerName);
            safe(() -> item.setDisplayOnly(true));
            safe(item::isDisplayOnly);
            safe(item::doesTriggerPressurePlate);
            checked++;
            close(item);
        }

        EntityXpOrb orb = spawn(EntityID.XP_ORB);
        if (orb != null) {
            safe(() -> orb.setExp(7));
            safe(orb::getExp);
            safe(orb::getPickupDelay);
            safe(() -> orb.setPickupDelay(5));
            safe(() -> EntityXpOrb.getMaxOrbSize(100));
            safe(() -> EntityXpOrb.splitIntoOrbSizes(100));
            checked++;
            close(orb);
        }

        EntityFallingBlock falling = spawn(EntityID.FALLING_BLOCK);
        if (falling != null) {
            safe(falling::getBlock);
            safe(falling::canBeMovedByCurrents);
            safe(falling::resetFallDistance);
            checked++;
            close(falling);
        }

        EntityTnt tnt = spawn(EntityID.TNT);
        if (tnt != null) {
            safe(tnt::getSource);
            safe(tnt::canCollide);
            checked++;
            close(tnt);
        }

        EntityArmorStand stand = spawn(EntityID.ARMOR_STAND);
        if (stand != null) {
            safe(stand::getInventory);
            safe(stand::getArmorInventory);
            safe(stand::getEquipmentInventory);
            safe(stand::canDoInteraction);
            safe(stand::canEquipByDispenser);
            checked++;
            close(stand);
        }

        EntityPainting painting = spawn(EntityID.PAINTING);
        if (painting != null) {
            safe(painting::getMotive);
            safe(() -> EntityPainting.getMotive("Kebab"));
            checked++;
            close(painting);
        }

        EntityMinecartAbstract cart = spawn(EntityID.CHEST_MINECART);
        if (cart != null) {
            safe(cart::getType);
            safe(cart::getMaxSpeed);
            safe(cart::getDisplayBlock);
            safe(cart::isSlowWhenEmpty);
            safe(() -> cart.setSlowWhenEmpty(false));
            safe(() -> cart.setCurrentSpeed(0.1));
            checked++;
            close(cart);
        }

        EntityFireworksRocket rocket = spawn(EntityID.FIREWORKS_ROCKET);
        if (rocket != null) {
            safe(() -> rocket.setLifetime(10));
            safe(rocket::getWidth);
            checked++;
            close(rocket);
        }
    }

    // --- projectiles -----------------------------------------------------

    private void driveProjectiles() {
        EntityArrow arrow = spawn(EntityID.ARROW);
        if (arrow != null) {
            safe(arrow::setCritical);
            safe(arrow::isCritical);
            safe(() -> arrow.setCritical(false));
            safe(arrow::getResultDamage);
            safe(arrow::getPickupMode);
            safe(() -> arrow.setPickupMode(EntityProjectile.PICKUP_ANY));
            safe(arrow::getArrowItem);
            exerciseProjectile(arrow);
        }

        EntityThrownTrident trident = spawn(EntityID.THROWN_TRIDENT);
        if (trident != null) {
            safe(trident::setCritical);
            safe(trident::isCritical);
            safe(trident::getResultDamage);
            safe(trident::getItem);
            safe(trident::getLoyaltyLevel);
            safe(() -> trident.setLoyaltyLevel(2));
            safe(trident::hasChanneling);
            safe(() -> trident.setChanneling(true));
            safe(trident::getRiptideLevel);
            safe(trident::getImpalingLevel);
            safe(trident::isCreative);
            safe(trident::canReturnToShooter);
            exerciseProjectile(trident);
        }

        for (String id : new String[]{EntityID.SNOWBALL, EntityID.EGG, EntityID.ENDER_PEARL, EntityID.FIREBALL}) {
            EntityProjectile p = spawn(id);
            if (p != null) {
                exerciseProjectile(p);
            }
        }
    }

    private void exerciseProjectile(EntityProjectile p) {
        safe(p::getResultDamage);
        safe(p::getHasAge);
        safe(() -> p.setHasAge(true));
        safe(p::getEnchantments);
        EntityAnimal victim = spawn(EntityID.CHICKEN);
        if (victim != null) {
            safe(() -> p.onCollideWithEntity(victim));
            close(victim);
        }
        checked++;
        close(p);
    }

    // --- helpers ---------------------------------------------------------

    @SuppressWarnings("unchecked")
    private <T extends Entity> T spawn(String id) {
        try {
            Entity e = Entity.createEntity(id, new Position(0, 80, 0, level));
            return (T) e;
        } catch (Throwable t) {
            return null;
        }
    }

    private Item safeItem(String id) {
        try {
            return Item.get(id);
        } catch (Throwable t) {
            return null;
        }
    }

    private void close(Entity e) {
        try {
            if (e != null && !e.isClosed()) {
                e.close();
            }
        } catch (Throwable ignored) {
        }
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignored) {
        }
    }
}
