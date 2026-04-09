package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemUseMethod;

public class ItemOminousBottle extends Item {

    private static final String TAG_OMINOUS_BOTTLE_AMPLIFIER = "OminousBottleAmplifier";
    private static final int BAD_OMEN_DURATION = 100 * 60 * 20;

    public ItemOminousBottle() {
        super(OMINOUS_BOTTLE);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @Override
    public int getUsingTicks() {
        return 31;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (ticksUsed < 31) {
            return false;
        }

        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, this);
        player.getServer().getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            player.getInventory().sendContents(player);
            return false;
        }

        int amplifier = Math.max(0, Math.min(4, this.getNamedTag() != null
                ? this.getNamedTag().getInt(TAG_OMINOUS_BOTTLE_AMPLIFIER, 0)
                : 0));

        player.addEffect(Effect.get(EffectType.BAD_OMEN)
                .setAmplifier(amplifier)
                .setDuration(BAD_OMEN_DURATION)
                .setVisible(true));

        player.completeUsingItem(this.getRuntimeId(), ItemUseMethod.EAT);
        player.getLevel().addLevelSoundEvent(player, SoundEvent.OMINOUS_BOTTLE_END_USE);
        player.getLevel().addLevelSoundEvent(player, SoundEvent.APPLY_EFFECT_BAD_OMEN);
        player.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(
                player,
                player.add(0, player.getEyeHeight(), 0),
                VibrationType.DRINKING
        ));

        if (player.isAdventure() || player.isSurvival()) {
            --this.count;
            player.getInventory().setItemInHand(this);
        }

        return true;
    }
}
