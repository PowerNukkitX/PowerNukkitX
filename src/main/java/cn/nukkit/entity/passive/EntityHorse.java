package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.*;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.*;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.inventory.HorseInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author PikyCZ
 */
public class EntityHorse extends EntityAnimal implements EntityWalkable, EntityVariant, EntityMarkVariant, EntityRideable, EntityOwnable, InventoryHolder, EntityAgeable {

    public static final int NETWORK_ID = 23;
    private static final int[] VARIANTS = {0, 1, 2, 3, 4, 5, 6};
    private static final int[] MARK_VARIANTS = {0, 1, 2, 3, 4};
    private HorseInventory horseInventory;

    public EntityHorse(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.7f;
        }
        return 1.4f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.8f;
        }
        return 1.6f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(15);
        super.initEntity();

        this.horseInventory = new HorseInventory(this);
        ListTag<CompoundTag> inventoryTag;
        if (this.namedTag.containsList("Inventory")) {
            inventoryTag = this.namedTag.getList("Inventory", CompoundTag.class);
        } else {
            inventoryTag = new ListTag<>();
            this.namedTag.putList(inventoryTag);
        }
        if (inventoryTag.size() > 0 && inventoryTag.get(0) != null) {
            this.getInventory().setItem(0, NBTIO.getItemHelper(inventoryTag.get(0)));
        } else if (inventoryTag.size() > 1 && inventoryTag.get(1) != null) {
            this.getInventory().setItem(1, NBTIO.getItemHelper(inventoryTag.get(1)));
        }

        if (!hasVariant()) {
            this.setVariant(randomVariant());
        }
        if (!hasMarkVariant()) {
            this.setMarkVariant(randomMarkVariant());
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        ListTag<CompoundTag> inventoryTag = this.namedTag.getList("Inventory", CompoundTag.class);
        if (this.getInventory() != null) {
            Item item0 = this.getInventory().getItem(0);
            Item item1 = this.getInventory().getItem(1);
            if (item0.getId() != Item.AIR) {
                inventoryTag.remove(0);
                inventoryTag.add(NBTIO.putItemHelper(item0, 0));
            } else if (item1.getId() != Item.AIR) {
                inventoryTag.remove(1);
                inventoryTag.add(NBTIO.putItemHelper(item0, 1));
            }
        }
        this.namedTag.putList(inventoryTag);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER), getHorseArmor(), getSaddle()};
    }


    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Horse";
    }

    @Override
    public int[] getAllVariant() {
        return VARIANTS;
    }

    @Override
    public int[] getAllMarkVariant() {
        return MARK_VARIANTS;
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        //用于刷新InLove状态的核心行为
                        new Behavior(
                                new InLoveExecutor(400),
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_FEED_TIME, 0, 400),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_IN_LOVE_TIME, 6000, Integer.MAX_VALUE)
                                ),
                                1, 1
                        ),
                        //生长
                        new Behavior(
                                new AnimalGrowExecutor(),
                                //todo：Growth rate
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.ENTITY_SPAWN_TIME, 20 * 60 * 20, Integer.MAX_VALUE),
                                        entity -> entity instanceof EntityAnimal animal && animal.isBaby()
                                )
                                , 1, 1, 1200
                        )
                ),
                Set.of(
                        new Behavior(new HorseFlatRandomRoamExecutor(0.4f, 12, 40, true, 100, true, 10, 35), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.RIDER_NAME),
                                e -> !this.hasOwner(false)
                        ), 4, 1),
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ProbabilityEvaluator(4, 10), 1, 1, 100),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(new NearestFeedingPlayerSensor(8, 0), new NearestPlayerSensor(8, 0, 20)),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Nullable
    @Override
    public String getOwnerName() {
        String ownerName = EntityOwnable.super.getOwnerName();
        if (ownerName == null) {
            this.setDataProperty(new ByteEntityData(Entity.DATA_CONTAINER_TYPE, 0));
            this.setDataProperty(new IntEntityData(Entity.DATA_CONTAINER_BASE_SIZE, 0));
        } else {
            //添加两个metadata这个才能交互物品栏
            this.setDataProperty(new ByteEntityData(Entity.DATA_CONTAINER_TYPE, 12));
            this.setDataProperty(new IntEntityData(Entity.DATA_CONTAINER_BASE_SIZE, 2));
        }
        return ownerName;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        mountEntity(player);
        return true;
    }

    @Override
    public boolean mountEntity(Entity entity) {
        this.getMemoryStorage().put(CoreMemoryTypes.RIDER_NAME, entity.getName());
        super.mountEntity(entity, SetEntityLinkPacket.TYPE_RIDE);
        return true;
    }

    @Override
    public boolean dismountEntity(Entity entity) {
        this.getMemoryStorage().clear(CoreMemoryTypes.RIDER_NAME);
        return super.dismountEntity(entity);
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, 2.42f, 0);
    }

    @Override
    public HorseInventory getInventory() {
        return horseInventory;
    }

    @Nullable
    public Entity getRider() {
        String name = getMemoryStorage().get(CoreMemoryTypes.RIDER_NAME);
        if (name != null) {
            return Server.getInstance().getPlayerExact(name);
        } else return null;//todo other entity
    }

    public void setSaddle(Item item) {
        this.getInventory().setSaddle(item);
    }

    public void setHorseArmor(Item item) {
        this.getInventory().setHorseArmor(item);
    }

    public Item getSaddle() {
        return this.getInventory().getSaddle();
    }

    public Item getHorseArmor() {
        return this.getInventory().getHorseArmor();
    }

    public void playTameFailAnimation() {
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_MAD, -1, "minecraft:horse", this.isBaby(), false);
        this.setDataFlag(EntityHorse.DATA_FLAGS, EntityHorse.DATA_FLAG_REARING);
    }

    public void stopTameFailAnimation() {
        this.setDataFlag(EntityHorse.DATA_FLAGS, EntityHorse.DATA_FLAG_REARING, false);
    }
}
