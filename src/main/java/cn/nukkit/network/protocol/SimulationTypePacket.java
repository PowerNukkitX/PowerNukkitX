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

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

/**
 * @author joserobjr
 * @since 2021-07-06
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SimulationTypePacket extends DataPacket {
    private static final SimulationType[] TYPES = SimulationType.values();

    public SimulationType type = SimulationType.GAME;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        type = TYPES[byteBuf.readByte()];
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) type.ordinal());
    }

    public enum SimulationType {
        GAME,
        EDITOR,
        TEST
    }

    @Override
    public int pid() {
        return ProtocolInfo.SIMULATION_TYPE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
