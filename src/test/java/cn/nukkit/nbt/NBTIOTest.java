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

package cn.nukkit.nbt;

import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 * @since 2021-02-14
 */
@ExtendWith(PowerNukkitExtension.class)
class NBTIOTest {
    @Test
    void gitHubIssue960() {
        Item badItem = new Item(879, 3, 12, "Cocoa Bean from a bad Alpha version");
        CompoundTag compoundTag = NBTIO.putItemHelper(badItem);
        Item recoveredItem = NBTIO.getItemHelper(compoundTag);
        Item correctItem = MinecraftItemID.COCOA_BEANS.get(12);
        assertEquals(correctItem, recoveredItem);
    }

    @Test
    void TestToSnbt() {
        var compoundTag = new CompoundTag();
        compoundTag
                .putList(new ListTag<CompoundTag>("list1")
                        .add(new CompoundTag()
                                .putInt("int", 1)
                                .putString("str", "string"))
                        .add(new CompoundTag()
                                .putInt("int", 2)
                                .putString("str", "string2")))
                .putCompound("cmp", new CompoundTag()
                        .putString("str", "string3")
                        .putCompound("", new CompoundTag()
                                .putInt("int1", 123)
                                .putInt("int2", 321)
                                .putInt("int3", 132)
                                .putCompound("", new CompoundTag()
                                        .putString("str1", "test1")
                                        .putString("str2", "test2")
                                        .putString("str3", "test3")
                                        .putCompound("", new CompoundTag()
                                                .putString("str1", "test1")
                                                .putString("str2", "test2")
                                                .putString("str3", "test3")))))
                .putList(new ListTag<StringTag>("list2")
                        .add(new StringTag("str", "string4"))
                        .add(new StringTag("str", "string5"))
                        .add(new StringTag("str", "string6")));
        System.out.println(compoundTag.toSnbt(4));
    }
}
