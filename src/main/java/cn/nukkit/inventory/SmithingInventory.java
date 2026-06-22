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

package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockSmithingTable;
import cn.nukkit.item.Item;
import com.google.common.collect.BiMap;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Map;

/**
 * @author joserobjr | CoolLoong
 * @since 2020-09-28
 */
public class SmithingInventory extends ContainerInventory implements CraftTypeInventory, SoleInventory {
    private static final int EQUIPMENT = 0;
    private static final int INGREDIENT = 1;
    private static final int TEMPLATE = 2;

    public SmithingInventory(BlockSmithingTable blockSmithingTable) {
        super(blockSmithingTable, ContainerType.SMITHING_TABLE, 3);
    }

    @Override
    public void init() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, 51 + i);
        }

        Map<Integer, ContainerEnumName> map2 = super.slotTypeMap();
        map2.put(0, ContainerEnumName.SMITHING_TABLE_INPUT_CONTAINER);
        map2.put(1, ContainerEnumName.SMITHING_TABLE_MATERIAL_CONTAINER);
        map2.put(2, ContainerEnumName.SMITHING_TABLE_TEMPLATE_CONTAINER);
    }

    public Item getEquipment() {
        return getItem(EQUIPMENT);
    }

    public void setEquipment(Item equipment) {
        setItem(EQUIPMENT, equipment);
    }

    public Item getIngredient() {
        return getItem(INGREDIENT);
    }

    public void setIngredient(Item ingredient) {
        setItem(INGREDIENT, ingredient);
    }

    public Item getTemplate() {
        return getItem(TEMPLATE);
    }

    public void setTemplate(Item template) {
        setItem(TEMPLATE, template);
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        who.giveItem(getItem(EQUIPMENT), getItem(INGREDIENT), getItem(TEMPLATE));

        this.clear(EQUIPMENT);
        this.clear(INGREDIENT);
        this.clear(TEMPLATE);
    }
}