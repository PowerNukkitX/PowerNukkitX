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
import cn.nukkit.resourcepacks.ResourcePack;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author joserobjr
 * @since 2021-07-13
 */
@PowerNukkitOnly
@Since("FUTURE")
class ResourcePacksInfoPacketTest {
    @Test
    void encodeDecode() {
        val resourcePack = mock(ResourcePack.class);
        when(resourcePack.getPackId()).thenReturn(UUID.randomUUID());
        when(resourcePack.getPackVersion()).thenReturn("1.0");
        when(resourcePack.getPackSize()).thenReturn(1000);
        
        val packet = new ResourcePacksInfoPacket();
        packet.setBehaviourPackEntries(new ResourcePack[]{resourcePack});
        packet.setResourcePackEntries(new ResourcePack[]{resourcePack});
        packet.setForcedToAccept(true);
        packet.setForcingServerPacksEnabled(true);
        packet.setScriptingEnabled(true);
        packet.encode();
        packet.decode();
        
        assertTrue(packet.isForcedToAccept());
        assertTrue(packet.isScriptingEnabled());
        assertTrue(packet.isForcingServerPacksEnabled());
        
        assertEquals(Collections.singletonList(resourcePack), Arrays.asList(packet.getBehaviourPackEntries()));
        assertEquals(Collections.singletonList(resourcePack), Arrays.asList(packet.getResourcePackEntries()));
    }
}
