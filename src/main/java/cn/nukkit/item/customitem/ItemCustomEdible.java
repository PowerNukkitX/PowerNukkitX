package cn.nukkit.item.customitem;

import cn.nukkit.Player;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.food.Food;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author lt_name
 */


public abstract class ItemCustomEdible extends ItemEdible implements CustomItem {
    private final String id;
    private final String textureName;

    public ItemCustomEdible(@NotNull String id, @Nullable String name) {
        super(ItemID.STRING_IDENTIFIED_ITEM, 0, 1, StringItem.notEmpty(name));
        this.id = id;
        this.textureName = name;
    }

    public ItemCustomEdible(@NotNull String id, @Nullable String name, @NotNull String textureName) {
        super(ItemID.STRING_IDENTIFIED_ITEM, 0, 1, StringItem.notEmpty(name));
        this.id = id;
        this.textureName = textureName;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.getFoodData().getLevel() < player.getFoodData().getMaxLevel() || player.isCreative() || canAlwaysEat()) {
            return true;
        }
        player.getFoodData().sendFoodLevel();
        return false;
    }

    public String getTextureName() {
        return textureName;
    }


    @Override
    public String getNamespaceId() {
        return id;
    }

    @Override
    public final int getId() {
        return CustomItem.super.getId();
    }


    public abstract Map.Entry<Plugin, Food> getFood();

    public boolean isDrink() {
        return false;
    }

    public boolean canAlwaysEat() {
        return false;
    }
}
