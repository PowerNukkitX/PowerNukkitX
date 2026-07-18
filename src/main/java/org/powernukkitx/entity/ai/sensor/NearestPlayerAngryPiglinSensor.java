package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.inventory.BarrelInventory;
import org.powernukkitx.inventory.ChestBoatInventory;
import org.powernukkitx.inventory.ChestInventory;
import org.powernukkitx.inventory.DoubleChestInventory;
import org.powernukkitx.inventory.HumanEnderChestInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.MinecartChestInventory;
import org.powernukkitx.inventory.ShulkerBoxInventory;
import lombok.Getter;

//存储最近的玩家的Memory


@Getter
public class NearestPlayerAngryPiglinSensor implements ISensor {

    public NearestPlayerAngryPiglinSensor() {
    }

    @Override
    public void sense(EntityIntelligent entity) {
        for(Player player : entity.getViewers().values()) {
            if(player.distance(entity) < 32) {
                boolean trigger = false;
                if(player.getTopWindow().isPresent()) {
                    if(checkInventory(player.getTopWindow().get())) {
                        trigger = true;
                    }
                }
                if(player.isBreakingBlock()) {
                    if(checkBlock(player.breakingBlock)) {
                        trigger = true;
                    }
                }
                if(trigger) {
                    entity.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, player);
                }
            }
        }
    }

    @Override
    public int getPeriod() {
        return 1;
    }

    private boolean checkInventory(Inventory inventory) {
        return inventory instanceof ChestInventory ||
                inventory instanceof DoubleChestInventory ||
                inventory instanceof HumanEnderChestInventory ||
                inventory instanceof ShulkerBoxInventory ||
                inventory instanceof BarrelInventory ||
                inventory instanceof MinecartChestInventory ||
                inventory instanceof ChestBoatInventory;
    }

    private boolean checkBlock(Block block) {
        return switch (block.getId()) {
            case Block.GOLD_BLOCK,
                 Block.GOLD_ORE,
                 Block.GILDED_BLACKSTONE,
                 Block.NETHER_GOLD_ORE,
                 Block.RAW_GOLD_BLOCK,
                 Block.DEEPSLATE_GOLD_ORE -> true;
            default -> false;
        };
    }

}
