package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCanAttack;
import cn.nukkit.entity.EntityCanSit;
import cn.nukkit.entity.EntityColor;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityVariant;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.ConditionalProbabilityEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.BegExecutor;
import cn.nukkit.entity.ai.executor.BreedingExecutor;
import cn.nukkit.entity.ai.executor.EntityMoveToOwnerExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.LoveTimeoutExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.WolfAttackExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.EntityAttackedByOwnerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.components.AgeableComponent;
import cn.nukkit.entity.components.BreedableComponent;
import cn.nukkit.entity.components.HealableComponent;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.entity.components.TameableComponent;
import cn.nukkit.entity.data.property.BooleanEntityProperty;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.entity.data.property.EnumEntityProperty;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityArmorInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemWolfArmor;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * @author BeYkeRYkt (Nukkit Project)
 * @author Cool_Loong (PowerNukkitX Project)
 * TODO: Wild wolves will not be refreshed.
 */
public class EntityWolf extends EntityAnimal implements EntityWalkable, EntityCanAttack, EntityCanSit, EntityColor, EntityVariant, InventoryHolder {
    private static final String[] SOUND_VARIANTS = {
        "default",
        "big",
        "cute",
        "grumpy",
        "mad",
        "puglin",
        "sad"
    };

    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
        new BooleanEntityProperty("minecraft:is_armorable", false),
        new BooleanEntityProperty("minecraft:has_increased_max_health", false),
        new EnumEntityProperty("minecraft:sound_variant", SOUND_VARIANTS, "default", true)
    };

    private final static String PROPERTY_IS_ARMORABLE = "minecraft:is_armorable";
    private final static String PROPERTY_HAS_INCREASED_MAX_HEALTH = "minecraft:has_increased_max_health";
    private final static String PROPERTY_SOUND_VARIANT = "minecraft:sound_variant";

    private static final String TAG_ARMOR = "Armor";

    private static final int[] VARIANTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};

    private static final int PALE = 0;
    private static final int ASHEN = 1;
    private static final int BLACK = 2;
    private static final int CHESSNUT = 3;
    private static final int RUSTY = 4;
    private static final int SNOWY = 5;
    private static final int SPOTTED = 6;
    private static final int STRIPPED = 7;
    private static final int WOODS = 8;

    private EntityArmorInventory armorInventory;

    @Override
    @NotNull
    public String getIdentifier() {
        return WOLF;
    }

    protected float[] diffHandDamage = new float[]{3, 4, 6};

    public EntityWolf(IChunk chunk, CompoundTag nbt) {
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

    @Override
    public String getOriginalName() {
        return "Wolf";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("wolf", "mob");
    }

    @Override
    public String[] getSoundVariants() {
        return SOUND_VARIANTS;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(8);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.3f);
    }

    @Override
    public @Nullable TameableComponent getComponentTameable() {
        return new TameableComponent(
                0.33f,
                Set.of(
                    ItemID.BONE
                )
        );
    }

    @Override
    public void onTameSuccess(Player player) {
        this.setHealthMax(20);
        this.setHealthCurrent(20);
        if (!this.hasColor()) {
            this.setColor(DyeColor.RED);
        }
        this.getLevel().dropExpOrb(this, Utils.rand(1, 7));

        super.onTameSuccess(player);
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                null,
                null,
                null,
                Set.of(
                    ItemID.CHICKEN,
                    ItemID.COOKED_CHICKEN,
                    ItemID.BEEF,
                    ItemID.COOKED_BEEF,
                    ItemID.MUTTON,
                    ItemID.COOKED_MUTTON,
                    ItemID.PORKCHOP,
                    ItemID.COOKED_PORKCHOP,
                    ItemID.RABBIT,
                    ItemID.COOKED_RABBIT,
                    ItemID.ROTTEN_FLESH
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.WOLF, EntityID.WOLF)
                ),
                null,
                null,
                null,
                null,
                false,
                null,
                true,
                true,
                true,
                null
        );
    }

    @Override
    public HealableComponent getComponentHealable() {
        return new HealableComponent(
                List.of(
                    new HealableComponent.Item(ItemID.PORKCHOP, 6),
                    new HealableComponent.Item(ItemID.COOKED_PORKCHOP, 16),
                    new HealableComponent.Item(ItemID.COD, 4),
                    new HealableComponent.Item(ItemID.SALMON, 4),
                    new HealableComponent.Item(ItemID.TROPICAL_FISH, 2),
                    new HealableComponent.Item(ItemID.PUFFERFISH, 2),
                    new HealableComponent.Item(ItemID.COOKED_COD, 10),
                    new HealableComponent.Item(ItemID.COOKED_SALMON, 12),
                    new HealableComponent.Item(ItemID.BEEF, 6),
                    new HealableComponent.Item(ItemID.COOKED_BEEF, 16),
                    new HealableComponent.Item(ItemID.CHICKEN, 4),
                    new HealableComponent.Item(ItemID.COOKED_CHICKEN, 12),
                    new HealableComponent.Item(ItemID.MUTTON, 4),
                    new HealableComponent.Item(ItemID.COOKED_MUTTON, 12),
                    new HealableComponent.Item(ItemID.ROTTEN_FLESH, 8),
                    new HealableComponent.Item(ItemID.RABBIT, 6),
                    new HealableComponent.Item(ItemID.COOKED_RABBIT, 10),
                    new HealableComponent.Item(ItemID.RABBIT_STEW, 20)
                )
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                List.of(
                    new AgeableComponent.FeedItem(ItemID.CHICKEN),
                    new AgeableComponent.FeedItem(ItemID.COOKED_CHICKEN),
                    new AgeableComponent.FeedItem(ItemID.BEEF),
                    new AgeableComponent.FeedItem(ItemID.COOKED_BEEF),
                    new AgeableComponent.FeedItem(ItemID.MUTTON),
                    new AgeableComponent.FeedItem(ItemID.COOKED_MUTTON),
                    new AgeableComponent.FeedItem(ItemID.PORKCHOP),
                    new AgeableComponent.FeedItem(ItemID.COOKED_PORKCHOP),
                    new AgeableComponent.FeedItem(ItemID.RABBIT),
                    new AgeableComponent.FeedItem(ItemID.COOKED_RABBIT),
                    new AgeableComponent.FeedItem(ItemID.ROTTEN_FLESH)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public void initEntity() {
        super.initEntity();
        if (!hasVariant()) {
            this.setVariant(getBiomeVariant(getLevel().getBiomeId((int) x, (int) y, (int) z)));
        }
        // Update CollarColor to Color
        if (namedTag.contains("CollarColor")) {
            this.setColor(DyeColor.getByWoolData(namedTag.getByte("CollarColor")));
        }
        this.armorInventory = new EntityArmorInventory(this);

        if (this.namedTag.contains(TAG_ARMOR)) {
            ListTag<CompoundTag> armorList = this.namedTag.getList(TAG_ARMOR, CompoundTag.class);
            for (CompoundTag armorTag : armorList.getAll()) {
                this.armorInventory.setItem(armorTag.getByte("Slot"), NBTIO.getItemHelper(armorTag));
            }
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        // Synchronize owner eid
        if (hasOwner()) {
            Player owner = getOwner();
            if (owner != null && getDataProperty(Entity.OWNER_EID) != owner.getId()) {
                this.setDataProperty(Entity.OWNER_EID, owner.getId());
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (!item.isNull() && this.isTamed()) {
            if ((item instanceof ItemDye dyeItem) && this.hasOwner() && player.equals(this.getOwner())) {
                this.setColor(dyeItem.getDyeColor());
                return true;
            } else if ((item instanceof ItemWolfArmor armor) && this.hasOwner() && player.equals(this.getOwner()) && armorInventory.getChestplate().isNull()) {
                armorInventory.setChestplate(armor);
                return true;
            }
        }

        if (this.hasOwner() && player.getName().equals(getOwnerName()) && !this.isTouchingWater()) {
            this.setSitting(!this.isSitting());
            return false;
        }

        return false;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (this.armorInventory != null) {
            ListTag<CompoundTag> armorTag = new ListTag<>();
            for (int i = 0; i < 4; i++) {
                armorTag.add(NBTIO.putItemHelper(this.armorInventory.getItem(i), i));
            }
            this.namedTag.putList(TAG_ARMOR,armorTag);
        }
    }

    // Rabbits, foxes, skeletons and their variants, alpacas, sheep, and baby turtles. However, they will run away when an alpaca spits at them.
    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case RABBIT, FOX, SKELETON, WITHER_SKELETON, STRAY, LLAMA, SHEEP, TURTLE -> true;
            default -> false;
        };
    }

    @Override
    public float[] getDiffHandDamage() {
        return diffHandDamage;
    }

    @Override
    public int[] getAllVariant() {
        return VARIANTS;
    }

    @Override
    public EntityArmorInventory getInventory() {
        return armorInventory;
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        armorInventory.sendContents(player);
    }

    public Item getArmor() {
        return getInventory().getChestplate();
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(!getArmor().isNull()) {
            switch (source.getCause()) {
                case DROWNING,
                     FREEZING,
                     SUFFOCATION,
                     MAGIC,
                     THORNS,
                     WITHER,
                     VOID,
                     SUICIDE -> {
                    return super.attack(source);
                }
                default -> {
                    Item armor = damageArmor((int) source.getFinalDamage());
                    getInventory().setChestplate(armor);
                    source.setDamage(0);
                }
            }
        }
        return super.attack(source);
    }

    protected Item damageArmor(int amount) {
        Item armor = getArmor();
        if (armor.hasEnchantments()) {

            Enchantment durability = armor.getEnchantment(Enchantment.ID_DURABILITY);
            if (durability != null
                    && durability.getLevel() > 0
                    && (100 / (durability.getLevel() + 1)) <= Utils.random.nextInt(100)) {
                return armor;
            }
        }

        if (armor.isUnbreakable() || armor.getMaxDurability() < 0) {
            return armor;
        }

        armor.setDamage(armor.getDamage() + amount);

        if (armor.getDamage() >= armor.getMaxDurability()) {
            getLevel().addSound(this, Sound.RANDOM_BREAK);
            return Item.get(BlockID.AIR, 0, 0);
        }

        return armor;
    }

    public static int getBiomeVariant(int biomeId) {
        BiomeDefinition definition = Registries.BIOME.get(biomeId);
        Set<String> tags = definition.getTags();
        String name = definition.getName();
        if(name.equals("cold_taiga")) return ASHEN;
        if(name.equals("mega_taiga")) return BLACK;
        if(name.equals("redwood_taiga_mutated")) return CHESSNUT;
        if(tags.contains("jungle")) return RUSTY;
        if(name.equals("grove") || tags.contains("frozen")) return SNOWY;
        if(tags.contains("mesa")) return STRIPPED;
        if(tags.contains("savanna")) return SPOTTED;
        if(name.equals("forest")) return WOODS;
        return PALE;
    }

    private static final Set<String> BEG_ITEMS = Set.of(
        ItemID.BONE,
        ItemID.PORKCHOP,
        ItemID.COOKED_PORKCHOP,
        ItemID.CHICKEN,
        ItemID.COOKED_CHICKEN,
        ItemID.BEEF,
        ItemID.COOKED_BEEF,
        ItemID.ROTTEN_FLESH,
        ItemID.MUTTON,
        ItemID.COOKED_MUTTON,
        ItemID.RABBIT,
        ItemID.COOKED_RABBIT
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                    new Behavior(
                        new LoveTimeoutExecutor(20 * 30),
                            e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                        3, 1
                    ),
                    new Behavior(
                        new AnimalGrowExecutor(),
                            all(
                                e -> e.isAgeable(),
                                e -> e.isBaby(),
                                e -> !e.isGrowthPaused(),
                                e -> e.getTicksGrowLeft() > 0
                            ),
                        2, 1, 1200
                    ),
                    new Behavior( // Refresh attack target
                            entity -> {
                                var storage = getMemoryStorage();
                                var hasOwner = hasOwner();
                                Entity attackTarget = null;
                                var attackEvent = storage.get(CoreMemoryTypes.BE_ATTACKED_EVENT);
                                EntityDamageByEntityEvent attackByEntityEvent = null;
                                if (attackEvent instanceof EntityDamageByEntityEvent attackByEntityEv)
                                    attackByEntityEvent = attackByEntityEv;
                                boolean validAttacker = attackByEntityEvent != null && attackByEntityEvent.getDamager().isAlive() && (!(attackByEntityEvent.getDamager() instanceof Player player) || player.isSurvival());
                                if (hasOwner) {
                                    // Tamed
                                    if (storage.notEmpty(CoreMemoryTypes.ENTITY_ATTACKING_OWNER) && storage.get(CoreMemoryTypes.ENTITY_ATTACKING_OWNER).isAlive() && !storage.get(CoreMemoryTypes.ENTITY_ATTACKING_OWNER).equals(this)) {
                                        // Attacks creatures that attack their master (excluding themselves).
                                        attackTarget = storage.get(CoreMemoryTypes.ENTITY_ATTACKING_OWNER);
                                        storage.clear(CoreMemoryTypes.ENTITY_ATTACKING_OWNER);
                                    } else if (storage.notEmpty(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER) && storage.get(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER).isAlive() && !storage.get(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER).equals(this)) {
                                        // The creature that attacks its master
                                        attackTarget = storage.get(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER);
                                        storage.clear(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER);
                                    } else if (attackByEntityEvent != null && validAttacker && !attackByEntityEvent.getDamager().equals(getOwner())) {
                                        // Attacks creatures that attack themselves (except their owners).
                                        attackTarget = attackByEntityEvent.getDamager();
                                        storage.clear(CoreMemoryTypes.BE_ATTACKED_EVENT);
                                    } else if (storage.notEmpty(CoreMemoryTypes.NEAREST_SKELETON) && storage.get(CoreMemoryTypes.NEAREST_SKELETON).isAlive()) {
                                        // Attack the nearest skeleton
                                        attackTarget = storage.get(CoreMemoryTypes.NEAREST_SKELETON);
                                        storage.clear(CoreMemoryTypes.NEAREST_SKELETON);
                                    }
                                } else {
                                    // Untamed
                                    if (validAttacker) {
                                        // Attack the creature that attacks itself
                                        attackTarget = attackByEntityEvent.getDamager();
                                        storage.clear(CoreMemoryTypes.BE_ATTACKED_EVENT);
                                    } else if (storage.notEmpty(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET) && storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET).isAlive()) {
                                        // Attack the nearest suitable creature
                                        attackTarget = storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET);
                                        storage.clear(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET);
                                    }
                                }
                                storage.put(CoreMemoryTypes.ATTACK_TARGET, attackTarget);
                                return false;
                            },
                            entity -> this.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET), 1
                    )
                ),
                Set.of(
                    new Behavior(
                        new PlaySoundExecutor(Sound.MOB_WOLF_BARK),
                            new RandomSoundEvaluator(),
                        6,1
                    ),
                    new Behavior(
                        new BegExecutor(true, 8, BEG_ITEMS),
                            all(
                                e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                e -> BegExecutor.hasBeggingPlayer(e, false, 10, BEG_ITEMS)
                            ),
                        5, 1
                    ),
                    // Attack the target of hatred (todo) and summon allies.
                    new Behavior(
                        new WolfAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.7f, 33, true, 20),
                            all(
                                e -> !this.isSitting(),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.ATTACK_TARGET)
                            ),
                        4, 1
                    ),
                    new Behavior(
                        new BreedingExecutor(16, 200, 0.35f),
                            all(
                                e -> !this.isSitting(),
                                e -> !e.isBaby(),
                                e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                            ),
                        3, 1
                    ),
                    new Behavior(
                        new EntityMoveToOwnerExecutor(0.7f, true, 15),
                            entity -> {
                                if (this.isSitting()) return false;

                                if (this.hasOwner()) {
                                    var player = getOwner();
                                    if (!player.isOnGround()) return false;
                                    var distanceSquared = this.distanceSquared(player);
                                    return distanceSquared >= 100;
                                } else return false;
                            }, 
                        2, 1
                    ),
                    new Behavior(
                        new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                            all(
                                new ConditionalProbabilityEvaluator(3, 7, entity -> hasOwner(false), 10)
                            ),
                        1, 1, 25
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(0.2f, 12, 150, false, -1, true, 10),
                            all(
                                e -> !this.isSitting(),
                                new ProbabilityEvaluator(5, 10)
                            ),
                        1, 1, 50
                    )
                ),
                Set.of(
                    new NearestPlayerSensor(8, 0, 20),
                    new NearestTargetEntitySensor<>(0, 20, 20,
                        List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, CoreMemoryTypes.NEAREST_SKELETON), this::attackTarget,
                        entity -> switch (entity.getIdentifier()) {
                            case SKELETON, WITHER_SKELETON, STRAY -> true;
                            default -> false;
                        }),
                    new EntityAttackedByOwnerSensor(5, false)
                ),
                Set.of(
                    new WalkController(),
                    new LookController(true, true),
                    new FluctuateController()
                ),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

}
