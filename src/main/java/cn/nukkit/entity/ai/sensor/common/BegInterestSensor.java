package cn.nukkit.entity.ai.sensor.common;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.sensor.Sensor;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * 探测附近是否有该实体感兴趣的乞食对象，例如羊会向8格内持有小麦的玩家乞求食物。
 */
public class BegInterestSensor implements Sensor {
    private double distance = 8.0;
    @NotNull
    private Predicate<Entity> interestPredicate;

    public BegInterestSensor(double distance, @NotNull Predicate<Entity> interestPredicate) {
        this.distance = distance;
        this.interestPredicate = interestPredicate;
    }

    /**
     * 乞求手中有指定物品的玩家
     *
     * @param distance 最大距离
     * @param item     条件物品
     */
    public BegInterestSensor(double distance, @NotNull final Item item) {
        this.distance = distance;
        this.interestPredicate = entity -> {
            if (entity instanceof Player player) {
                return player.getInventory().getItemInHand().equals(item);
            }
            return false;
        };
    }

    /**
     * 乞求手中有指定物品的玩家
     *
     * @param item 条件物品
     */
    public BegInterestSensor(@NotNull final Item item) {
        this.interestPredicate = entity -> {
            if (entity instanceof Player player) {
                return player.getInventory().getItemInHand().equals(item);
            }
            return false;
        };
    }

    @Override
    public boolean shouldSense(int currentTick, EntityIntelligent entity) {
        return entity.level != null && Sensor.super.shouldSense(currentTick, entity);
    }

    @Override
    public void sense(int currentTick, EntityIntelligent entity) {
        var entities = entity.level.fastNearbyEntities(entity, entity, distance);
        var output = new ArrayList<Entity>(entities.size());
        for (var each : entities) {
            if (interestPredicate.test(each)) {
                output.add(each);
            }
        }
        entity.addMemory(BegInterestSensor.class, output);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @NotNull
    public Predicate<Entity> getInterestPredicate() {
        return interestPredicate;
    }

    public void setInterestPredicate(@NotNull Predicate<Entity> interestPredicate) {
        this.interestPredicate = interestPredicate;
    }
}
