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

package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2020-010-06
 */


public class BlockExplosionPrimeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private double force;
    private boolean blockBreaking;
    private double fireChance;
    private final Player player;

    public BlockExplosionPrimeEvent(Block block, double force, double fireChance) {
        this(block, null, force, fireChance);
    }

    public BlockExplosionPrimeEvent(Block block, @Nullable Player player, double force) {
        this(block, player, force, 0);
    }

    public BlockExplosionPrimeEvent(Block block, @Nullable Player player, double force, double fireChance) {
        super(block);
        this.force = force;
        this.blockBreaking = true;
        this.fireChance = fireChance;
        this.player = player;
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public boolean isBlockBreaking() {
        return blockBreaking;
    }

    public void setBlockBreaking(boolean blockBreaking) {
        this.blockBreaking = blockBreaking;
    }

    public boolean isIncendiary() {
        return fireChance > 0;
    }

    public void setIncendiary(boolean incendiary) {
        if (!incendiary) {
            fireChance = 0;
        } else if (fireChance <= 0) {
            fireChance = 1.0 / 3.0;
        }
    }

    public double getFireChance() {
        return fireChance;
    }

    public void setFireChance(double fireChance) {
        this.fireChance = fireChance;
    }

    public @Nullable Player getPlayer() {
        return player;
    }
}
