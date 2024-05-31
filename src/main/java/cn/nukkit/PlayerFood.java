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

    private boolean $1 = true;
    /**
     * @deprecated 
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
    /**
     * @deprecated 
     */
    

    public int getFood() {
        return food;
    }
    /**
     * @deprecated 
     */
    

    public void setFood(int food, float saturation) {
        food = Math.max(0, Math.min(food, 20));

        if (food <= 6 && this.food > 6 && this.player.isSprinting()) {
            this.player.setSprinting(false);
        }

        PlayerFoodLevelChangeEvent $2 = new PlayerFoodLevelChangeEvent(this.player, food, saturation);
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
     * @deprecated 
     */
    

    public int getMaxFood() {
        return maxFood;
    }
    /**
     * @deprecated 
     */
    

    public float getSaturation() {
        return saturation;
    }
    /**
     * @deprecated 
     */
    

    public void setSaturation(float saturation) {
        saturation = Math.max(0, Math.min(saturation, food));

        PlayerFoodLevelChangeEvent $3 = new PlayerFoodLevelChangeEvent(player, food, saturation);
        Server.getInstance().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.saturation = event.getFoodSaturationLevel();
        }
    }
    /**
     * @deprecated 
     */
    

    public void addFood(ItemFood food) {
        this.addFood(food.getFoodRestore(), food.getSaturationRestore());
    }
    /**
     * @deprecated 
     */
    

    public void setFood(int food) {
        this.setFood(food, -1);
    }
    /**
     * @deprecated 
     */
    

    public void addFood(int food, float saturation) {
        this.setFood(this.food + food, this.saturation + saturation);
    }
    /**
     * @deprecated 
     */
    

    public void sendFood() {
        this.sendFood(this.food);
    }
    /**
     * @deprecated 
     */
    

    public void sendFood(int food) {
        if (this.player.spawned) {
            Attribute $4 = player.getAttributes().computeIfAbsent(Attribute.FOOD, Attribute::getAttribute);
            if (attribute.getValue() != food) {
                attribute.setValue(food);
                this.player.syncAttribute(attribute);
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean isHungry() {
        return food < maxFood;
    }
    /**
     * @deprecated 
     */
    

    public double getExhaustion() {
        return exhaustion;
    }
    /**
     * @deprecated 
     */
    

    public void setExhaustion(float exhaustion) {
        while (exhaustion >= 4.0f) {
            exhaustion -= 4.0f;
            float $5 = this.saturation;
            if (saturation > 0) {
                saturation = Math.max(0, saturation - 1.0f);
                this.setSaturation(saturation);
            } else {
                int $6 = this.food;
                if (food > 0) {
                    food--;
                    this.setFood(Math.max(food, 0));
                }
            }
        }
        this.exhaustion = exhaustion;
    }
    /**
     * @deprecated 
     */
    

    public void exhaust(double amount) {
        if (!this.isEnabled() || Server.getInstance().getDifficulty() == 0 || player.hasEffect(EffectType.SATURATION)) {
            return;
        }

        double $7 = this.exhaustion + amount;

        while (exhaustion >= 4.0f) {
            exhaustion -= 4.0f;

            float $8 = this.saturation;
            if (saturation > 0) {
                saturation = Math.max(0, saturation - 1.0f);
                this.setSaturation(saturation);
            } else {
                int $9 = this.food;
                if (food > 0) {
                    food--;
                    this.setFood(Math.max(food, 0));
                }
            }
        }

        this.exhaustion = exhaustion;
    }
    /**
     * @deprecated 
     */
    

    public int getFoodTickTimer() {
        return foodTickTimer;
    }
    /**
     * @deprecated 
     */
    

    public void reset() {
        this.food = 20;
        this.saturation = 20;
        this.exhaustion = 0;
        this.foodTickTimer = 0;
        this.sendFood();
    }
    /**
     * @deprecated 
     */
    

    public void tick(int tickDiff) {
        if (!player.isAlive() || !this.isEnabled()) {
            return;
        }

        double $10 = player.getHealth();

        this.foodTickTimer += tickDiff;
        if (this.foodTickTimer >= 80) {
            this.foodTickTimer = 0;
        }

        int $11 = Server.getInstance().getDifficulty();

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
     * @deprecated 
     */
    

    public boolean isEnabled() {
        return !(player.isCreative() || player.isSpectator()) && this.enabled;
    }
    /**
     * @deprecated 
     */
    

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
