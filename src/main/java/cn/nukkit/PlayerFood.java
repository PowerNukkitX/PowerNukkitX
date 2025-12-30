package cn.nukkit;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFood;

/**
 * Manages the food and hunger system for a player.
 * <p>
 * Handles food level, saturation, exhaustion, and related mechanics such as healing and starvation.
 * This class is responsible for updating the player's food state, applying exhaustion, and triggering
 * events related to food changes.
 *
 * @author funcraft
 * @since 2015/11/11
 */
public class PlayerFood {
    /**
     * The player associated with this food manager.
     */
    private final Player player;
    /**
     * Current food level (0-20).
     */
    private int food;
    /**
     * Maximum food level (always 20).
     */
    private final int maxFood;
    /**
     * Current saturation level (cannot exceed food level).
     */
    private float saturation;
    /**
     * Current exhaustion value. When exhaustion reaches 4, it reduces saturation or food.
     */
    private double exhaustion;
    /**
     * Internal timer for food tick updates.
     */
    private int foodTickTimer;
    /**
     * Whether the food system is enabled for this player.
     */
    private boolean enabled = true;

    /**
     * Constructs a PlayerFood manager for the given player.
     *
     * @param player     The player instance
     * @param food       Initial food level
     * @param saturation Initial saturation level
     */
    public PlayerFood(Player player, int food, float saturation) {
        this.player = player;
        this.food = food;
        this.maxFood = 20;
        this.saturation = saturation;
    }

    /**
     * Gets the player associated with this food manager.
     *
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the current food level.
     *
     * @return The food level (0-20)
     */
    public int getFood() {
        return food;
    }

    /**
     * Sets the food level and saturation.
     *
     * @param food       The new food level
     * @param saturation The new saturation level
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

    /**
     * Gets the maximum food level (always 20).
     *
     * @return The maximum food level
     */
    public int getMaxFood() {
        return maxFood;
    }

    /**
     * Gets the current saturation level.
     *
     * @return The saturation level
     */
    public float getSaturation() {
        return saturation;
    }

    /**
     * Sets the saturation level.
     *
     * @param saturation The new saturation value
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
     * Adds food and saturation from an item.
     *
     * @param item The item to consume
     */
    public void addFood(Item item) {
        this.addFood(item.getNutrition(), item.getSaturation());
    }

    /**
     * Sets the food level, keeping the current saturation.
     *
     * @param food The new food level
     */
    public void setFood(int food) {
        this.setFood(food, -1);
    }

    /**
     * Adds food and saturation values.
     *
     * @param food       Amount of food to add
     * @param saturation Amount of saturation to add
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
     * Sends a specific food level to the player.
     *
     * @param food The food level to send
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

    /**
     * Checks if the player is hungry (food < maxFood).
     *
     * @return True if hungry
     */
    public boolean isHungry() {
        return food < maxFood;
    }

    /**
     * Gets the current exhaustion value.
     *
     * @return The exhaustion value
     */
    public double getExhaustion() {
        return exhaustion;
    }

    /**
     * Sets the exhaustion value. If exhaustion exceeds 4, reduces saturation or food.
     *
     * @param exhaustion The new exhaustion value
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
     * Increases exhaustion by a given amount, applying food/saturation reduction if needed.
     *
     * @param amount The amount of exhaustion to add
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

    /**
     * Gets the internal food tick timer.
     *
     * @return The food tick timer
     */
    public int getFoodTickTimer() {
        return foodTickTimer;
    }

    /**
     * Resets the food, saturation, exhaustion, and timer to default values.
     */
    public void reset() {
        this.food = 20;
        this.saturation = 20;
        this.exhaustion = 0;
        this.foodTickTimer = 0;
        this.sendFood();
    }

    /**
     * Updates the food system for the player. Handles healing, starvation, and food tick logic.
     *
     * @param tickDiff The number of ticks since the last update
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

    /**
     * Checks if the food system is enabled for this player.
     *
     * @return True if enabled
     */
    public boolean isEnabled() {
        return !(player.isCreative() || player.isFlying() || player.isSpectator()) && this.enabled;
    }

    /**
     * Enables or disables the food system for this player.
     *
     * @param enabled True to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
