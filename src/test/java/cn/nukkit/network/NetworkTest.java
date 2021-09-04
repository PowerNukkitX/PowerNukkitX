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

package cn.nukkit.network;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 * @since 2021-07-13
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(MockitoExtension.class)
class NetworkTest {
    @Mock
    Server server;
    
    Network network;

    @BeforeEach
    void setUp() {
        network = new Network(server);
    }

    @Test
    void addStatistics() {
        network.addStatistics(1, 2);
        assertEquals(1, network.getUpload());
        assertEquals(2, network.getDownload());

        network.addStatistics(1, 2);
        assertEquals(2, network.getUpload());
        assertEquals(4, network.getDownload());
    }
}
