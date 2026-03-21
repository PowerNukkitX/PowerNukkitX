/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.components.BoostableComponent;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;

/**
 * @author joserobjr
 * @since 2021-02-16
 */

public class ItemWarpedFungusOnAStick extends ItemTool {

    public ItemWarpedFungusOnAStick() {
        this(0, 1);
    }

    public ItemWarpedFungusOnAStick(Integer meta) {
        this(meta, 1);
    }

    public ItemWarpedFungusOnAStick(Integer meta, int count) {
        super(WARPED_FUNGUS_ON_A_STICK, meta, count, "Warped Fungus on a Stick");
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
        return ItemTool.DURABILITY_WARPED_FUNGUS_ON_A_STICK;
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
