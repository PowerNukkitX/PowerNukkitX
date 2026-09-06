package org.powernukkitx.entity.mob;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.passive.EntityVillagerV2;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.enchantment.EnchantmentHelper;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.powernukkitx.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public abstract class EntityIllager extends EntityMob implements EntityWalkable {
    protected static final String[] RAID_GEAR = {
            ItemID.IRON_PICKAXE, ItemID.IRON_AXE, ItemID.IRON_SHOVEL, ItemID.IRON_SWORD,
            ItemID.IRON_HELMET, ItemID.IRON_CHESTPLATE, ItemID.IRON_LEGGINGS, ItemID.IRON_BOOTS
    };

    private boolean raiding;

    public EntityIllager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.raiding = this.nbt.getBoolean("Raiding");
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putBoolean("Raiding", this.raiding);
    }

    /**
     * @return whether this illager was spawned for a raid, which is what grants it the extra drops
     */
    public boolean isRaiding() {
        return raiding;
    }

    public void setRaiding(boolean raiding) {
        this.raiding = raiding;
    }

    /**
     * The drops an illager killed during a raid hands out on top of its own.
     *
     * @param weapon the weapon it was killed with, for the looting enchantment
     */
    protected Item[] raidDrops(@NotNull Item weapon) {
        if (!raiding) {
            return Item.EMPTY_ARRAY;
        }

        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();
        int emeralds = Utils.rand(0, 1) + lootingBonus(looting);
        if (emeralds > 0) {
            drops.add(Item.get(ItemID.EMERALD, 0, emeralds));
        }

        Item bonus = rollRaidBonus(looting);
        if (bonus != null) {
            drops.add(bonus);
        }
        return drops.toArray(Item.EMPTY_ARRAY);
    }

    private static int lootingBonus(int looting) {
        int bonus = 0;
        for (int level = 0; level < looting; level++) {
            bonus += Utils.rand(0, 1);
        }
        return bonus;
    }

    private @Nullable Item rollRaidBonus(int looting) {
        float chance = switch (getServer().getDifficulty()) {
            case 0 -> 0f;
            case 3 -> 0.8f;
            default -> 0.65f;
        };
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextFloat() >= chance) {
            return null;
        }

        int roll = random.nextInt(156);
        if (roll < 40) {
            return Item.get(ItemID.EMERALD, 0, Utils.rand(0, 1) + lootingBonus(looting));
        }
        if (roll < 60) {
            return Item.get(ItemID.EMERALD, 0, Utils.rand(2, 3) + lootingBonus(looting));
        }
        if (roll < 68) {
            return Item.get(ItemID.EMERALD, 0, Utils.rand(4, 5) + lootingBonus(looting));
        }
        if (roll < 76) {
            Item book = Item.get(ItemID.ENCHANTED_BOOK);
            for (Enchantment enchantment : EnchantmentHelper.selectEnchantments(new NukkitRandom(), book, 30)) {
                book.addEnchantment(enchantment);
            }
            return book;
        }

        Item gear = Item.get(RAID_GEAR[(roll - 76) / 10]);
        gear.setDamage((int) (gear.getMaxDurability() * (0.3 + random.nextDouble() * 0.6)));
        return enchantGear(gear, 0.5f);
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case VILLAGER ->
                    entity instanceof EntityVillagerV2 villager && !villager.isBaby();
            case IRON_GOLEM, WANDERING_TRADER -> true;
            default -> super.attackTarget(entity);
        };
    }
}
