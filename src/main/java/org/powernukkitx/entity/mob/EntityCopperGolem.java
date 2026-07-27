package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockChest;
import org.powernukkitx.block.BlockCopperBlock;
import org.powernukkitx.block.BlockPumpkin;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.copper.chest.BlockCopperChest;
import org.powernukkitx.block.copper.golem.BlockOxidizedCopperGolemStatue;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.MinecraftCardinalDirection;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.DistanceEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.MoveToTargetExecutor;
import org.powernukkitx.entity.ai.executor.coppergolem.ChestInteractionFailExecutor;
import org.powernukkitx.entity.ai.executor.coppergolem.PutInChestExecutor;
import org.powernukkitx.entity.ai.executor.coppergolem.TakeFromCopperChestExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.BlockSensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.data.property.BooleanEntityProperty;
import org.powernukkitx.entity.data.property.EntityProperty;
import org.powernukkitx.entity.data.property.EnumEntityProperty;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.inventory.EntityEquipmentInventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemHoneycomb;
import org.powernukkitx.item.ItemShears;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.entity.condition.Condition;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.particle.DestroyBlockParticle;
import org.powernukkitx.level.particle.ScrapeParticle;
import org.powernukkitx.level.particle.WaxOffParticle;
import org.powernukkitx.level.particle.WaxOnParticle;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.Utils;
import org.powernukkitx.utils.random.NukkitRandom;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.Locale;
import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author Buddelbubi
 * @since 2025/11/18
 */
