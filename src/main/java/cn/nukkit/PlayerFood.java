package cn.nukkit;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.item.ItemFood;

/**
 * @author funcraft
 * @since 2015/11/11
 */
public class PlayerFood {

    private final Player player;

    private int food;
    private final int maxFood;
    private float saturation;
    private double exhaustion;

    private int foodTickTimer;

    private boolean enabled = true;

    public PlayerFood(Player player, int food, float saturation) {
        this.player = player;
        this.food = food;
        this.maxFood = 20;
        this.saturation = saturation;
    }

    public Player getPlayer() {
        return player;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food, float saturation) {
        food = Math.max(0, Math.min(food, 20));

        if (food <= 6 && this.food > 6 && this.player.isSprinting()) {
            this.player.setSprinting(false);
        }

        PlayerFoodLevelChangeEvent event = new PlayerFoodLevelChangeEvent(this.player, food, saturation);
        Server.getInstance().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            this.sendFood(this.food);
            return;
        }

        this.food = event.getFoodLevel();
        this.saturation = Math.min(event.getFoodSaturationLevel(), food);

        this.sendFood();
    }

    public int getMaxFood() {
        return maxFood;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        saturation = Math.max(0, Math.min(saturation, food));

        PlayerFoodLevelChangeEvent event = new PlayerFoodLevelChangeEvent(player, food, saturation);
        Server.getInstance().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.saturation = event.getFoodSaturationLevel();
        }
    }

    public void addFood(ItemFood food) {
        this.addFood(food.getFoodRestore(), food.getSaturationRestore());
    }

    public void setFood(int food) {
        this.setFood(food, -1);
    }

    public void addFood(int food, float saturation) {
        this.setFood(this.food + food, this.saturation + saturation);
    }

    public void sendFood() {
        this.sendFood(this.food);
    }

    public void sendFood(int food) {
        if (this.player.spawned) {
            Attribute attribute = player.getAttributes().computeIfAbsent(Attribute.FOOD, Attribute::getAttribute);
            if (attribute.getValue() != food) {
                attribute.setValue(food);
                this.player.syncAttribute(attribute);
            }
        }
    }

    public boolean isHungry() {
        return food < maxFood;
    }

    public double getExhaustion() {
        return exhaustion;
    }

    public void setExhaustion(float exhaustion) {
        while (exhaustion >= 4.0f) {
            exhaustion -= 4.0f;
            float saturation = this.saturation;
            if (saturation > 0) {
                saturation = Math.max(0, saturation - 1.0f);
                this.setSaturation(saturation);
            } else {
                int food = this.food;
                if (food > 0) {
                    food--;
                    this.setFood(Math.max(food, 0));
                }
            }
        }
        this.exhaustion = exhaustion;
    }

    public void exhaust(double amount) {
        if (!this.isEnabled() || Server.getInstance().getDifficulty() == 0 || player.hasEffect(EffectType.SATURATION)) {
            return;
        }

        double exhaustion = this.exhaustion + amount;

        while (exhaustion >= 4.0f) {
            exhaustion -= 4.0f;

            float saturation = this.saturation;
            if (saturation > 0) {
                saturation = Math.max(0, saturation - 1.0f);
                this.setSaturation(saturation);
            } else {
                int food = this.food;
                if (food > 0) {
                    food--;
                    this.setFood(Math.max(food, 0));
                }
            }
        }

        this.exhaustion = exhaustion;
    }

    public int getFoodTickTimer() {
        return foodTickTimer;
    }

    public void reset() {
        this.food = 20;
        this.saturation = 20;
        this.exhaustion = 0;
        this.foodTickTimer = 0;
        this.sendFood();
    }

    public void tick(int tickDiff) {
        if (!player.isAlive() || !this.isEnabled()) {
            return;
        }

        double health = player.getHealth();

        this.foodTickTimer += tickDiff;
        if (this.foodTickTimer >= 80) {
            this.foodTickTimer = 0;
        }

        int difficulty = Server.getInstance().getDifficulty();

        if (difficulty == 0 && this.foodTickTimer % 10 == 0) {
            if (this.isHungry()) {
                this.addFood(1, 0);
            }
            if (this.foodTickTimer % 20 == 0 && health < this.player.getMaxHealth()) {
                this.player.heal(new EntityRegainHealthEvent(this.player, 1, EntityRegainHealthEvent.CAUSE_EATING));
            }
        }

        if (this.foodTickTimer == 0) {
            if (this.food >= 18) {
                if (health < player.getMaxHealth()) {
                    this.player.heal(new EntityRegainHealthEvent(this.player, 1, EntityRegainHealthEvent.CAUSE_EATING));
                    this.exhaust(6);
                }
            } else if (food <= 0) {
                if ((difficulty == 1 && health > 10) || (difficulty == 2 && health > 1) || difficulty == 3) {
                    this.player.attack(new EntityDamageEvent(this.player, EntityDamageEvent.DamageCause.HUNGER, 1));
                }
            }
        }

        if (this.food <= 6) {
            this.player.setSprinting(false);
        }
    }

    public boolean isEnabled() {
        return !(player.isCreative() || player.isSpectator()) && this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
