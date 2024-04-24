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

package cn.nukkit.entity.mob;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author joserobjr
 * @since 2021-01-13
 */
public class EntityIronGolem extends EntityMob implements EntityWalkable {
    @Override
    @NotNull public String getIdentifier() {
        return IRON_GOLEM;
    }

    public EntityIronGolem(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Iron Golem";
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 2.9f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(100);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        // Item drops
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int flowerAmount = random.nextInt(3);
        Item[] drops;
        if (flowerAmount > 0) {
            drops = new Item[2];
            drops[1] = Item.get(BlockID.RED_TULIP, 0, flowerAmount);
        } else {
            drops = new Item[1];
        }

        drops[0] = Item.get(ItemID.IRON_INGOT, 0, random.nextInt(3, 6));

        return drops;
    }
}