public class EntityCopperGolem extends EntityGolem implements InventoryHolder {

    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
            new EnumEntityProperty("minecraft:chest_interaction", new String[]{
                    "none",
                    "take",
                    "take_fail",
                    "put",
                    "put_fail"
            }, "none", true),
            new BooleanEntityProperty("minecraft:has_flower", false, true),
            new EnumEntityProperty("minecraft:oxidation_level", new String[]{
                    "unoxidized",
                    "exposed",
                    "weathered",
                    "oxidized"
            }, "unoxidized", true),
    };

    public EntityCopperGolem(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected NukkitRandom random = new NukkitRandom();
    protected int weatherTick = -1;
    @Getter(onMethod_ = {@Override})
    private EntityEquipmentInventory inventory;


    @Override
    public @NotNull String getIdentifier() {
        return COPPER_GOLEM;
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .behaviors(
                        new Behavior(new ChestInteractionFailExecutor(), entity -> {
                            String interaction = getEnumEntityProperty(PROPERTIES[0].getIdentifier());
                            return "take_fail".equals(interaction) || "put_fail".equals(interaction);
                        }, 8, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), all(
                            entity -> entity.getMemoryStorage().get(CoreMemoryTypes.FORCE_WANDERING) > 0,
                            entity -> {
                                entity.getMemoryStorage().put(CoreMemoryTypes.FORCE_WANDERING,
                                    entity.getMemoryStorage().get(CoreMemoryTypes.FORCE_WANDERING) - 1);
                                return true;
                            }
                        ), 7, 1),
                        new Behavior(new PutInChestExecutor(), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK),
                                not(entity -> inventory.getItemInHand().isNull()),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_BLOCK, 3.1f)
                        ), 6, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK, 0.2f, true), all(
                                not(entity -> inventory.getItemInHand().isNull()),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK)
                        ), 5, 1),
                        new Behavior(new TakeFromCopperChestExecutor(), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK_2),
                                entity -> inventory.getItemInHand().isNull(),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_BLOCK_2, 3.1f)
                        ), 4, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK_2, 0.2f, true), all(
                                entity -> inventory.getItemInHand().isNull(),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK_2)
                        ), 3, 1),
                        new Behavior(entity -> {
                            entity.getMemoryStorage().put(CoreMemoryTypes.FORCE_WANDERING, 7 * 20);
                            entity.getMemoryStorage().get(CoreMemoryTypes.COPPER_CHESTS).clear();
                            return true;
                        }, all(
                                entity -> inventory.getItemInHand().isNull(),
                                new MemoryCheckEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK_2),
                                entity -> !entity.getMemoryStorage().get(CoreMemoryTypes.COPPER_CHESTS).isEmpty()
                        ), 2, 1),
                        new Behavior(entity -> {
                            entity.getMemoryStorage().put(CoreMemoryTypes.FORCE_WANDERING, 7 * 20);
                            entity.getMemoryStorage().get(CoreMemoryTypes.CHESTS).clear();
                            return true;
                        }, all(
                                not(entity -> inventory.getItemInHand().isNull()),
                                new MemoryCheckEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK)
                        ), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), none(), 1, 1)
                )
                .sensors(
                        new BlockSensor(BlockChest.class, CoreMemoryTypes.NEAREST_BLOCK, 32, 8, 20, new ChestCondition(false)),
                        new BlockSensor(BlockCopperChest.class, CoreMemoryTypes.NEAREST_BLOCK_2, 32, 8, 20, new ChestCondition(true))
                )
                .controllers(new WalkController(), new LookController(true, true))
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        final CompoundTag nbtMap = this.getNbt();
        if (!nbtMap.contains("oxidationLevel")) {
            this.nbt.putString("oxidationLevel", Oxidation.UNOXIDIZED.getName());
        }
        this.inventory = new EntityEquipmentInventory(this);

        if (nbtMap.contains("Mainhand")) {
            this.inventory.setItemInHand(ItemHelper.read(nbtMap.getCompound("Mainhand")), true);
        }
        setOxidation(Oxidation.valueOf(nbtMap.getString("oxidationLevel").toUpperCase(Locale.ROOT)));
        this.weatherTick = nbtMap.getInt("weatheredTick");
        setEnumEntityProperty(PROPERTIES[0].getIdentifier(), "none");
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        this.getInventory().sendContents(player);
    }

    @Override
    public double getFloatingForceFactor() {
        return 0;
    }

    @Override
    public String getOriginalName() {
        return "Copper Golem";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("coppergolem", "mob");
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.DROWNING) return false;
        if (source.getCause() == EntityDamageEvent.DamageCause.FALL) return false;
        if (super.attack(source)) {
            if (source.getCause() == EntityDamageEvent.DamageCause.LIGHTNING && !isWaxed()) {
                setOxidation(Oxidation.VALUES[getOxidation().ordinal() - 1]);
                weatherTick = -1;
            }
            return true;
        }
        return false;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putCompound("Mainhand", ItemHelper.write(this.getInventory().getItem(0), null))
                .putString("oxidationLevel", this.getOxidation().getName())
                .putInt("weatheredTick", this.weatherTick);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item instanceof ItemShears) {
            if (hasFlower()) {
                this.setFlower(false);
                this.level.addLevelSoundEvent(this, SoundEvent.SHEAR);
                if (player.getGamemode() != Player.CREATIVE)
                    player.getInventory().getItemInMainHand().setDamage(item.getDamage() + 1);
                this.level.dropItem(this.add(0, this.getEyeHeight(), 0), Item.get(Block.POPPY));
            }
        } else if (item instanceof ItemHoneycomb) {
            if (!isWaxed()) {
                if (player.getGamemode() != Player.CREATIVE)
                    player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                setWaxed(true);
                getLevel().addSound(this, Sound.COPPER_WAX_ON);
                Server.broadcastPacket(getViewers().values(), new WaxOnParticle(getVector3()).encode()[0]);
            }
        } else if (item.isAxe()) {
            if (isWaxed()) {
                if (player.getGamemode() != Player.CREATIVE)
                    player.getInventory().getItemInMainHand().setDamage(item.getDamage() + 1);
                setWaxed(false);
                getLevel().addSound(this, Sound.SCRAPE);
                Server.broadcastPacket(getViewers().values(), new WaxOffParticle(this).encode()[0]);
            } else if (getOxidation() != Oxidation.UNOXIDIZED) {
                setOxidation(Oxidation.VALUES[getOxidation().ordinal() - 1]);
                getLevel().addSound(this, Sound.SCRAPE);
                Server.broadcastPacket(getViewers().values(), new ScrapeParticle(this).encode()[0]);
            }
        }
        return super.onInteract(player, item);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (!isWaxed()) {
            if (getOxidation() != Oxidation.OXIDIZED) {
                if (weatherTick == -1) {
                    weatherTick = NukkitMath.randomRange(random, 504000, 552000);
                } else {
                    if (weatherTick <= 0) {
                        setOxidation(Oxidation.VALUES[getOxidation().ordinal() + 1]);
                        weatherTick = -1;
                    } else weatherTick--;
                }
            } else if (getLevelBlock().isAir() && random.nextFloat() <= 0.0058F) {
                turnToStatue();
            }
        }
        return super.onUpdate(currentTick);
    }

    protected void turnToStatue() {
        BlockFace face = BlockFace.fromHorizontalAngle(getYaw());
        BlockState statue = BlockOxidizedCopperGolemStatue.PROPERTIES.getBlockState(
                CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION.createValue(MinecraftCardinalDirection.fromBlockFace(face))
        );
        this.close();
        this.level.setBlockStateAt(getFloorX(), getFloorY(), getFloorZ(), statue);
    }

    public void setWaxed(boolean waxed) {
        this.weatherTick = waxed ? -2 : -1;
    }

    public boolean isWaxed() {
        return this.weatherTick == -2;
    }

    public void setOxidation(Oxidation oxidation) {
        this.setEnumEntityProperty(PROPERTIES[2].getIdentifier(), oxidation.getName());
        this.sendData(this.getViewers().values().toArray(Player[]::new));
    }

    public Oxidation getOxidation() {
        return Oxidation.valueOf(this.getEnumEntityProperty(PROPERTIES[2].getIdentifier()).toUpperCase(Locale.ROOT));
    }

    public void setFlower(boolean flower) {
        this.setBooleanEntityProperty(PROPERTIES[1].getIdentifier(), flower);
        this.sendData(this.getViewers().values().toArray(Player[]::new));
    }

    public boolean hasFlower() {
        return this.getBooleanEntityProperty(PROPERTIES[1].getIdentifier());
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    public float getWidth() {
        return 0.49f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(12);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.2f);
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        int amount = Utils.rand(1, 3 + looting);

        return new Item[]{
                Item.get(Item.COPPER_INGOT, 0, amount),
                getInventory().getItemInHand()
        };
    }

    public static void checkAndSpawnGolem(Block block) {
        if (block.getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) {
            if (block instanceof BlockPumpkin pumpkin) {
                for (BlockFace blockFace : BlockFace.values()) {
                    if (pumpkin.getSide(blockFace) instanceof BlockCopperBlock copperBlock) {
                        block.level.setBlock(copperBlock, Block.get(copperBlock.getId().replace("_block", "") + "_chest"));
                        block.level.addParticle(new DestroyBlockParticle(copperBlock.add(0.5, 0.5, 0.5), block));
                        block.level.getVibrationManager().callVibrationEvent(new VibrationEvent(null, copperBlock.add(0.5, 0.5, 0.5), VibrationType.BLOCK_DESTROY));
                        CompoundTag nbt = Entity.getDefaultNBT(pumpkin.add(0.5, 0, 0.5f));
                        EntityCopperGolem copperGolem = (EntityCopperGolem) Entity.createEntity(EntityID.COPPER_GOLEM, block.level.getChunk(block.getChunkX(), block.getChunkZ()), nbt);
                        copperGolem.setOxidation(Oxidation.getGolemOxidation(copperBlock.getOxidizationLevel()));
                        copperGolem.spawnToAll();
                        block.level.setBlock(block, BlockAir.STATE.toBlock());
                        return;
                    }
                }
            }
        }
    }

    public enum Oxidation {
        UNOXIDIZED,
        EXPOSED,
        WEATHERED,
        OXIDIZED;

        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static Oxidation getGolemOxidation(OxidizationLevel level) {
            return VALUES[level.ordinal()];
        }

        public OxidizationLevel getOxidationLevel() {
            return OxidizationLevel.values()[this.ordinal()];
        }

        public final static Oxidation[] VALUES = values();
    }

    protected class ChestCondition extends Condition {

        private final boolean copper;

        public ChestCondition(boolean copper) {
            super("coppergolem:chest");
            this.copper = copper;
        }

        @Override
        public boolean evaluate(Block block) {
            if (block instanceof BlockChest chest) {
                if (copper ^ (chest instanceof BlockCopperChest)) return false;
                MemoryType<ObjectList<InventoryHolder>> memory = copper ? CoreMemoryTypes.COPPER_CHESTS : CoreMemoryTypes.CHESTS;
                return getMemoryStorage().get(memory).stream().noneMatch(b -> b.getInventory() == chest.getOrCreateBlockEntity().getInventory());
            }
            return false;
        }
    }

}
