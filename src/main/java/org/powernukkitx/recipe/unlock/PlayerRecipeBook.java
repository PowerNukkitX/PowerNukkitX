package org.powernukkitx.recipe.unlock;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.cloudburstmc.protocol.bedrock.packet.UnlockedRecipesPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.powernukkitx.Player;
import org.powernukkitx.event.player.PlayerUnlockRecipeEvent;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.InventoryListener;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.recipe.Recipe;
import org.powernukkitx.registry.Registries;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The set of recipes a player has discovered, mirrored to the client through
 * {@link UnlockedRecipesPacket}.
 * <p>
 * Discovery is driven by the {@code recipesUnlock} game rule of the level the player spawned in.
 * While the rule is off the client keeps showing the full recipe book and no packet is sent, so
 * the book behaves exactly as it did before this class existed. While it is on the player starts
 * from the persisted set and unlocks a recipe as soon as one of its vanilla unlock trigger items
 * enters the inventory, or as soon as the recipe is crafted.
 * <p>
 * Every method must be called from the thread that owns the player.
 *
 * @author xRookieFight
 * @since 29/07/2026
 */
public class PlayerRecipeBook implements InventoryListener {
    private static final String UNLOCKED_RECIPES_TAG = "UnlockedRecipes";

    private final Player player;
    private final Set<String> unlockedRecipes = new ObjectOpenHashSet<>();
    private boolean discovering;
    private boolean wasInWater;

    public PlayerRecipeBook(@NotNull Player player) {
        this.player = player;
    }

    /**
     * Whether this book progressively discovers recipes. {@code false} means the client sees every
     * recipe from the start, which is the case when the {@code recipesUnlock} game rule is off or
     * when the recipe registry is disabled entirely.
     */
    public boolean isDiscovering() {
        return this.discovering;
    }

    /**
     * @return the unlocked recipe ids, in no particular order
     */
    public @NotNull @UnmodifiableView Set<String> getUnlockedRecipes() {
        return Collections.unmodifiableSet(this.unlockedRecipes);
    }

    public boolean isUnlocked(@NotNull String recipeId) {
        return this.unlockedRecipes.contains(recipeId);
    }

    /**
     * Reads the persisted recipe ids. Recipes that no longer exist are dropped on the next spawn.
     *
     * @param nbt the player's root tag
     */
    public void load(@NotNull CompoundTag nbt) {
        this.unlockedRecipes.clear();
        if (!nbt.contains(UNLOCKED_RECIPES_TAG)) {
            return;
        }
        for (StringTag tag : nbt.getList(UNLOCKED_RECIPES_TAG, StringTag.class).getAll()) {
            this.unlockedRecipes.add(tag.parseValue());
        }
    }

    /**
     * Writes the unlocked recipe ids into the player's root tag.
     *
     * @param nbt the player's root tag
     */
    public void save(@NotNull CompoundTag nbt) {
        if (this.unlockedRecipes.isEmpty() && !nbt.contains(UNLOCKED_RECIPES_TAG)) {
            return;
        }
        final ListTag<StringTag> list = new ListTag<>();
        for (String recipeId : this.unlockedRecipes) {
            list.add(new StringTag(recipeId));
        }
        nbt.putList(UNLOCKED_RECIPES_TAG, list);
    }

    /**
     * Decides whether to discover recipes for this session, sends the initial book state and starts
     * listening for inventory changes. Called once the player has spawned and its inventory holds
     * the loaded contents.
     */
    public void onSpawn() {
        this.discovering = Registries.RECIPE.isEnabled()
            && this.player.getLevel().getGameRules().getBoolean(GameRule.RECIPES_UNLOCK);
        if (!this.discovering) {
            return;
        }

        final RecipeUnlockIndex index = Registries.RECIPE.getUnlockIndex();

        this.unlockedRecipes.removeIf(recipeId -> Registries.RECIPE.get(recipeId) == null);
        this.addAllWithoutPacket(index.getAlwaysUnlockedRecipes());

        this.wasInWater = this.player.isTouchingWater();
        if (this.wasInWater) {
            this.addAllWithoutPacket(index.getPlayerInWaterRecipes());
        }

        if (this.hasManyItems()) {
            this.addAllWithoutPacket(index.getPlayerHasManyItemsRecipes());
        }

        this.sendPacket(UnlockedRecipesPacket.UnlockedRecipesPacketType.INITIALLY_UNLOCKED, this.unlockedRecipes);

        this.player.getInventory().addListener(this);
        this.discoverFromInventory();
    }

    /**
     * Unlocks a single recipe and notifies the client. Fires {@link PlayerUnlockRecipeEvent}.
     *
     * @param recipe the recipe to unlock
     * @return {@code false} when discovery is off, when it was already unlocked, or when a plugin
     * cancelled the event
     */
    public boolean unlock(@NotNull Recipe recipe) {
        if (!this.addUnlocked(recipe)) {
            return false;
        }
        this.sendPacket(UnlockedRecipesPacket.UnlockedRecipesPacketType.NEWLY_UNLOCKED, List.of(recipe.getRecipeId()));
        return true;
    }

