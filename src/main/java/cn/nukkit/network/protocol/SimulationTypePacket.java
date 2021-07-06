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

import javax.annotation.Nonnull;

/**
 * @author joserobjr
 * @since 2021-07-06
 */
@PowerNukkitOnly
@Since("FUTURE")
public class SimulationTypePacket extends DataPacket {
    @PowerNukkitOnly
    @Since("FUTURE")
    public static final byte NETWORK_ID = ProtocolInfo.NPC_DIALOGUE_PACKET;
    
    private static final SimulationType[] TYPES = SimulationType.values();
    
    private SimulationType type;

    @PowerNukkitOnly
    @Since("FUTURE")
    public SimulationTypePacket() {
        type = SimulationType.GAME;
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        type = TYPES[getByte()];
    }

    @Override
    public void encode() {
        reset();
        putByte((byte) type.ordinal());
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @Nonnull
    public SimulationType getSimulationType() {
        return type;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setSimulationType(@Nonnull SimulationType type) {
        this.type = type;
    }
    
    @PowerNukkitOnly
    @Since("FUTURE")
    public enum SimulationType {
        @PowerNukkitOnly @Since("FUTURE") GAME,
        @PowerNukkitOnly @Since("FUTURE") EDITOR,
        @PowerNukkitOnly @Since("FUTURE") TEST
    } 
}
