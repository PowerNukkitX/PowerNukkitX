package cn.nukkit.entity.ai.sensor;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.inventory.BarrelInventory;
import cn.nukkit.inventory.ChestBoatInventory;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.DoubleChestInventory;
import cn.nukkit.inventory.HumanEnderChestInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.MinecartChestInventory;
import cn.nukkit.inventory.ShulkerBoxInventory;
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
