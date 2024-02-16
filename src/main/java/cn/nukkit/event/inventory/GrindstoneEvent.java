/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2021  José Roberto de Araújo Júnior
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

package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2021-03-21
 */


public class GrindstoneEvent extends InventoryEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final @NotNull Item firstItem;
    private final @NotNull Item resultItem;
    private final @NotNull Item secondItem;
    private final @NotNull Player player;
    private int experienceDropped;

    public GrindstoneEvent(GrindstoneInventory inventory, @NotNull Item firstItem, @NotNull Item resultItem, @NotNull Item secondItem, int cost, @NotNull Player player) {
        super(inventory);
        this.firstItem = firstItem;
        this.resultItem = resultItem;
        this.secondItem = secondItem;
        this.experienceDropped = cost;
        this.player = player;
    }

    @NotNull public Player getPlayer() {
        return this.player;
    }

    @NotNull public Item getFirstItem() {
        return this.firstItem;
    }

    @NotNull public Item getResultItem() {
        return this.resultItem;
    }

    @NotNull public Item getSecondItem() {
        return this.secondItem;
    }

    public int getExperienceDropped() {
        return this.experienceDropped;
    }

    public void setExperienceDropped(int experienceDropped) {
        this.experienceDropped = experienceDropped;
    }
}
