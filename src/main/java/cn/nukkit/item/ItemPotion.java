package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.Vector3;

import javax.annotation.Nullable;

public class ItemPotion extends Item {

    public ItemPotion() {
        this(0, 1);
    }

    public ItemPotion(Integer meta) {
        this(meta, 1);
    }

    public ItemPotion(Integer meta, int count) {
        super(POTION, meta, count, "Potion");
        updateName();
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
        updateName();
    }

    private void updateName() {
        PotionType potion = this.getPotion();
        if (PotionType.WATER.equals(potion)) {
            name = buildName(potion, "Bottle", true);
        } else {
            name = buildName(potion, "Potion", true);
        }
    }

    static String buildName(PotionType potion, String type, boolean includeLevel) {
        return switch (potion.stringId()) {
            case "minecraft:water" -> "Water " + type;
            case "minecraft:mundane", "minecraft:long_mundane" -> "Mundane " + type;
            case "minecraft:thick" -> "Thick " + type;
            case "minecraft:awkward" -> "Awkward " + type;
            case "minecraft:turtle_master", "minecraft:long_turtle_master", "minecraft:strong_turtle_master" -> {
                String name = type + " of the Turtle Master";
                if (!includeLevel) {
                    yield name;
                }

                if (potion.level() <= 1) {
                    yield name;
                }

                yield name + " " + potion.getRomanLevel();
            }
            default -> {
                String finalName = potion.name();
                if (finalName.isEmpty()) {
                    finalName = type;
                } else {
                    finalName = type + " of " + finalName;
                }
                if (includeLevel && potion.level() > 1) {
                    finalName += " " + potion.getRomanLevel();
                }
                yield finalName;
            }
        };
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (ticksUsed < 31) {
            return false;
        }
        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, this);
        player.getServer().getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            return false;
        }
        PotionType potion = PotionType.get(this.getDamage());

        player.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player, player.getLocation(), VibrationType.DRINKING));

        if (player.isAdventure() || player.isSurvival()) {
            --this.count;
            player.getInventory().setItemInHand(this);
            player.getInventory().addItem(new ItemGlassBottle());
        }

        if (potion != null) {
            potion.applyEffects(player, false, 1);
        }
        return true;
    }

    public @Nullable PotionType getPotion() {
        return PotionType.get(getDamage());
    }

    public static ItemPotion fromPotion(PotionType potion) {
        return new ItemPotion(potion.id());
    }
}
