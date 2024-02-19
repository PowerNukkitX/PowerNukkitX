package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;

public class ItemGoatHorn extends Item {
    protected int coolDownTick = 140;

    public ItemGoatHorn() {
        this(1);
    }

    public ItemGoatHorn(int count) {
        this(0, 1);
    }

    public ItemGoatHorn(Integer aux, int count) {
        super(GOAT_HORN);
        this.meta = aux;
        this.count = count;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.isItemCoolDownEnd(this.getIdentifier())) {
            playSound(player);
            player.setItemCoolDown(coolDownTick, this.getIdentifier());
            return true;
        } else return false;
    }

    /**
     * Sets cool down tick
     *
     * @param coolDownTick the cool down tick
     */
    public void setCoolDown(int coolDownTick) {
        this.coolDownTick = coolDownTick;
    }

    public void playSound(Player player) {
        switch (this.meta) {
            case 0 -> player.getLevel().addSound(player, Sound.HORN_CALL_0);
            case 1 -> player.getLevel().addSound(player, Sound.HORN_CALL_1);
            case 2 -> player.getLevel().addSound(player, Sound.HORN_CALL_2);
            case 3 -> player.getLevel().addSound(player, Sound.HORN_CALL_3);
            case 4 -> player.getLevel().addSound(player, Sound.HORN_CALL_4);
            case 5 -> player.getLevel().addSound(player, Sound.HORN_CALL_5);
            case 6 -> player.getLevel().addSound(player, Sound.HORN_CALL_6);
            case 7 -> player.getLevel().addSound(player, Sound.HORN_CALL_7);
        }
    }
}
