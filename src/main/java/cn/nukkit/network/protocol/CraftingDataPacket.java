package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.recipe.*;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Nukkit Project Team
 */
@ToString
public class CraftingDataPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CRAFTING_DATA_PACKET;
    public static final String CRAFTING_TAG_CRAFTING_TABLE = "crafting_table";
    public static final String CRAFTING_TAG_CARTOGRAPHY_TABLE = "cartography_table";
    public static final String CRAFTING_TAG_STONECUTTER = "stonecutter";
    public static final String CRAFTING_TAG_FURNACE = "furnace";
    public static final String CRAFTING_TAG_CAMPFIRE = "campfire";
    public static final String CRAFTING_TAG_SOUL_CAMPFIRE = "soul_campfire";
    public static final String CRAFTING_TAG_BLAST_FURNACE = "blast_furnace";
    public static final String CRAFTING_TAG_SMOKER = "smoker";
    public static final String CRAFTING_TAG_SMITHING_TABLE = "smithing_table";

    private final List<Recipe> entries = new ArrayList<>();
    private final List<BrewingRecipe> brewingEntries = new ArrayList<>();
    private final List<ContainerRecipe> containerEntries = new ArrayList<>();
    public boolean cleanRecipes;

    public void addNetworkIdRecipe(List<Recipe> recipes) {
        entries.addAll(recipes);
    }

    public void addFurnaceRecipe(FurnaceRecipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addSmokerRecipe(SmokerRecipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addBlastFurnaceRecipe(BlastFurnaceRecipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addCampfireRecipeRecipe(CampfireRecipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addBrewingRecipe(BrewingRecipe... recipe) {
        Collections.addAll(brewingEntries, recipe);
    }

    public void addContainerRecipe(ContainerRecipe... recipe) {
        Collections.addAll(containerEntries, recipe);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeUnsignedVarInt(entries.size());

        int recipeNetworkId = 1;
        for (Recipe recipe : entries) {
            byteBuf.writeVarInt(recipe.getType().networkType);
            switch (recipe.getType()) {
                case STONECUTTER -> {
                    StonecutterRecipe stonecutter = (StonecutterRecipe) recipe;
                    byteBuf.writeString(stonecutter.getRecipeId());
                    byteBuf.writeUnsignedVarInt(1);
                    byteBuf.writeRecipeIngredient(new DefaultDescriptor(stonecutter.getIngredient()));
                    byteBuf.writeUnsignedVarInt(1);
                    byteBuf.writeSlot(stonecutter.getResult(), true);
                    byteBuf.writeUUID(stonecutter.getUUID());
                    byteBuf.writeString(CRAFTING_TAG_STONECUTTER);
                    byteBuf.writeVarInt(stonecutter.getPriority());
                    byteBuf.writeUnsignedVarInt(recipeNetworkId++);
                }
                case SHAPELESS, CARTOGRAPHY, SHULKER_BOX -> {
                    ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
                    byteBuf.writeString(shapeless.getRecipeId());
                    List<ItemDescriptor> ingredients = shapeless.getIngredients();
                    byteBuf.writeUnsignedVarInt(ingredients.size());
                    for (var ingredient : ingredients) {
                        byteBuf.writeRecipeIngredient(ingredient);
                    }
                    byteBuf.writeUnsignedVarInt(1);
                    byteBuf.writeSlot(shapeless.getResult(), true);
                    byteBuf.writeUUID(shapeless.getUUID());
                    switch (recipe.getType()) {
                        case CARTOGRAPHY -> byteBuf.writeString(CRAFTING_TAG_CARTOGRAPHY_TABLE);
                        case SHAPELESS, SHULKER_BOX -> byteBuf.writeString(CRAFTING_TAG_CRAFTING_TABLE);
                    }
                    byteBuf.writeVarInt(shapeless.getPriority());
                    byteBuf.writeUnsignedVarInt(recipeNetworkId++);
                }
                case SMITHING_TRANSFORM -> {
                    SmithingTransformRecipe smithing = (SmithingTransformRecipe) recipe;
                    byteBuf.writeString(smithing.getRecipeId());
                    //todo 1.19.80还没有模板，下个版本再加入
                    byteBuf.writeRecipeIngredient(smithing.getTemplate());
                    byteBuf.writeRecipeIngredient(smithing.getBase());
                    byteBuf.writeRecipeIngredient(smithing.getAddition());
                    byteBuf.writeSlot(smithing.getResult(), true);
                    byteBuf.writeString(CRAFTING_TAG_SMITHING_TABLE);
                    byteBuf.writeUnsignedVarInt(recipeNetworkId++);
                }
                case SMITHING_TRIM -> {
                    //todo
                }
                case SHAPED -> {
                    ShapedRecipe shaped = (ShapedRecipe) recipe;
                    byteBuf.writeString(shaped.getRecipeId());
                    byteBuf.writeVarInt(shaped.getWidth());
                    byteBuf.writeVarInt(shaped.getHeight());
                    for (int z = 0; z < shaped.getHeight(); ++z) {
                        for (int x = 0; x < shaped.getWidth(); ++x) {
                            byteBuf.writeRecipeIngredient(shaped.getIngredient(x, z));
                        }
                    }
                    List<Item> results = shaped.getResults();
                    byteBuf.writeUnsignedVarInt(results.size());
                    for (Item output : results) {
                        byteBuf.writeSlot(output, true);
                    }
                    byteBuf.writeUUID(shaped.getUUID());
                    byteBuf.writeString(CRAFTING_TAG_CRAFTING_TABLE);
                    byteBuf.writeVarInt(shaped.getPriority());
                    byteBuf.writeUnsignedVarInt(recipeNetworkId++);
                }
                case MULTI -> {
                    byteBuf.writeUUID(((MultiRecipe) recipe).getId());
                    byteBuf.writeUnsignedVarInt(recipeNetworkId++);
                }
                case FURNACE, FURNACE_DATA, SMOKER, SMOKER_DATA, BLAST_FURNACE, BLAST_FURNACE_DATA, CAMPFIRE, CAMPFIRE_DATA, SOUL_CAMPFIRE_DATA, SOUL_CAMPFIRE -> {
                    SmeltingRecipe smelting = (SmeltingRecipe) recipe;
                    Item input = smelting.getInput().toItem();
                    byteBuf.writeVarInt(input.getRuntimeId());
                    if (recipe.getType().name().endsWith("_DATA")) {
                        byteBuf.writeVarInt(input.getDamage());
                    }
                    byteBuf.writeSlot(smelting.getResult(), true);
                    switch (recipe.getType()) {
                        case FURNACE, FURNACE_DATA -> byteBuf.writeString(CRAFTING_TAG_FURNACE);
                        case SMOKER, SMOKER_DATA -> byteBuf.writeString(CRAFTING_TAG_SMOKER);
                        case BLAST_FURNACE, BLAST_FURNACE_DATA -> byteBuf.writeString(CRAFTING_TAG_BLAST_FURNACE);
                        case CAMPFIRE, CAMPFIRE_DATA -> byteBuf.writeString(CRAFTING_TAG_CAMPFIRE);
                        case SOUL_CAMPFIRE_DATA, SOUL_CAMPFIRE -> byteBuf.writeString(CRAFTING_TAG_SOUL_CAMPFIRE);
                    }
                }
            }
        }

        byteBuf.writeUnsignedVarInt(this.brewingEntries.size());
        for (BrewingRecipe recipe : brewingEntries) {
            byteBuf.writeVarInt(recipe.getInput().getRuntimeId());
            byteBuf.writeVarInt(recipe.getInput().getDamage());
            byteBuf.writeVarInt(recipe.getIngredient().getRuntimeId());
            byteBuf.writeVarInt(recipe.getIngredient().getDamage());
            byteBuf.writeVarInt(recipe.getResult().getRuntimeId());
            byteBuf.writeVarInt(recipe.getResult().getDamage());
        }

        byteBuf.writeUnsignedVarInt(this.containerEntries.size());
        for (ContainerRecipe recipe : containerEntries) {
            byteBuf.writeVarInt(recipe.getInput().getRuntimeId());
            byteBuf.writeVarInt(recipe.getIngredient().getRuntimeId());
            byteBuf.writeVarInt(recipe.getResult().getRuntimeId());
        }

        byteBuf.writeUnsignedVarInt(0); // Material reducers size

        byteBuf.writeBoolean(cleanRecipes);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
