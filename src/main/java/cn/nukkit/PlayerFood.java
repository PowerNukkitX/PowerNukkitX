package cn.nukkit;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.item.ItemFood;

/**
 * This class manages the player's food level, saturation, and exhaustion.
 * It handles food consumption, exhaustion, and the effects of hunger on the player.
 *
 * @since 2015/11/11
 */
public class PlayerFood {

    private final Player player;

    // Current food level of the player
    private int food;
    // Maximum food level the player can have
    private final int maxFood;
    // Current saturation level of the player
    private float saturation;
    // Current exhaustion level of the player
    private double exhaustion;

    // Timer for food-related ticks
    private int foodTickTimer;

    // Indicates whether the food system is enabled
    private boolean enabled = true;

    /**
     * Constructs a PlayerFood instance for the specified player with initial food and saturation levels.
     *
     * @param player The player associated with this food manager.
     * @param food Initial food level.
     * @param saturation Initial saturation level.
     */
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

    /**
     * Sets the player's food and saturation levels.
     *
     * @param food The new food level.
     * @param saturation The new saturation level.
     */
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

    /**
     * Sets the player's saturation level.
     *
     * @param saturation The new saturation level.
     */
    public void setSaturation(float saturation) {
        saturation = Math.max(0, Math.min(saturation, food));

        PlayerFoodLevelChangeEvent event = new PlayerFoodLevelChangeEvent(player, food, saturation);
        Server.getInstance().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.saturation = event.getFoodSaturationLevel();
        }
    }

    /**
     * Adds food and saturation to the player's current levels.
     *
     * @param food The food item to be consumed.
     */
    public void addFood(ItemFood food) {
        this.addFood(food.getFoodRestore(), food.getSaturationRestore());
    }

    public void setFood(int food) {
        this.setFood(food, -1);
    }

    /**
     * Adds the specified amount of food and saturation to the player's current levels.
     *
     * @param food The amount of food to add.
     * @param saturation The amount of saturation to add.
     */
    public void addFood(int food, float saturation) {
        this.setFood(this.food + food, this.saturation + saturation);
    }

    /**
     * Sends the current food level to the player.
     */
    public void sendFood() {
        this.sendFood(this.food);
    }

    /**
     * Sends the specified food level to the player.
     *
     * @param food The food level to send.
     */
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

    /**
     * Sets the player's exhaustion level.
     *
     * @param exhaustion The new exhaustion level.
     */
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

    /**
     * Increases the player's exhaustion by the specified amount.
     *
     * @param amount The amount of exhaustion to add.
     */
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

    /**
     * Resets the player's food, saturation, and exhaustion levels to their defaults.
     */
    public void reset() {
        this.food = 20;
        this.saturation = 20;
        this.exhaustion = 0;
        this.foodTickTimer = 0;
        this.sendFood();
    }

    /**
     * Updates the player's food and health based on the current tick difference.
     *
     * @param tickDiff The difference in ticks since the last update.
     */
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