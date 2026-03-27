package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.components.BoostableComponent;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;

/**
 * @author lion
 * @since 21.03.17
 */
public class ItemCarrotOnAStick extends ItemTool {

    public ItemCarrotOnAStick() {
        this(0, 1);
    }

    public ItemCarrotOnAStick(Integer meta) {
        this(meta, 1);
    }

    public ItemCarrotOnAStick(Integer meta, int count) {
        super(CARROT_ON_A_STICK, meta, count, "Carrot on a Stick");
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        Entity e = player.getRiding();
        if (!(e instanceof EntityLiving ride) || !ride.isBoostable()) return false;
        if (ride.getBoostableTicks() > -1) return false;

        BoostableComponent boostable = ride.getComponentBoostable();
        if (boostable == null) return false;

        BoostableComponent.BoostItem entry = boostable.resolveBoostItem(this);
        if (entry == null) return false;

        ride.setBoostableDuration(boostable.resolvedDuration());

        if (!this.isUnbreakable() && !player.isCreative()) {
            Item newItem = entry.applyUse(this);
            if (newItem.isNull()) player.getLevel().addSound(player, Sound.RANDOM_BREAK);
            player.getInventory().setItem(player.getInventory().getHeldItemIndex(), newItem);
        }

        return false;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CARROT_ON_A_STICK;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean noDamageOnBreak() {
        return true;
    }
}

