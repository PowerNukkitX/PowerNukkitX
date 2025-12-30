package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityPig;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;

/**
 * @author lion
 * @since 21.03.17
 */
public class ItemCarrotOnAStick extends ItemTool {

    public ItemCarrotOnAStick() {
        this(0, 1);
    }

    public ItemCarrotOnAStick(Integer meta) {
        this(meta, 1);
    }

    public ItemCarrotOnAStick(Integer meta, int count) {
        super(CARROT_ON_A_STICK, meta, count, "Carrot on a Stick");
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if(player.getRiding() != null) {
            if(player.getRiding() instanceof EntityPig pig) {
                if(pig.getMemoryStorage().get(CoreMemoryTypes.PIG_BOOST) == 0) {
                    pig.getMemoryStorage().put(CoreMemoryTypes.PIG_BOOST, Utils.rand(140, 980));
                    if (!this.isUnbreakable() && !player.isCreative()) {
                        this.setDamage(getDamage() + 7);
                        if (this.getDamage() >= getMaxDurability()) {
                            player.getLevel().addSound(player, Sound.RANDOM_BREAK);
                            player.getInventory().setItem(player.getInventory().getHeldItemIndex(), Item.get(Item.FISHING_ROD));
                        } else player.getInventory().setItemInHand(this);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CARROT_ON_A_STICK;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean noDamageOnBreak() {
        return true;
    }
}

