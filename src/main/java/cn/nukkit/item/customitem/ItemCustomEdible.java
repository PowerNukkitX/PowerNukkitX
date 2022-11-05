package cn.nukkit.item.customitem;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.item.food.Food;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.CompletedUsingItemPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author lt_name
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class ItemCustomEdible extends ItemCustom {

    public ItemCustomEdible(@Nonnull String id, @Nullable String name) {
        super(id, name);
    }

    public ItemCustomEdible(@Nonnull String id, @Nullable String name, @Nonnull String textureName) {
        super(id, name, textureName);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.getFoodData().getLevel() < player.getFoodData().getMaxLevel() || player.isCreative() || this.canAlwaysEat()) {
            return true;
        }
        player.getFoodData().sendFoodLevel();
        return false;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (player.isSpectator()) {
            player.getInventory().sendContents(player);
            return false;
        }

        if (ticksUsed < this.getEatTick()) {
            return false;
        }
        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, this);

        player.getServer().getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            return false; // Inventory#sendContents is called in Player
        }

        Food food = Food.getByRelative(this);
        if (food != null && food.eatenBy(player)) {
            player.completeUsingItem(this.getNetworkId(), CompletedUsingItemPacket.ACTION_EAT);
            player.getLevel().addSound(player, Sound.RANDOM_BURP);
            if (!player.isCreative() && !player.isSpectator()) {
                --this.count;
                player.getInventory().setItemInHand(this);
            }
        }
        return true;
    }

    public int getEatTick() {
        return 40;
    }

    public boolean isDrink() {
        return false;
    }

    public boolean canAlwaysEat() {
        return this.isDrink();
    }
}
