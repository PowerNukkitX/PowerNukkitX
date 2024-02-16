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

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

import java.util.Set;

/**
 * @author joserobjr
 * @since 2020-10-06
 */


public class BlockExplodeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Position position;
    protected final double fireChance;

    protected double yield;
    protected Set<Block> blocks;
    protected Set<Block> ignitions;

    public BlockExplodeEvent(Block block, Position position, Set<Block> blocks, Set<Block> ignitions, double yield, double fireChance) {
        super(block);
        this.position = position;
        this.blocks = blocks;
        this.yield = yield;
        this.ignitions = ignitions;
        this.fireChance = fireChance;
    }

    public Position getPosition() {
        return this.position;
    }

    public Set<Block> getAffectedBlocks() {
        return this.blocks;
    }

    public void setAffectedBlocks(Set<Block> blocks) {
        this.blocks = blocks;
    }

    public double getYield() {
        return this.yield;
    }

    public void setYield(double yield) {
        this.yield = yield;
    }

    public Set<Block> getIgnitions() {
        return ignitions;
    }

    public void setIgnitions(Set<Block> ignitions) {
        this.ignitions = ignitions;
    }

    public double getFireChance() {
        return fireChance;
    }
}
