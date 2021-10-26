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

package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static cn.nukkit.block.BlockID.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * @author joserobjr
 * @since 2021-07-14
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class BlockEntityTest {
    @MockLevel
    Level level;

    /**
     * https://github.com/PowerNukkit/PowerNukkit/issues/1174
     */
    @Test
    void issue1174() {
        Server server = level.getServer();
        when(server.isRedstoneEnabled()).thenReturn(true);
        when(level.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);
        
        Vector3 pos = new Vector3(0,64, 0);
        level.setBlock(pos, Block.get(STONE));
        level.setBlock(pos.getSide(BlockFace.EAST), Block.get(STONE));
        level.setBlock(pos.getSide(BlockFace.EAST, 2), Block.get(STONE));
        level.setBlock(pos.getSide(BlockFace.EAST, 3), Block.get(STONE));
        
        pos.y++;
        level.setBlock(pos, Block.get(REDSTONE_WIRE));
        level.setBlock(pos.getSide(BlockFace.EAST), BlockState.of(UNPOWERED_COMPARATOR)
                .withProperty(CommonBlockProperties.DIRECTION, BlockFace.EAST)
                .getBlock());
        level.setBlock(pos.getSide(BlockFace.EAST, 2), Block.get(CHEST));
        level.setBlock(pos.getSide(BlockFace.EAST, 3), Block.get(CHEST));
        
        BlockChest chest1 = (BlockChest) level.getBlock(pos.getSide(BlockFace.EAST, 2));
        BlockChest chest2 = (BlockChest) level.getBlock(pos.getSide(BlockFace.EAST, 3));

        BlockEntityChest chest1Entity = chest1.getOrCreateBlockEntity();
        BlockEntityChest chest2Entity = chest2.getOrCreateBlockEntity();
        
        ChestInventory chest1Inventory = chest1Entity.getRealInventory();
        int size = chest1Inventory.getSize();
        for (int i = 0; i < size; i++) {
            chest1Inventory.setItem(i, Item.getBlock(STONE,0,64));
        }
        
        chest1Entity.pairWith(chest2Entity);
        
        chest2Entity.checkPairing();
    }
}
