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
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 * @since 2021-07-14
 */
@PowerNukkitOnly
@Since("1.5.2.0-PN")
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
    void putSlotGetSlotBlock() {
        Item item = BlockState.of(BlockID.SHULKER_BOX, 11).asItemBlock();
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

    @Test
    void addSkin() {
        Skin skin = new Skin();
        skin.setSkinData(new byte[Skin.SINGLE_SKIN_SIZE]);
        skin.setSkinId("id");
        skin.setCapeData(new byte[Skin.SINGLE_SKIN_SIZE]);
        skin.setTrusted(true);
        skin.setAnimationData("animation");
        skin.setCapeId("id");
        skin.getAnimations().add(new SkinAnimation(new SerializedImage(10, 10, new byte[10]), 1, 2, 1));
        skin.getPersonaPieces().add(new PersonaPiece("id", "type", "packId", true, "product"));
        skin.getTintColors().add(new PersonaPieceTint("color", Arrays.asList("a", "b")));
        stream.putSkin(skin);

        Skin read = stream.getSkin();
        assertEquals(skin, read);
    }
}
