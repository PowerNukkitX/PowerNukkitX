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

package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.SetTitlePacket.TitleAction;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2021-07-13
 */
@PowerNukkitOnly
@Since("FUTURE")
class SetTitlePacketTest {
    @Test
    void encodeDecode() {
        val packet = new SetTitlePacket();
        packet.setTitleAction(TitleAction.SET_TITLE_MESSAGE);
        packet.setText("text");
        packet.setFadeInTime(1);
        packet.setFadeOutTime(2);
        packet.setStayTime(3);
        packet.setXuid("xuid");
        packet.setPlatformOnlineId("poid");
        packet.encode();
        
        val packet2 = new SetTitlePacket();
        packet2.setBuffer(packet.getBuffer());
        packet2.getUnsignedVarInt();
        packet2.decode();
        
        assertEquals(TitleAction.SET_TITLE_MESSAGE, packet2.getTitleAction());
        assertEquals("text", packet2.getText());
        assertEquals(1, packet2.getFadeInTime());
        assertEquals(2, packet2.getFadeOutTime());
        assertEquals(3, packet2.getStayTime());
        assertEquals("xuid", packet2.getXuid());
        assertEquals("poid", packet2.getPlatformOnlineId());
        assertTrue(packet2.feof());
    }

    @Test
    void badType() {
        val packet = new SetTitlePacket();
        packet.type = -1;
        assertThrows(UnsupportedOperationException.class, packet::getTitleAction);

        val values = TitleAction.values();
        packet.type = values.length;
        assertThrows(UnsupportedOperationException.class, packet::getTitleAction);

        packet.type = values.length - 1;
        assertEquals(values[values.length - 1], packet.getTitleAction());
    }
}
