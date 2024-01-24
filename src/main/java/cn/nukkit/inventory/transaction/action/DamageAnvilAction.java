package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockAnvil;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.property.enums.Damage;
import cn.nukkit.event.block.AnvilDamageEvent;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import lombok.ToString;

import java.util.concurrent.ThreadLocalRandom;


@ToString(callSuper = true)
public class DamageAnvilAction extends InventoryAction {

    private final AnvilInventory anvil;
    private boolean shouldDamage;
    private CraftingTransaction transaction;


    public DamageAnvilAction(AnvilInventory anvil, boolean shouldDamage, CraftingTransaction transaction) {
        super(Item.AIR, Item.AIR);
        this.anvil = anvil;
        this.shouldDamage = shouldDamage;
        this.transaction = transaction;
    }

    @Override
    public boolean isValid(Player source) {
        return true;
    }

    @Override
    public boolean execute(Player source) {
        Block levelBlock = anvil.getHolder().getLevelBlock();
        if (!(levelBlock instanceof BlockAnvil)) {
            return false;
        }
        Block newState = levelBlock.clone();
        BlockAnvil anvil = (BlockAnvil) newState;
        Damage damage = anvil.getAnvilDamage();
        if (damage.ordinal() >= 2) {
            newState = new BlockAir();
        } else {
            anvil.setAnvilDamage(damage.next());
        }
        AnvilDamageEvent ev = new AnvilDamageEvent(levelBlock, anvil, AnvilDamageEvent.DamageCause.USE, source, transaction);
        ev.setCancelled(!shouldDamage);
        source.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            levelBlock.getLevel().addSound(levelBlock, Sound.RANDOM_ANVIL_USE);
            return true;
        } else {
            if (newState.isAir()) {
                levelBlock.getLevel().addSound(levelBlock, Sound.RANDOM_ANVIL_BREAK);
            } else {
                levelBlock.getLevel().addSound(levelBlock, Sound.RANDOM_ANVIL_USE);
            }
            return levelBlock.getLevel().setBlock(levelBlock, newState, true, true);
        }
    }

    @Override
    public void onExecuteSuccess(Player source) {

    }

    @Override
    public void onExecuteFail(Player source) {

    }
}
