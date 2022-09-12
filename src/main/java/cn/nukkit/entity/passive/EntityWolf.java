package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.AllMatchEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.EntityBreedingExecutor;
import cn.nukkit.entity.ai.executor.InLoveExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.RandomRoamExecutor;
import cn.nukkit.entity.ai.executor.entity.WolfLookPlayerExecutor;
import cn.nukkit.entity.ai.executor.entity.WolfMoveToOwnerExecutor;
import cn.nukkit.entity.ai.memory.*;
import cn.nukkit.entity.ai.route.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.WolfNearestFeedingPlayerSensor;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author BeYkeRYkt (Nukkit Project)
 * <br>
 * @author Cool_Loong (PowerNukkitX Project)
 * 投喂肉可以繁殖
 * 攻击会红眼反击你
 */
public class EntityWolf extends EntityWalkingAnimal implements EntityTamable {
    public static final int NETWORK_ID = 14;
    private Player owner = null;
    private String ownerName = "";
    private boolean sitting = false;
    private boolean angry = false;
    private DyeColor collarColor = DyeColor.RED;//项圈颜色
    private IBehaviorGroup behaviorGroup;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public IBehaviorGroup getBehaviorGroup() {
        if (behaviorGroup == null) {
            behaviorGroup = new BehaviorGroup(
                    this.tickSpread,
                    Set.of(
                            //用于刷新InLove状态的核心行为
                            new Behavior(
                                    new InLoveExecutor(400),
                                    new AllMatchEvaluator(
                                            new PassByTimeEvaluator<>(PlayerBreedingMemory.class, 0, 400),
                                            new PassByTimeEvaluator<>(InLoveMemory.class, 6000, Integer.MAX_VALUE, true)
                                    ),
                                    1, 1
                            )
                    ),
                    Set.of(
                            new Behavior(new RandomRoamExecutor(this.movementSpeed, 12, 40, true, 100, true, 10), new AllMatchEvaluator(
                                    new PassByTimeEvaluator<>(AttackMemory.class, 0, 100),
                                    entity -> {
                                        if (entity instanceof EntityWolf entityWolf) {
                                            return !entityWolf.hasOwner(false);
                                        } else return true;
                                    }
                            ), 6, 1),
                            new Behavior(new WolfLookPlayerExecutor(NearestFeedingPlayerMemory.class), new MemoryCheckNotEmptyEvaluator(NearestFeedingPlayerMemory.class), 6, 1),
                            new Behavior(new WolfMoveToOwnerExecutor(0.5f, true, 15), entity -> {
                                if (entity instanceof EntityWolf entityWolf && entityWolf.hasOwner()) {
                                    var player = entityWolf.getServer().getPlayer(entityWolf.getOwnerName());
                                    if (player == null) return false;
                                    var distanceSquared = entity.distanceSquared(player);
                                    return distanceSquared >= 100;
                                } else return false;
                            }, 5, 1),
                            new Behavior(new EntityBreedingExecutor<>(EntitySheep.class, 16, 100, 0.5f), entity -> entity.getMemoryStorage().get(InLoveMemory.class).isInLove(), 4, 1),
                            new Behavior(new LookAtTargetExecutor(NearestPlayerMemory.class, 100), new ProbabilityEvaluator(4, 10), 1, 1, 100),
                            new Behavior(new RandomRoamExecutor(this.movementSpeed, 12, 250, false, -1, true, 10),
                                    entity -> {
                                        if (entity instanceof EntityWolf entityWolf) {
                                            return !entityWolf.hasOwner(false);
                                        } else return true;
                                    }, 1, 1)
                    ),
                    Set.of(new WolfNearestFeedingPlayerSensor(7, 0), new NearestPlayerSensor(8, 0, 20)),
                    Set.of(new WalkController(), new LookController(true, true)),
                    new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)
            );
        }
        return behaviorGroup;
    }

    public EntityWolf(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.425f;
        }
        return 0.8f;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Wolf";
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(8);

        if (this.namedTag.contains("Sitting")) {
            if (this.namedTag.getBoolean("Sitting")) {
                this.sitting = true;
                this.setDataFlag(DATA_FLAGS, DATA_FLAG_SITTING, true);
            }
        }

        if (this.namedTag.contains("Angry")) {
            if (this.namedTag.getBoolean("Angry")) {
                this.angry = true;
                this.setDataFlag(DATA_FLAGS, DATA_FLAG_ANGRY, true);
            }
        }

        if (this.namedTag.contains("CollarColor")) {
            var collarColor = DyeColor.getByDyeData(this.namedTag.getByte("CollarColor"));
            if (collarColor == null) {
                this.collarColor = DyeColor.RED;
                this.setDataProperty(new ByteEntityData(DATA_COLOUR, DyeColor.RED.getWoolData()));
            } else {
                this.collarColor = collarColor;
                this.setDataProperty(new ByteEntityData(DATA_COLOUR, collarColor.getWoolData()));
            }
        }
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean("Angry", this.angry);
        this.namedTag.putByte("CollarColor", this.collarColor.getDyeData());
        this.namedTag.putBoolean("Sitting", this.sitting);
        this.namedTag.putString("OwnerName", this.ownerName);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        var result = super.onUpdate(currentTick);

        //initEntity的时候玩家还没连接，所以只能在onUpdate里面更新
        if (this.namedTag.contains("OwnerName") && this.owner == null) {
            String ownerName = namedTag.getString("OwnerName");
            if (ownerName != null && ownerName.length() > 0) {
                setOwnerName(ownerName);
            }
        }
        return result;
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.NAME_TAG && !player.isAdventure()) {
            return applyNameTag(player, item);
        }

        int healable = this.getHealableItem(item);
        if (item.getId() == ItemID.BONE) {
            if (!this.hasOwner() && !this.isAngry()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                if (Utils.rand(1, 3) == 3) {
                    EntityEventPacket packet = new EntityEventPacket();
                    packet.eid = this.getId();
                    packet.event = EntityEventPacket.TAME_SUCCESS;
                    player.dataPacket(packet);

                    this.setMaxHealth(20);
                    this.setHealth(20);
                    this.setOwnerName(player.getName());
                    this.setCollarColor(DyeColor.RED);
                    this.saveNBT();

                    this.getLevel().dropExpOrb(this, Utils.rand(1, 7));

                    return true;
                } else {
                    EntityEventPacket packet = new EntityEventPacket();
                    packet.eid = this.getId();
                    packet.event = EntityEventPacket.TAME_FAIL;
                    player.dataPacket(packet);
                }
            }
        } else if (item.getId() == Item.DYE) {
            if (this.hasOwner() && player.equals(this.getOwner())) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                this.setCollarColor(((ItemDye) item).getDyeColor());
                return true;
            }
        } else if (this.isBreedingItem(item)) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            this.getLevel().addSound(this, Sound.RANDOM_EAT);
            this.getLevel().addParticle(new ItemBreakParticle(this.add(0, getHeight() * 0.75F, 0), Item.get(item.getId(), 0, 1)));

            if (healable != 0) {
                this.setHealth(Math.max(this.getMaxHealth(), this.getHealth() + healable));
            }

            getMemoryStorage().get(PlayerBreedingMemory.class).setData(player);
            return true;
        } else if (this.hasOwner() && player.getName().equals(getOwnerName())) {
            this.setSitting(!this.isSitting());
            return true;
        }

        return false;
    }

    @Override
    public String getOwnerName() {
        return this.ownerName;
    }

    @Override
    public void setOwnerName(String playerName) {
        var player = getServer().getPlayerExact(playerName);
        if (player == null) return;
        this.owner = player;
        this.ownerName = playerName;
        this.setDataProperty(new LongEntityData(DATA_OWNER_EID, player.getId()));
        this.setTamed(true);
        this.namedTag.putString("OwnerName", this.ownerName);
    }

    @Nullable
    @Override
    public Player getOwner() {
        return this.owner;
    }

    @Override
    public boolean hasOwner() {
        return hasOwner(true);
    }

    public boolean hasOwner(boolean checkOnline) {
        if (checkOnline) {
            if (this.ownerName == null || this.ownerName.isEmpty()) return false;
            var owner = getServer().getPlayerExact(this.ownerName);
            return owner != null;
        } else {
            return this.ownerName != null && !this.ownerName.isEmpty();
        }
    }

    public boolean isSitting() {
        return this.sitting;
    }

    public void setSitting(boolean sit) {
        this.sitting = sit;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SITTING, sit);
        this.namedTag.putBoolean("Sitting", sit);
    }

    private void setTamed(boolean tamed) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_TAMED, tamed);
    }

    public boolean isAngry() {
        return this.angry;
    }

    public void setAngry(boolean angry) {
        this.angry = angry;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ANGRY, angry);
        this.namedTag.putBoolean("Angry", angry);
    }

    public void setCollarColor(DyeColor color) {
        this.collarColor = color;
        this.setDataProperty(new ByteEntityData(DATA_COLOUR, color.getWoolData()));
        this.namedTag.putByte("CollarColor", color.getDyeData());
    }

    @Override
    public boolean isBreedingItem(Item item) {
        return item.getId() == ItemID.RAW_CHICKEN ||
                item.getId() == ItemID.COOKED_CHICKEN ||
                item.getId() == ItemID.RAW_BEEF ||
                item.getId() == ItemID.COOKED_BEEF ||
                item.getId() == ItemID.RAW_MUTTON ||
                item.getId() == ItemID.COOKED_MUTTON ||
                item.getId() == ItemID.RAW_PORKCHOP ||
                item.getId() == ItemID.COOKED_PORKCHOP ||
                item.getId() == ItemID.RAW_RABBIT ||
                item.getId() == ItemID.COOKED_RABBIT ||
                item.getId() == ItemID.ROTTEN_FLESH;
    }

    /**
     * 获得可以治疗狼的物品的治疗量
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    public int getHealableItem(Item item) {
        return switch (item.getId()) {
            case ItemID.RAW_PORKCHOP, ItemID.RAW_BEEF, ItemID.RAW_RABBIT -> 3;
            case ItemID.COOKED_PORKCHOP, ItemID.COOKED_BEEF -> 8;
            case ItemID.RAW_FISH, ItemID.RAW_SALMON, ItemID.RAW_CHICKEN, ItemID.RAW_MUTTON -> 2;
            case ItemID.CLOWNFISH, ItemID.PUFFERFISH -> 1;
            case ItemID.COOKED_FISH, ItemID.COOKED_RABBIT -> 5;
            case ItemID.COOKED_SALMON, ItemID.COOKED_CHICKEN, ItemID.COOKED_MUTTON -> 6;
            case ItemID.ROTTEN_FLESH -> 4;
            case ItemID.RABBIT_STEW -> 10;
            default -> 0;
        };
    }
}
