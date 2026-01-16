package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;

public abstract class ItemSpear extends ItemTool {

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

        // TODO: Stab mechanics
    }

    public float getChargeDamage(Player player, boolean fullCharge) {
        double speed = player.getMotion().length();
        float base = getAttackDamage();

        float velocityBonus = (float) Math.min(speed * 6.0, 12.0);
        float damage = base + velocityBonus;

        if (fullCharge) {
            damage += base * 0.5f;
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
