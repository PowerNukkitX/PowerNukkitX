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
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemTrimMaterial;
import cn.nukkit.item.ItemTrimPattern;
import cn.nukkit.level.Position;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * @author joserobjr
 * @since 2020-09-28
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class SmithingInventory extends FakeBlockUIComponent {
    private static final int EQUIPMENT = 0;
    private static final int INGREDIENT = 1;
    @PowerNukkitXOnly
    @Since("1.20.30-r2")
    private static final int TEMPLATE = 2;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int SMITHING_EQUIPMENT_UI_SLOT = 51;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int SMITHING_INGREDIENT_UI_SLOT = 52;

    @PowerNukkitXOnly
    @Since("1.20.30-r2")
    public static final int SMITHING_TEMPLATE_UI_SLOT = 53;

    @Since("1.19.63-r2")
    @PowerNukkitXOnly
    private Item currentResult = AIR_ITEM;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public SmithingInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.SMITHING_TABLE, 51, position);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public SmithingRecipe matchRecipe() {
        return Server.getInstance().getCraftingManager().matchSmithingRecipe(Arrays.asList(getEquipment(), getIngredient(), getTemplate()));
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        if (index == EQUIPMENT || index == INGREDIENT || index == TEMPLATE) {
            updateResult();
        }
        super.onSlotChange(index, before, send);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void updateResult() {
        Item result;
        SmithingRecipe recipe = matchRecipe();
        if (recipe == null) {
            result =  AIR_ITEM;
        } else {
            result = recipe.getFinalResult(getEquipment(), getTemplate());
        }
        setResult(result);
    }
    
    private void setResult(Item result) {
//        We don't need to send the result to the player, because the client will do it for us
//        playerUI.setItem(50, result);
        this.currentResult = result;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public Item getResult() {
        SmithingRecipe recipe = matchRecipe();
        if (recipe == null) {
            return AIR_ITEM;
        }
        return recipe.getFinalResult(getEquipment(), getTemplate());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getEquipment() {
        return getItem(EQUIPMENT);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setEquipment(Item equipment) {
        setItem(EQUIPMENT, equipment);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getIngredient() {
        return getItem(INGREDIENT);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setIngredient(Item ingredient) {
        setItem(INGREDIENT, ingredient);
    }

    @PowerNukkitXOnly
    @Since("1.20.30-r2")
    public Item getTemplate() {
        return getItem(TEMPLATE);
    }

    @PowerNukkitXOnly
    @Since("1.20.30-r2")
    public void setTemplate(Item template) {
        setItem(TEMPLATE, template);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_SMITHING;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;

        who.giveItem(getItem(EQUIPMENT), getItem(INGREDIENT), getItem(TEMPLATE));
        
        this.clear(EQUIPMENT);
        this.clear(INGREDIENT);
        this.clear(TEMPLATE);
        playerUI.clear(CREATED_ITEM_OUTPUT_UI_SLOT);
    }

    @Since("1.19.63-r2")
    @PowerNukkitXOnly
    public @NotNull Item getCurrentResult() {
        return currentResult;
    }

    @PowerNukkitXOnly
    @Since("1.20.30-r2")
    public Item getTrimOutPutItem() {
        Item input = this.getEquipment().clone();
        if (this.getIngredient() instanceof ItemTrimMaterial && this.getTemplate() instanceof ItemTrimPattern) {
            if (!input.isNull() && input instanceof ItemArmor) {
                ItemArmor trimmedArmor = (ItemArmor) input.clone();
                ItemTrimMaterial material = (ItemTrimMaterial) this.getIngredient();
                ItemTrimPattern pattern = (ItemTrimPattern) this.getTemplate();
                trimmedArmor.setTrim(pattern.getPattern(), material.getMaterial());
                return trimmedArmor;
            }
        }
        return Item.AIR_ITEM.clone();
    }
}
