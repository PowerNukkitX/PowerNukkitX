/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockIronBlock;
import cn.nukkit.block.BlockPumpkin;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemIronIngot;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author joserobjr
 * @since 2021-01-13
 */
public class EntityIronGolem extends EntityGolem {
    @Override
    @NotNull
    public String getIdentifier() {
        return IRON_GOLEM;
    }

    public EntityIronGolem(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    private boolean attackingPlayer = false;

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.2f, 40, true, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                not(any(
                                        entity -> entity.getServer().getDifficulty() == 0,
                                        all(
                                                entity -> attackingPlayer = getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) instanceof Player,
                                                entity -> hasOwner(false)
                                        )))
                        ), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 0.2f, 40, true, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY),
                                entity -> attackTarget(getMemoryStorage().get(CoreMemoryTypes.NEAREST_SHARED_ENTITY)),
                                not(entity -> attackingPlayer = false)
                        ), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestEntitySensor(EntityMob.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 16, 0)),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public String getOriginalName() {
        return "Iron Golem";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("irongolem", "mob");
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 2.9f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(100);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.25f);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.syncAttribute(getHealthAttribute());
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        this.syncAttribute(getHealthAttribute());
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item instanceof ItemIronIngot && getHealthCurrent() <= getHealthMax() * 0.75f) {
            this.level.addSound(this, Sound.MOB_IRONGOLEM_REPAIR);
            if(player.getGamemode() != Player.CREATIVE) player.getInventory().getItemInMainHand().decrement(1);
            heal(25);
        }
        return super.onInteract(player, item);
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        // Item drops
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int flowerAmount = random.nextInt(3);
        Item[] drops;
        if (flowerAmount > 0) {
            drops = new Item[2];
            drops[1] = Item.get(BlockID.POPPY, 0, flowerAmount);
        } else {
            drops = new Item[1];
        }
        drops[0] = Item.get(ItemID.IRON_INGOT, 0, random.nextInt(3, 6));
        return drops;
    }


    private Attribute getHealthAttribute() {
        return Attribute.getAttribute(Attribute.HEALTH).setMaxValue(getHealthMax()).setValue(this.health < getHealthMax() ? this.health : getHealthMax());
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        float health = getHealthCurrent();
        if (!super.attack(source)) return false;
        for (int i : new int[]{74, 50, 25}) {
            if (health > i && getHealthCurrent() <= i) {
                this.level.addSound(this, Sound.MOB_IRONGOLEM_CRACK);
            }
        }
        return true;
    }

    @Override
    public void setHealthCurrent(float health) {
        super.setHealthCurrent(health);
        syncAttribute(getHealthAttribute());
    }

    @Override
    public float getDiffHandDamage(int difficulty) {
        if (attackingPlayer) {
            switch (this.getServer().getDifficulty()) {
                case 1:
                    return ThreadLocalRandom.current().nextFloat(4.5f, 11.5f);
                case 2:
                    return ThreadLocalRandom.current().nextFloat(7.5f, 21.5f);
                case 3:
                    return ThreadLocalRandom.current().nextFloat(11.5f, 32.25f);
            }
        }
        return ThreadLocalRandom.current().nextFloat(7.5f, 11.75f);
    }

    public static void checkAndSpawnGolem(Block block, Player player) {
        if (block.getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) {
            if (block instanceof BlockPumpkin) {
                faces:
                for (BlockFace blockFace : BlockFace.values()) {
                    for (int i = 1; i <= 2; i++) {
                        if (!(block.getSide(blockFace, i) instanceof BlockIronBlock)) {
                            continue faces;
                        }
                    }
                    faces1:
                    for (BlockFace face : Set.of(BlockFace.UP, BlockFace.NORTH, BlockFace.EAST)) {
                        for (int i = -1; i <= 1; i++) {
                            if (!(block.getSide(blockFace).getSide(face, i) instanceof BlockIronBlock)) {
                                continue faces1;
                            }
                        }
                        for (int i = 0; i <= 2; i++) {
                            Block location = block.getSide(blockFace, i);
                            block.level.setBlock(location, Block.get(Block.AIR));
                            block.level.addParticle(new DestroyBlockParticle(location.add(0.5, 0.5, 0.5), block));
                            block.level.getVibrationManager().callVibrationEvent(new VibrationEvent(null, location.add(0.5, 0.5, 0.5), VibrationType.BLOCK_DESTROY));
                        }
                        for (int i = -1; i <= 1; i++) {
                            Block location = block.getSide(blockFace).getSide(face, i);
                            block.level.setBlock(location, Block.get(Block.AIR));
                            block.level.addParticle(new DestroyBlockParticle(location.add(0.5, 0.5, 0.5), block));
                            block.level.getVibrationManager().callVibrationEvent(new VibrationEvent(null, location.add(0.5, 0.5, 0.5), VibrationType.BLOCK_DESTROY));

                        }
                        Block pos = block.getSide(blockFace, 2);
                        NbtMap nbt = NbtMap.builder()
                                .putList("Pos", NbtType.DOUBLE, Arrays.asList(pos.x + 0.5, pos.y, pos.z + 0.5)
                                ).putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0)
                                ).putList("Rotation", NbtType.FLOAT, Arrays.asList(0f, 0f)
                                ).build();
                        Entity irongolem = Entity.createEntity(EntityID.IRON_GOLEM, block.level.getChunk(block.getChunkX(), block.getChunkZ()), nbt);
                        irongolem.spawnToAll();
                        if (irongolem instanceof EntityIronGolem golem) {
                            if (player != null) {
                                golem.setOwnerName(player.getName());
                            }
                        }
                        return;
                    }
                }
            }
        }
    }
}
