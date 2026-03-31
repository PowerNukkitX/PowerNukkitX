package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockVault;
import cn.nukkit.block.property.enums.VaultState;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.random.RandomSourceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BlockEntityVault extends BlockEntitySpawnable {
    public static final String TAG_CONFIG = "config";
    public static final String TAG_SERVER_DATA = "server_data";
    public static final String DATA = "data";

    public static final String TAG_LOOT_TABLE = "loot_table";
    public static final String TAG_OVERRIDE_LOOT_TABLE_TO_DISPLAY = "override_loot_table_to_display";
    public static final String TAG_ACTIVATION_RANGE = "activation_range";
    public static final String TAG_DEACTIVATION_RANGE = "deactivation_range";
    public static final String TAG_KEY_ITEM = "key_item";

    public static final String TAG_REWARDED_PLAYERS = "rewarded_players";
    public static final String TAG_STATE_UPDATING_RESUMES_AT = "state_updating_resumes_at";
    public static final String TAG_ITEMS_TO_EJECT = "items_to_eject";
    public static final String TAG_TOTAL_EJECTIONS_NEEDED = "total_ejections_needed";

    public static final String TAG_DISPLAY_ITEM = "display_item";
    public static final String TAG_CONNECTED_PLAYERS = "connected_players";
    public static final String TAG_CONNECTED_PARTICLES_RANGE = "connected_particles_range";

    public static final String DEFAULT_LOOT_TABLE = "minecraft:chests/trial_chambers/reward";
    public static final double DEFAULT_ACTIVATION_RANGE = 4.0d;
    public static final double DEFAULT_DEACTIVATION_RANGE = 4.5d;
    public static final double DEFAULT_CONNECTED_PARTICLES_RANGE = 4.5d;

    private String lootTable = DEFAULT_LOOT_TABLE;
    private String overrideLootTableToDisplay = "";
    private double activationRange = DEFAULT_ACTIVATION_RANGE;
    private double deactivationRange = DEFAULT_DEACTIVATION_RANGE;
    private Item keyItem = Item.get(ItemID.TRIAL_KEY);

    private LinkedHashSet<String> rewardedPlayers;
    private long stateUpdatingResumesAt;
    private List<Item> itemsToEject;
    private int totalEjectionsNeeded;

    private Item displayItem = Item.get(BlockID.AIR);
    private Set<String> connectedPlayers;
    private double connectedParticlesRange = DEFAULT_CONNECTED_PARTICLES_RANGE;

    public BlockEntityVault(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock() instanceof BlockVault;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        ensureCollections();

        CompoundTag config = namedTag.getCompound(TAG_CONFIG);
        lootTable = config.contains(TAG_LOOT_TABLE) ? config.getString(TAG_LOOT_TABLE) : DEFAULT_LOOT_TABLE;
        overrideLootTableToDisplay = config.getString(TAG_OVERRIDE_LOOT_TABLE_TO_DISPLAY);
        activationRange = config.contains(TAG_ACTIVATION_RANGE) ? config.getDouble(TAG_ACTIVATION_RANGE) : DEFAULT_ACTIVATION_RANGE;
        deactivationRange = config.contains(TAG_DEACTIVATION_RANGE) ? config.getDouble(TAG_DEACTIVATION_RANGE) : DEFAULT_DEACTIVATION_RANGE;
        keyItem = config.containsCompound(TAG_KEY_ITEM) ? NBTIO.getItemHelper(config.getCompound(TAG_KEY_ITEM)) : Item.get(ItemID.TRIAL_KEY);

        CompoundTag serverData = namedTag.getCompound(TAG_SERVER_DATA);
        rewardedPlayers.clear();
        rewardedPlayers.addAll(readStringSet(serverData, TAG_REWARDED_PLAYERS));
        stateUpdatingResumesAt = serverData.getLong(TAG_STATE_UPDATING_RESUMES_AT);
        itemsToEject.clear();
        itemsToEject.addAll(readItemList(serverData, TAG_ITEMS_TO_EJECT));
        totalEjectionsNeeded = serverData.getInt(TAG_TOTAL_EJECTIONS_NEEDED);

        CompoundTag sharedData = namedTag.getCompound(DATA);
        displayItem = sharedData.containsCompound(TAG_DISPLAY_ITEM) ? NBTIO.getItemHelper(sharedData.getCompound(TAG_DISPLAY_ITEM)) : Item.get(BlockID.AIR);
        connectedPlayers.clear();
        connectedPlayers.addAll(readStringSet(sharedData, TAG_CONNECTED_PLAYERS));
        connectedParticlesRange = sharedData.contains(TAG_CONNECTED_PARTICLES_RANGE) ? sharedData.getDouble(TAG_CONNECTED_PARTICLES_RANGE) : DEFAULT_CONNECTED_PARTICLES_RANGE;
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        this.scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        namedTag.putCompound(TAG_CONFIG, createConfigTag());
        namedTag.putCompound(TAG_SERVER_DATA, createServerDataTag());
        namedTag.putCompound(DATA, createSharedDataTag());
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putCompound(TAG_CONFIG, createConfigTag())
                .putCompound(DATA, createSharedDataTag());
    }

    @Override
    public boolean onUpdate() {
        if(!isBlockEntityValid()) {
            this.close();
        }
        if(closed) return false;
        if(level.getTick() % 5 == 0 && itemsToEject.isEmpty()) {
            var deactivationBox = level.getCollidingEntities(
                    this.getBlock().getBoundingBox().grow(deactivationRange, deactivationRange, deactivationRange)
            );
            Set<String> connectedPlayers = new HashSet<>(getConnectedPlayers());
            Set<String> rewardedPlayers = new HashSet<>(getRewardedPlayers());

            boolean changed = connectedPlayers.removeIf(id ->
                    Arrays.stream(deactivationBox).noneMatch(entity ->
                            entity instanceof Player player
                                    && player.getUniqueId().toString().equals(id)
                                    && !rewardedPlayers.contains(id)
                                    && !(player.isSpectator() || player.getDataFlag(EntityFlag.SILENT))
                    )
            );

            var activationBox = level.getCollidingEntities(
                    this.getBlock().getBoundingBox().grow(activationRange, activationRange, activationRange)
            );

            for (Entity entity : activationBox) {
                if (entity instanceof Player player) {
                    String id = player.getUniqueId().toString();

                    if (!rewardedPlayers.contains(id) && !connectedPlayers.contains(id) && !(player.isSpectator() || player.getDataFlag(EntityFlag.SILENT))) {
                        connectedPlayers.add(id);
                        changed = true;
                    }
                }
            }

            if (changed) {
                setConnectedPlayers(connectedPlayers);

                if (getBlock() instanceof BlockVault vault) {
                    vault.setVaultState(connectedPlayers.isEmpty()
                            ? VaultState.INACTIVE
                            : VaultState.ACTIVE);
                }
            }
        }
        if(level.getTick() % 20 == 0) {
            if(!itemsToEject.isEmpty()) {
                if(getBlock() instanceof BlockVault vault) {
                    if(vault.getVaultState() == VaultState.UNLOCKING) {
                        vault.setVaultState(VaultState.EJECTING);
                        this.scheduleUpdate();
                        return true;
                    }
                }
                level.dropItem(this.add(0.5, 0.8, 0.5), itemsToEject.removeFirst(), new Vector3(0, 0.2, 0));
                level.addLevelSoundEvent(this, LevelSoundEvent.VAULT_EJECT_ITEM);
                if(!itemsToEject.isEmpty()) {
                    setDisplayItem(itemsToEject.getFirst());
                } else setDisplayItem(Item.AIR);
            }
        }
        this.scheduleUpdate();
        return true;
    }

    public String getLootTable() {
        return lootTable;
    }

    public void setLootTable(@NotNull String lootTable) {
        this.lootTable = lootTable;
        setDirty();
    }

    public String getOverrideLootTableToDisplay() {
        return overrideLootTableToDisplay;
    }

    public void setOverrideLootTableToDisplay(String overrideLootTableToDisplay) {
        this.overrideLootTableToDisplay = overrideLootTableToDisplay == null ? "" : overrideLootTableToDisplay;
        setDirty();
    }

    public double getActivationRange() {
        return activationRange;
    }

    public void setActivationRange(double activationRange) {
        this.activationRange = activationRange;
        setDirty();
    }

    public double getDeactivationRange() {
        return deactivationRange;
    }

    public void setDeactivationRange(double deactivationRange) {
        this.deactivationRange = deactivationRange;
        setDirty();
    }

    public Item getKeyItem() {
        return keyItem.clone();
    }

    public void setKeyItem(@NotNull Item keyItem) {
        this.keyItem = keyItem.clone();
        setDirty();
    }

    public Set<String> getRewardedPlayers() {
        ensureCollections();
        return Set.copyOf(rewardedPlayers);
    }

    public void setRewardedPlayers(@NotNull Set<String> rewardedPlayers) {
        ensureCollections();
        this.rewardedPlayers.clear();
        this.rewardedPlayers.addAll(rewardedPlayers);
        setDirty();
    }

    public void addRewardedPlayer(@NotNull String rewardedPlayer) {
        ensureCollections();
        rewardedPlayers.addLast(rewardedPlayer);
        if(rewardedPlayers.size() > 64) rewardedPlayers.removeFirst();
        setDirty();
    }

    public List<Item> getItemsToEject() {
        ensureCollections();
        return itemsToEject.stream().map(Item::clone).toList();
    }

    public void setItemsToEject(@NotNull List<Item> itemsToEject) {
        ensureCollections();
        this.itemsToEject.clear();
        itemsToEject.stream().map(Item::clone).forEach(this.itemsToEject::add);
        setDirty();
    }

    public int getTotalEjectionsNeeded() {
        return totalEjectionsNeeded;
    }

    public void setTotalEjectionsNeeded(int totalEjectionsNeeded) {
        this.totalEjectionsNeeded = totalEjectionsNeeded;
        setDirty();
    }

    public Item getDisplayItem() {
        return displayItem.clone();
    }

    public void setDisplayItem(@NotNull Item displayItem) {
        this.displayItem = displayItem.clone();
        spawnToAll();
    }

    public Set<String> getConnectedPlayers() {
        ensureCollections();
        return Set.copyOf(connectedPlayers);
    }

    public void setConnectedPlayers(@NotNull Set<String> connectedPlayers) {
        ensureCollections();
        this.connectedPlayers.clear();
        this.connectedPlayers.addAll(connectedPlayers);
        this.spawnToAll();
        setDirty();
    }

    public void addConnectedPlayer(@NotNull String connectedPlayer) {
        ensureCollections();
        connectedPlayers.add(connectedPlayer);
        setDirty();
    }

    public double getConnectedParticlesRange() {
        return connectedParticlesRange;
    }

    public boolean canPlayerOpen(@NotNull Player player) {
        ensureCollections();
        return !rewardedPlayers.contains(player.getUniqueId().toString());
    }

    public boolean matchesKey(@NotNull Item item, boolean ominous) {
        if (item.isNull()) {
            return false;
        }

        String expectedKeyId = resolveKeyId(ominous);
        return expectedKeyId.equals(item.getId());
    }

    public boolean populateItemsToEject(@NotNull RandomSourceProvider randomSourceProvider, boolean ominous) {
        if(!itemsToEject.isEmpty()) return false;
        String effectiveLootTable = resolveLootTable(ominous);

        List<Item> rolledItems = TrialChambersVaultLoot.roll(effectiveLootTable, randomSourceProvider);
        setItemsToEject(rolledItems);
        setTotalEjectionsNeeded(rolledItems.size());

        if (!rolledItems.isEmpty()) {
            setDisplayItem(rolledItems.get(0));
        } else {
            setDisplayItem(Item.AIR);
        }
        return true;
    }

    public void clearPendingRewards() {
        setItemsToEject(List.of());
        setTotalEjectionsNeeded(0);
        setDisplayItem(Item.AIR);
    }

    public String resolveLootTable(boolean ominous) {
        String normalizedLootTable = normalizeResourceKey(lootTable);
        if (ominous && ("chests/trial_chambers/reward".equals(normalizedLootTable) || DEFAULT_LOOT_TABLE.equals(lootTable))) {
            return "chests/trial_chambers/reward_ominous";
        }
        return normalizedLootTable;
    }

    public String resolveKeyId(boolean ominous) {
        if (ominous && (keyItem.isNull() || ItemID.TRIAL_KEY.equals(keyItem.getId()))) {
            return ItemID.OMINOUS_TRIAL_KEY;
        }
        return keyItem.isNull() ? ItemID.TRIAL_KEY : keyItem.getId();
    }

    private CompoundTag createConfigTag() {
        CompoundTag config = new CompoundTag()
                .putString(TAG_LOOT_TABLE, lootTable)
                .putDouble(TAG_ACTIVATION_RANGE, activationRange)
                .putDouble(TAG_DEACTIVATION_RANGE, deactivationRange)
                .putCompound(TAG_KEY_ITEM, NBTIO.putItemHelper(keyItem));
        if (!overrideLootTableToDisplay.isEmpty()) {
            config.putString(TAG_OVERRIDE_LOOT_TABLE_TO_DISPLAY, overrideLootTableToDisplay);
        }
        return config;
    }

    private CompoundTag createServerDataTag() {
        ensureCollections();
        return new CompoundTag()
                .putList(TAG_REWARDED_PLAYERS, writeStringSet(rewardedPlayers))
                .putLong(TAG_STATE_UPDATING_RESUMES_AT, stateUpdatingResumesAt)
                .putList(TAG_ITEMS_TO_EJECT, writeItemList(itemsToEject))
                .putInt(TAG_TOTAL_EJECTIONS_NEEDED, totalEjectionsNeeded);
    }

    private CompoundTag createSharedDataTag() {
        ensureCollections();
        return new CompoundTag()
                .putCompound(TAG_DISPLAY_ITEM, NBTIO.putItemHelper(displayItem))
                .putList(TAG_CONNECTED_PLAYERS, writeStringSet(connectedPlayers))
                .putDouble(TAG_CONNECTED_PARTICLES_RANGE, connectedParticlesRange);
    }

    private static Set<String> readStringSet(CompoundTag source, String key) {
        LinkedHashSet<String> values = new LinkedHashSet<>();
        for (StringTag tag : source.getList(key, StringTag.class).getAll()) {
            values.add(tag.data);
        }
        return values;
    }

    private static List<Item> readItemList(CompoundTag source, String key) {
        ArrayList<Item> items = new ArrayList<>();
        for (CompoundTag tag : source.getList(key, CompoundTag.class).getAll()) {
            items.add(NBTIO.getItemHelper(tag));
        }
        return items;
    }

    private static ListTag<StringTag> writeStringSet(Set<String> values) {
        ListTag<StringTag> tags = new ListTag<>();
        for (String value : values) {
            tags.add(new StringTag(value));
        }
        return tags;
    }

    private static ListTag<CompoundTag> writeItemList(List<Item> items) {
        ListTag<CompoundTag> tags = new ListTag<>();
        for (Item item : items) {
            tags.add(NBTIO.putItemHelper(item));
        }
        return tags;
    }

    private static String normalizeResourceKey(String key) {
        if (key == null || key.isBlank()) {
            return "";
        }

        int separator = key.indexOf(':');
        return separator >= 0 ? key.substring(separator + 1) : key;
    }

    private void ensureCollections() {
        if (rewardedPlayers == null) {
            rewardedPlayers = new LinkedHashSet<>();
        }
        if (itemsToEject == null) {
            itemsToEject = new ArrayList<>();
        }
        if (connectedPlayers == null) {
            connectedPlayers = new LinkedHashSet<>();
        }
    }

    private final static class TrialChambersVaultLoot {
        private static final String REWARD = "chests/trial_chambers/reward";
        private static final String REWARD_COMMON = "chests/trial_chambers/reward_common";
        private static final String REWARD_RARE = "chests/trial_chambers/reward_rare";
        private static final String REWARD_UNIQUE = "chests/trial_chambers/reward_unique";
        private static final String REWARD_OMINOUS = "chests/trial_chambers/reward_ominous";
        private static final String REWARD_OMINOUS_COMMON = "chests/trial_chambers/reward_ominous_common";
        private static final String REWARD_OMINOUS_RARE = "chests/trial_chambers/reward_ominous_rare";
        private static final String REWARD_OMINOUS_UNIQUE = "chests/trial_chambers/reward_ominous_unique";

        private TrialChambersVaultLoot() {
        }

        static List<Item> roll(String lootTable, RandomSourceProvider random) {
            return switch (normalize(lootTable)) {
                case REWARD -> rollReward(random);
                case REWARD_COMMON -> List.of(rollRewardCommon(random));
                case REWARD_RARE -> List.of(rollRewardRare(random));
                case REWARD_UNIQUE -> List.of(rollRewardUnique(random));
                case REWARD_OMINOUS -> rollRewardOminous(random);
                case REWARD_OMINOUS_COMMON -> List.of(rollRewardOminousCommon(random));
                case REWARD_OMINOUS_RARE -> List.of(rollRewardOminousRare(random));
                case REWARD_OMINOUS_UNIQUE -> List.of(rollRewardOminousUnique(random));
                default -> List.of();
            };
        }

        static Item rollDisplayItem(String lootTable, RandomSourceProvider random) {
            List<Item> items = roll(lootTable, random);
            return items.isEmpty() ? Item.AIR : items.get(random.nextBoundedInt(items.size() - 1)).clone();
        }

        private static List<Item> rollReward(RandomSourceProvider random) {
            ArrayList<Item> items = new ArrayList<>();
            items.add(weighted(random,
                    weightedEntry(8, TrialChambersVaultLoot::rollRewardCommon),
                    weightedEntry(2, TrialChambersVaultLoot::rollRewardRare)
            ));
            int commonRolls = random.nextInt(1, 3);
            for (int i = 0; i < commonRolls; i++) {
                items.add(rollRewardCommon(random));
            }
            if (random.nextDouble() < 0.25d) {
                items.add(rollRewardUnique(random));
            }
            return items;
        }

        private static List<Item> rollRewardOminous(RandomSourceProvider random) {
            ArrayList<Item> items = new ArrayList<>();
            items.add(weighted(random,
                    weightedEntry(2, TrialChambersVaultLoot::rollRewardOminousCommon),
                    weightedEntry(8, TrialChambersVaultLoot::rollRewardOminousRare)
            ));
            int commonRolls = random.nextInt(1, 3);
            for (int i = 0; i < commonRolls; i++) {
                items.add(rollRewardOminousCommon(random));
            }
            if (random.nextDouble() < 0.75d) {
                items.add(rollRewardOminousUnique(random));
            }
            return items;
        }

        private static Item rollRewardCommon(RandomSourceProvider random) {
            return weighted(random,
                    weightedEntry(4, rnd -> item(ItemID.ARROW, 0, rnd.nextInt(2, 8))),
                    weightedEntry(4, rnd -> tippedArrow("minecraft:poison", rnd.nextInt(2, 8))),
                    weightedEntry(4, rnd -> item(ItemID.EMERALD, 0, rnd.nextInt(2, 4))),
                    weightedEntry(3, rnd -> item(ItemID.WIND_CHARGE, 0, rnd.nextInt(1, 3))),
                    weightedEntry(3, rnd -> item(ItemID.IRON_INGOT, 0, rnd.nextInt(1, 4))),
                    weightedEntry(3, rnd -> item(ItemID.HONEY_BOTTLE, 0, rnd.nextInt(1, 2))),
                    weightedEntry(2, rnd -> ominousBottle(rnd.nextInt(0, 1))),
                    weightedEntry(1, rnd -> item(ItemID.WIND_CHARGE, 0, rnd.nextInt(4, 12))),
                    weightedEntry(1, rnd -> item(ItemID.DIAMOND, 0, rnd.nextInt(1, 2)))
            );
        }

        private static Item rollRewardRare(RandomSourceProvider random) {
            return weighted(random,
                    weightedEntry(3, rnd -> item(ItemID.EMERALD, 0, rnd.nextInt(2, 4))),
                    weightedEntry(3, TrialChambersVaultLoot::damagedShield),
                    weightedEntry(3, rnd -> enchantWithLevels(item(ItemID.BOW, 0, 1), rnd.nextInt(5, 15), true, rnd)),
                    weightedEntry(2, rnd -> enchantWithLevels(item(ItemID.CROSSBOW, 0, 1), rnd.nextInt(5, 20), true, rnd)),
                    weightedEntry(2, rnd -> enchantWithLevels(item(ItemID.IRON_AXE, 0, 1), rnd.nextInt(0, 10), true, rnd)),
                    weightedEntry(2, rnd -> enchantWithLevels(item(ItemID.IRON_CHESTPLATE, 0, 1), rnd.nextInt(0, 10), true, rnd)),
                    weightedEntry(2, rnd -> item(ItemID.GOLDEN_CARROT, 0, rnd.nextInt(1, 2))),
                    weightedEntry(2, rnd -> tradingBook(rnd,
                            enchantOption("sharpness", 1, 5),
                            enchantOption("bane_of_arthropods", 1, 5),
                            enchantOption("efficiency", 1, 5),
                            enchantOption("fortune", 1, 3),
                            enchantOption("silk_touch", 1, 1),
                            enchantOption("feather_falling", 1, 4)
                    )),
                    weightedEntry(2, rnd -> tradingBook(rnd,
                            enchantOption("riptide", 1, 3),
                            enchantOption("loyalty", 1, 3),
                            enchantOption("channeling", 1, 1),
                            enchantOption("impaling", 1, 5),
                            enchantOption("mending", 1, 1)
                    )),
                    weightedEntry(1, rnd -> enchantWithLevels(item(ItemID.DIAMOND_CHESTPLATE, 0, 1), rnd.nextInt(5, 15), true, rnd)),
                    weightedEntry(1, rnd -> enchantWithLevels(item(ItemID.DIAMOND_AXE, 0, 1), rnd.nextInt(5, 15), true, rnd))
            );
        }

        private static Item rollRewardUnique(RandomSourceProvider random) {
            return weighted(random,
                    weightedEntry(4, rnd -> item(ItemID.GOLDEN_APPLE, 0, 1)),
                    weightedEntry(3, rnd -> item(ItemID.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, 0, 1)),
                    weightedEntry(2, rnd -> item(ItemID.GUSTER_BANNER_PATTERN, 0, 1)),
                    weightedEntry(2, rnd -> item(ItemID.MUSIC_DISC_PRECIPICE, 0, 1)),
                    weightedEntry(1, rnd -> item(ItemID.TRIDENT, 0, 1))
            );
        }

        private static Item rollRewardOminousCommon(RandomSourceProvider random) {
            return weighted(random,
                    weightedEntry(5, rnd -> item(ItemID.EMERALD, 0, rnd.nextInt(4, 10))),
                    weightedEntry(4, rnd -> item(ItemID.WIND_CHARGE, 0, rnd.nextInt(8, 12))),
                    weightedEntry(3, rnd -> tippedArrow("minecraft:strong_slowness", rnd.nextInt(4, 12))),
                    weightedEntry(2, rnd -> item(ItemID.DIAMOND, 0, rnd.nextInt(2, 3))),
                    weightedEntry(1, rnd -> ominousBottle(rnd.nextInt(2, 4)))
            );
        }

        private static Item rollRewardOminousRare(RandomSourceProvider random) {
            return weighted(random,
                    weightedEntry(5, rnd -> item(BlockID.EMERALD_BLOCK, 0, 1)),
                    weightedEntry(4, rnd -> item(BlockID.IRON_BLOCK, 0, 1)),
                    weightedEntry(4, rnd -> enchantWithLevels(item(ItemID.CROSSBOW, 0, 1), rnd.nextInt(5, 20), true, rnd)),
                    weightedEntry(3, rnd -> item(ItemID.GOLDEN_APPLE, 0, 1)),
                    weightedEntry(3, rnd -> enchantWithLevels(item(ItemID.DIAMOND_AXE, 0, 1), rnd.nextInt(10, 20), true, rnd)),
                    weightedEntry(3, rnd -> enchantWithLevels(item(ItemID.DIAMOND_CHESTPLATE, 0, 1), rnd.nextInt(10, 20), true, rnd)),
                    weightedEntry(2, rnd -> tradingBook(rnd,
                            enchantOption("knockback", 1, 2),
                            enchantOption("punch", 1, 2),
                            enchantOption("smite", 1, 5),
                            enchantOption("looting", 1, 3),
                            enchantOption("multishot", 1, 1)
                    )),
                    weightedEntry(2, rnd -> tradingBook(rnd,
                            enchantOption("breach", 1, 4),
                            enchantOption("density", 1, 5)
                    )),
                    weightedEntry(2, rnd -> tradingBook(rnd, enchantOption("wind_burst", 1, 1))),
                    weightedEntry(1, rnd -> item(BlockID.DIAMOND_BLOCK, 0, 1))
            );
        }

        private static Item rollRewardOminousUnique(RandomSourceProvider random) {
            return weighted(random,
                    weightedEntry(3, rnd -> item(ItemID.ENCHANTED_GOLDEN_APPLE, 0, 1)),
                    weightedEntry(3, rnd -> item(ItemID.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, 0, 1)),
                    weightedEntry(2, rnd -> item(ItemID.FLOW_BANNER_PATTERN, 0, 1)),
                    weightedEntry(1, rnd -> item(ItemID.MUSIC_DISC_CREATOR, 0, 1)),
                    weightedEntry(1, rnd -> item(BlockID.HEAVY_CORE, 0, 1))
            );
        }

        private static Item damagedShield(RandomSourceProvider random) {
            Item shield = item(ItemID.SHIELD, 0, 1);
            int maxDurability = shield.getMaxDurability();
            if (maxDurability > 0) {
                int damage = (int) Math.round(maxDurability * (0.5d + (random.nextDouble() * 0.5d)));
                shield.setDamage(Math.min(damage, maxDurability));
            }
            return shield;
        }

        private static Item tippedArrow(String potionId, int count) {
            PotionType potionType = PotionType.get(potionId);
            int meta = potionType == null ? 0 : potionType.id() + 1;
            return item(ItemID.ARROW, meta, count);
        }

        private static Item ominousBottle(int amplifier) {
            Item item = item(ItemID.OMINOUS_BOTTLE, 0, 1);
            item.setNamedTag(new CompoundTag().putInt("OminousBottleAmplifier", amplifier));
            item.setLore("Amplifier: " + (amplifier + 1));
            return item;
        }

        private static Item tradingBook(RandomSourceProvider random, EnchantOption... options) {
            Item book = item(ItemID.ENCHANTED_BOOK, 0, 1);
            EnchantOption option = options[random.nextBoundedInt(options.length - 1)];
            Enchantment enchantment = Enchantment.getEnchantment(option.id());
            enchantment.setLevel(random.nextInt(option.minLevel(), option.maxLevel()), true);
            book.addEnchantment(enchantment);
            return book;
        }

        private static Item enchantWithLevels(Item item, int levels, boolean treasure, RandomSourceProvider random) {
            List<Enchantment> pool = new ArrayList<>(Enchantment.getRegisteredEnchantments(true));
            pool.removeIf(enchantment -> enchantment == null || !enchantment.canEnchant(item));
            if (pool.isEmpty()) {
                return item;
            }

            int enchantCount = levels >= 18 ? 3 : levels >= 10 ? 2 : 1;
            enchantCount = Math.min(enchantCount, pool.size());

            for (int i = 0; i < enchantCount && !pool.isEmpty(); i++) {
                Enchantment selected = removeCompatibleEnchantment(pool, item, random, treasure);
                if (selected == null) {
                    break;
                }
                int maxLevel = Math.max(selected.getMinLevel(), selected.getMaxLevel());
                int chosenLevel = random.nextInt(selected.getMinLevel(), maxLevel);
                selected.setLevel(chosenLevel, true);
                item.addEnchantment(selected);
            }
            return item;
        }

        private static Enchantment removeCompatibleEnchantment(List<Enchantment> pool, Item item, RandomSourceProvider random, boolean treasure) {
            while (!pool.isEmpty()) {
                Enchantment enchantment = copyOf(pool.remove(random.nextBoundedInt(pool.size() - 1)));
                if (enchantment == null || !enchantment.canEnchant(item)) {
                    continue;
                }
                if (!treasure && isTreasure(enchantment)) {
                    continue;
                }
                boolean compatible = true;
                for (Enchantment existing : item.getEnchantments()) {
                    if (!enchantment.isCompatibleWith(existing)) {
                        compatible = false;
                        break;
                    }
                }
                if (compatible) {
                    return enchantment;
                }
            }
            return null;
        }

        private static boolean isTreasure(Enchantment enchantment) {
            return switch (enchantment.getId()) {
                case Enchantment.ID_MENDING, Enchantment.ID_BINDING_CURSE, Enchantment.ID_VANISHING_CURSE,
                     Enchantment.ID_SWIFT_SNEAK, Enchantment.ID_SOUL_SPEED, Enchantment.ID_WIND_BURST,
                     Enchantment.ID_DENSITY, Enchantment.ID_BREACH -> true;
                default -> false;
            };
        }

        private static Enchantment copyOf(Enchantment enchantment) {
            Identifier identifier = enchantment.getIdentifier();
            return identifier != null ? Enchantment.getEnchantment(identifier) : Enchantment.getEnchantment(enchantment.getId());
        }

        private static Item item(String id, int meta, int count) {
            return Item.get(id, meta, count);
        }

        @SafeVarargs
        private static Item weighted(RandomSourceProvider random, WeightedEntry<Item>... entries) {
            int totalWeight = 0;
            for (WeightedEntry<Item> entry : entries) {
                totalWeight += entry.weight();
            }
            int choice = random.nextInt(1, totalWeight);
            int cursor = 0;
            for (WeightedEntry<Item> entry : entries) {
                cursor += entry.weight();
                if (choice <= cursor) {
                    return entry.roller().roll(random);
                }
            }
            return entries[entries.length - 1].roller().roll(random);
        }

        private static WeightedEntry<Item> weightedEntry(int weight, ItemRoll roller) {
            return new WeightedEntry<>(weight, roller);
        }

        private static String normalize(String lootTable) {
            if (lootTable == null || lootTable.isBlank()) {
                return "";
            }
            int separator = lootTable.indexOf(':');
            return separator >= 0 ? lootTable.substring(separator + 1) : lootTable;
        }

        private record WeightedEntry<T>(int weight, Roll<T> roller) {
        }

        private record EnchantOption(String id, int minLevel, int maxLevel) {
        }

        @FunctionalInterface
        private interface Roll<T> {
            T roll(RandomSourceProvider random);
        }

        @FunctionalInterface
        private interface ItemRoll extends Roll<Item> {
            @Override
            Item roll(RandomSourceProvider random);
        }

        private static EnchantOption enchantOption(String id, int minLevel, int maxLevel) {
            return new EnchantOption(id, minLevel, maxLevel);
        }
    }

}
