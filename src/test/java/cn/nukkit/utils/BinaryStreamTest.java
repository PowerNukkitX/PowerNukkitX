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

package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 * @since 2021-07-14
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class BinaryStreamTest {
    BinaryStream stream;

    @BeforeEach
    void setUp() {
        stream = new BinaryStream();
    }

    @Test
    void putSlotGetSlotNoTag() {
        Item item = new Item(1000, 0, 1, "Test");
        stream.putSlot(item);
        stream.setOffset(0);
        Item read = stream.getSlot();
        assertEquals(item, read);
    }

    @Test
    void putSlotGetSlotCustomName() {
        Item item = new Item(1000, 0, 1, "Test");
        item.setCustomName("CustomName");
        stream.putSlot(item);
        stream.setOffset(0);
        Item read = stream.getSlot();
        assertEquals(item, read);
    }
}
