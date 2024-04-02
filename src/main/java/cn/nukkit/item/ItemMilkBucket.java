package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.CompletedUsingItemPacket;

public class ItemMilkBucket extends ItemBucket {
    public ItemMilkBucket() {
        super(MILK_BUCKET);
    }

    @Override
    public void setDamage(int meta) {

    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (ticksUsed < 31) {
            return false;
        }

        PlayerItemConsumeEvent event = new PlayerItemConsumeEvent(player, this);
        Server.getInstance().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            player.getInventory().sendContents(player);
            return false;
        }

        player.removeAllEffects();

        player.completeUsingItem(this.getRuntimeId(), CompletedUsingItemPacket.ACTION_EAT);

        if (player.isAdventure() || player.isSurvival()) {
            --this.count;
            player.getInventory().addItem(Item.get(ItemID.BUCKET, 0, 1));
            player.getInventory().setItemInHand(this);
            player.getLevel().addSound(player, Sound.RANDOM_BURP);
        }

        player.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(player, player.add(0, player.getEyeHeight()), VibrationType.DRINKING));

        return true;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }
}