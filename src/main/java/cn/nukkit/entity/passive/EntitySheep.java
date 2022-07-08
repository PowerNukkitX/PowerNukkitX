package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.BehaviorGroup;
import cn.nukkit.entity.ai.IBehaviorGroup;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behavior.MultiBehavior;
import cn.nukkit.entity.ai.evaluator.PlayerEvaluator;
import cn.nukkit.entity.ai.executor.CryExecutor;
import cn.nukkit.entity.ai.executor.TestBaseMoveExecutor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntitySheep extends EntityAnimal {

    public static final int NETWORK_ID = 13;

    public boolean sheared = false;
    public int color = 0;

    protected IBehaviorGroup behaviorGroup = new BehaviorGroup(
            Set.of(new Behavior(new TestBaseMoveExecutor(),new PlayerEvaluator(),1,1)),
            Set.of(new NearestPlayerSensor(5))
    );

    public EntitySheep(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup getBehaviorGroup() {
        return behaviorGroup;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.3f;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Sheep";
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(8);

        if (!this.namedTag.contains("Color")) {
            this.setColor(randomColor());
        } else {
            this.setColor(this.namedTag.getByte("Color"));
        }

        if (!this.namedTag.contains("Sheared")) {
            this.namedTag.putByte("Sheared", 0);
        } else {
            this.sheared = this.namedTag.getBoolean("Sheared");
        }

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, this.sheared);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putByte("Color", this.color);
        this.namedTag.putBoolean("Sheared", this.sheared);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (super.onInteract(player, item, clickedPos)) {
            return true;
        }

        if (item instanceof ItemDye) {
            this.setColor(((ItemDye) item).getDyeColor().getWoolData());
            return true;
        }

        return item.getId() == Item.SHEARS && shear();
    }

    public boolean shear() {
        if (sheared) {
            return false;
        }

        this.sheared = true;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, true);

        this.level.dropItem(this, Item.get(Item.WOOL, getColor(), ThreadLocalRandom.current().nextInt(2) + 1));
        return true;
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_MUTTON : Item.RAW_MUTTON)), Item.get(Item.WOOL, getColor(), 1)};
        }
        return Item.EMPTY_ARRAY;
    }

    public void setColor(int color) {
        this.color = color;
        this.setDataProperty(new ByteEntityData(DATA_COLOUR, color));
        this.namedTag.putByte("Color", this.color);
    }

    public int getColor() {
        return namedTag.getByte("Color");
    }

    private int randomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double rand = random.nextDouble(1, 100);

        if (rand <= 0.164) {
            return DyeColor.PINK.getWoolData();
        }

        if (rand <= 15) {
            return random.nextBoolean() ? DyeColor.BLACK.getWoolData() : random.nextBoolean() ? DyeColor.GRAY.getWoolData() : DyeColor.LIGHT_GRAY.getWoolData();
        }

        return DyeColor.WHITE.getWoolData();
    }
}