    /**
     * Removes a recipe from the book and hides it on the client again.
     *
     * @param recipe the recipe to lock
     * @return {@code false} when it was not unlocked in the first place
     */
    public boolean lock(@NotNull Recipe recipe) {
        if (!this.unlockedRecipes.remove(recipe.getRecipeId())) {
            return false;
        }
        this.sendPacket(UnlockedRecipesPacket.UnlockedRecipesPacketType.REMOVE_UNLOCKED, List.of(recipe.getRecipeId()));
        return true;
    }

    /**
     * Empties the book and tells the client to forget everything it had unlocked.
     */
    public void reset() {
        this.unlockedRecipes.clear();
        this.sendPacket(UnlockedRecipesPacket.UnlockedRecipesPacketType.REMOVE_ALL, List.of());
    }

    @Override
    public void onInventoryChanged(Inventory inventory, Item oldItem, int slot) {
        if (!this.discovering) {
            return;
        }

        final Item current = inventory.getUnclonedItem(slot);

        if (!current.isNull()) {
            final Set<String> candidates = Registries.RECIPE.getUnlockIndex().getCandidates(current.getId());
            if (!candidates.isEmpty()) this.unlockAll(candidates);
        }

        this.checkPlayerHasManyItems();
    }

    public void updateWaterState() {
        if (!this.discovering) {
            return;
        }

        final boolean inWater = this.player.isTouchingWater();

        if (inWater && !this.wasInWater) {
            this.unlockAll(Registries.RECIPE.getUnlockIndex().getPlayerInWaterRecipes());
        }

        this.wasInWater = inWater;
    }

    public void onPlayerHasManyItems() {
        if (!this.discovering) return;

        this.unlockAll(Registries.RECIPE.getUnlockIndex().getPlayerHasManyItemsRecipes());
    }

    private boolean hasManyItems() {
        final HumanInventory inventory = this.player.getInventory();
        int occupiedSlots = 0;

        for (int slot = 0; slot < HumanInventory.ARMORS_INDEX; slot++) {
            if (!inventory.getUnclonedItem(slot).isNull()) {
                occupiedSlots++;
                if (occupiedSlots >= 10) return true;
            }
        }

        return false;
    }

    private void checkPlayerHasManyItems() {
        if (this.hasManyItems()) {
            this.onPlayerHasManyItems();
        }
    }

    private void discoverFromInventory() {
        final RecipeUnlockIndex index = Registries.RECIPE.getUnlockIndex();
        final Set<String> candidates = new ObjectOpenHashSet<>();
        final HumanInventory inventory = this.player.getInventory();
        for (int slot = 0; slot < HumanInventory.ARMORS_INDEX; slot++) {
            final Item item = inventory.getUnclonedItem(slot);
            if (!item.isNull()) {
                candidates.addAll(index.getCandidates(item.getId()));
            }
        }
        if (!candidates.isEmpty()) {
            this.unlockAll(candidates);
        }
    }

    private void addAllWithoutPacket(Collection<String> candidates) {
        for (String recipeId : candidates) {
            if (this.unlockedRecipes.contains(recipeId)) continue;

            final Recipe recipe = Registries.RECIPE.get(recipeId);
            if (recipe == null) continue;
            this.addUnlocked(recipe);
        }
    }

    private void unlockAll(Collection<String> candidates) {
        List<String> newlyUnlocked = null;
        for (String recipeId : candidates) {
            if (this.unlockedRecipes.contains(recipeId)) {
                continue;
            }
            final Recipe recipe = Registries.RECIPE.get(recipeId);
            if (recipe == null || !this.addUnlocked(recipe)) {
                continue;
            }
            if (newlyUnlocked == null) {
                newlyUnlocked = new ObjectArrayList<>();
            }
            newlyUnlocked.add(recipeId);
        }
        if (newlyUnlocked != null) {
            this.sendPacket(UnlockedRecipesPacket.UnlockedRecipesPacketType.NEWLY_UNLOCKED, newlyUnlocked);
        }
    }

    private boolean addUnlocked(Recipe recipe) {
        if (!this.discovering || this.unlockedRecipes.contains(recipe.getRecipeId())) {
            return false;
        }
        final PlayerUnlockRecipeEvent event = new PlayerUnlockRecipeEvent(this.player, recipe);
        this.player.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        this.unlockedRecipes.add(recipe.getRecipeId());
        return true;
    }

    private void sendPacket(UnlockedRecipesPacket.UnlockedRecipesPacketType type, Collection<String> recipeIds) {
        if (!this.discovering) {
            return;
        }
        final UnlockedRecipesPacket packet = new UnlockedRecipesPacket();
        packet.setType(type);
        packet.getUnlockedRecipesList().addAll(recipeIds);
        this.player.sendPacket(packet);
    }
}
