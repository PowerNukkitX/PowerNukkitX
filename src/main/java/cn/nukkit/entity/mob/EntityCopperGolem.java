package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockCopperBlock;
import cn.nukkit.block.BlockPumpkin;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.copper.chest.BlockCopperChest;
import cn.nukkit.block.copper.golem.BlockOxidizedCopperGolemStatue;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.block.property.enums.OxidizationLevel;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.coppergolem.PutInChestExecutor;
import cn.nukkit.entity.ai.executor.coppergolem.TakeFromCopperChestExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.BlockSensor;
import cn.nukkit.entity.data.property.BooleanEntityProperty;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.entity.data.property.EnumEntityProperty;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityEquipmentInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemHoneycomb;
import cn.nukkit.item.ItemShears;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Sound;
import cn.nukkit.level.entity.condition.Condition;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.ScrapeParticle;
import cn.nukkit.level.particle.WaxOffParticle;
import cn.nukkit.level.particle.WaxOnParticle;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import cn.nukkit.network.protocol.types.LevelSoundEvent;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.random.NukkitRandom;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

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
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), any(
                                all(
                                        entity -> entity.getMemoryStorage().get(CoreMemoryTypes.FORCE_WANDERING) > 0,
                                        entity -> {
                                            entity.getMemoryStorage().put(CoreMemoryTypes.FORCE_WANDERING,
                                                    entity.getMemoryStorage().get(CoreMemoryTypes.FORCE_WANDERING)-1);
                                            return true;
                                        }
                                )
                        ), 7, 1),
                        new Behavior(new PutInChestExecutor(), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK),
                                not(entity -> inventory.getItemInHand().isNull()),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_BLOCK, 2.1f)
                        ),6, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK, 0.2f, true), all(
                                not(entity -> inventory.getItemInHand().isNull()),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK)
                        ), 5, 1),
                        new Behavior(new TakeFromCopperChestExecutor(), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK_2),
                                entity -> inventory.getItemInHand().isNull(),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_BLOCK_2, 2.1f)
                        ),4, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK_2, 0.2f, true), all(
                                entity -> inventory.getItemInHand().isNull(),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK_2)
                        ), 3, 1),
                        new Behavior(entity -> {
                            entity.getMemoryStorage().put(CoreMemoryTypes.FORCE_WANDERING, 7*20);
                            return true;
                        }, all(
                                not(entity -> inventory.getItemInHand().isNull()),
                                new MemoryCheckEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK)
                        ), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new BlockSensor(BlockChest.class, CoreMemoryTypes.NEAREST_BLOCK, 32, 8, 20, new ChestCondition(false)),
                        new BlockSensor(BlockCopperChest.class, CoreMemoryTypes.NEAREST_BLOCK_2, 32, 8, 20, new ChestCondition(true))
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(12);
        super.initEntity();
        if(!namedTag.containsString("oxidationLevel")) {
            namedTag.putString("oxidationLevel", Oxidation.UNOXIDIZED.getName());
        }
        this.inventory = new EntityEquipmentInventory(this);

        if (this.namedTag.contains("Mainhand")) {
            this.inventory.setItemInHand(NBTIO.getItemHelper(this.namedTag.getCompound("Mainhand")), true);
        }
        setOxidation(Oxidation.valueOf(namedTag.getString("oxidationLevel").toUpperCase()));
        this.weatherTick = namedTag.getInt("weatheredTick");
        setEnumEntityProperty(PROPERTIES[0].getIdentifier(), "none");
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
        if(source.getCause() == EntityDamageEvent.DamageCause.DROWNING) return false;
        if(source.getCause() == EntityDamageEvent.DamageCause.FALL) return false;
        if(super.attack(source)) {
            if(source.getCause() == EntityDamageEvent.DamageCause.LIGHTNING && !isWaxed()) {
                setOxidation(Oxidation.VALUES[getOxidation().ordinal()-1]);
                weatherTick = -1;
            }
            return true;
        }
        return false;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.put("Mainhand", NBTIO.putItemHelper(this.getInventory().getItem(0)));
        this.namedTag.putString("oxidationLevel", this.getOxidation().getName());
        this.namedTag.putInt("weatheredTick", this.weatherTick);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if(item instanceof ItemShears) {
            if(hasFlower()) {
                this.setFlower(false);
                this.level.addLevelSoundEvent(this, LevelSoundEvent.SHEAR);
                if(player.getGamemode() != Player.CREATIVE) player.getInventory().getItemInHand().setDamage(item.getDamage() + 1);
                this.level.dropItem(this.add(0, this.getEyeHeight(), 0), Item.get(Block.POPPY));
            }
        } else if(item instanceof ItemHoneycomb) {
            if(!isWaxed()) {
                if(player.getGamemode() != Player.CREATIVE) player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                setWaxed(true);
                getLevel().addSound(this, Sound.COPPER_WAX_ON);
                Server.broadcastPacket(getViewers().values(), new WaxOnParticle(getVector3()).encode()[0]);
            }
        } else if(item.isAxe()) {
            if(isWaxed()) {
                if(player.getGamemode() != Player.CREATIVE) player.getInventory().getItemInHand().setDamage(item.getDamage() + 1);
                setWaxed(false);
                getLevel().addSound(this, Sound.SCRAPE);
                Server.broadcastPacket(getViewers().values(), new WaxOffParticle(this).encode()[0]);
            } else if(getOxidation() != Oxidation.UNOXIDIZED) {
                setOxidation(Oxidation.VALUES[getOxidation().ordinal()-1]);
                getLevel().addSound(this, Sound.SCRAPE);
                Server.broadcastPacket(getViewers().values(), new ScrapeParticle(this).encode()[0]);
            }
        }
        return super.onInteract(player, item);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(!isWaxed()) {
            if(getOxidation() != Oxidation.OXIDIZED) {
                if(weatherTick == -1) {
                    weatherTick = NukkitMath.randomRange(random, 504000, 552000);
                } else {
                    if(weatherTick <= 0) {
                        setOxidation(Oxidation.VALUES[getOxidation().ordinal()+1]);
                        weatherTick = -1;
                    } else weatherTick--;
                }
            } else if(getLevelBlock().isAir() && random.nextFloat() <=  0.0058F) {
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
        return Oxidation.valueOf(this.getEnumEntityProperty(PROPERTIES[2].getIdentifier()).toUpperCase());
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
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.COPPER_INGOT, 0, Utils.rand(1, 3)), getInventory().getItemInHand()};
    }

    public static void checkAndSpawnGolem(Block block) {
        if(block.getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) {
            if(block instanceof BlockPumpkin pumpkin) {
                for(BlockFace blockFace : BlockFace.values()) {
                    if(pumpkin.getSide(blockFace) instanceof BlockCopperBlock copperBlock) {
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
            return name().toLowerCase();
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
            if(block instanceof BlockChest chest) {
                if(copper ^ (chest instanceof BlockCopperChest)) return false;
                MemoryType<ObjectList<InventoryHolder>> memory = copper ? CoreMemoryTypes.COPPER_CHESTS : CoreMemoryTypes.CHESTS;
                return getMemoryStorage().get(memory).stream().noneMatch(b -> b.getInventory() == chest.getOrCreateBlockEntity().getInventory());
            }
            return false;
        }
    }

}
