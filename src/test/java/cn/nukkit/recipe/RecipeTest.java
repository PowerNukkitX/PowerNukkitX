package cn.nukkit.recipe;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.registry.Registries;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RecipeTest {
    @BeforeAll
    @SneakyThrows
    static void before() {
        Registries.POTION.init();
        Registries.BLOCKSTATE_ITEMMETA.init();
        Registries.BLOCK.init();
        Registries.ITEM.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.RECIPE.init();
    }

    @Test
    void test_getNetworkIdRecipeList() {
        Registries.RECIPE.getNetworkIdRecipeList();
    }

    @Test
    void test_findBlastFurnaceRecipe() {
        BlastFurnaceRecipe blastFurnaceRecipe = Registries.RECIPE.findBlastFurnaceRecipe(Item.get(ItemID.IRON_PICKAXE));
        Assertions.assertEquals("minecraft:iron_nugget", blastFurnaceRecipe.getResult().getId());
    }

    @Test
    void test_findShapelessRecipe() {
        ShapelessRecipe shapelessRecipe = Registries.RECIPE.findShapelessRecipe(Item.get(BlockID.BLUE_SHULKER_BOX), Item.get(ItemID.BROWN_DYE));
        Assertions.assertEquals("minecraft:brown_shulker_box", shapelessRecipe.getResult().getId());
    }

    @Test
    void test_tryShrinkMatrix1() {
        Item[] item1 = List.of(Item.AIR, Item.AIR, Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[] item2 = List.of(Item.AIR, Item.get(ItemID.PLANKS), Item.get(ItemID.DIAMOND)).toArray(Item.EMPTY_ARRAY);
        Item[] item3 = List.of(Item.AIR, Item.get(ItemID.TORCHFLOWER_SEEDS), Item.get(ItemID.MELON_SEEDS)).toArray(Item.EMPTY_ARRAY);
        Item[][] data = new Item[][]{item1, item2, item3};
        Input input = new Input(3, 3, data);
        ShapedRecipe.tryShrinkMatrix(input);
        Item[][] items = input.getData();

        Item[] expected1 = List.of(Item.get(ItemID.PLANKS), Item.get(ItemID.DIAMOND)).toArray(Item.EMPTY_ARRAY);
        Item[] expected2 = List.of(Item.get(ItemID.TORCHFLOWER_SEEDS), Item.get(ItemID.MELON_SEEDS)).toArray(Item.EMPTY_ARRAY);
        Item[][] expected = new Item[][]{expected1, expected2};
        Assertions.assertArrayEquals(expected, items);
    }

    @Test
    void test_tryShrinkMatrix2() {
        Item[] item1 = List.of(Item.get(ItemID.PLANKS), Item.get(ItemID.DIAMOND), Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[] item2 = List.of(Item.get(ItemID.TORCHFLOWER_SEEDS), Item.get(ItemID.MELON_SEEDS), Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[] item3 = List.of(Item.AIR, Item.AIR, Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[][] data = new Item[][]{item1, item2, item3};
        Input input = new Input(3, 3, data);
        ShapedRecipe.tryShrinkMatrix(input);
        Item[][] items = input.getData();

        Item[] expected1 = List.of(Item.get(ItemID.PLANKS), Item.get(ItemID.DIAMOND)).toArray(Item.EMPTY_ARRAY);
        Item[] expected2 = List.of(Item.get(ItemID.TORCHFLOWER_SEEDS), Item.get(ItemID.MELON_SEEDS)).toArray(Item.EMPTY_ARRAY);
        Item[][] expected = new Item[][]{expected1, expected2};
        Assertions.assertArrayEquals(expected, items);
    }

    @Test
    void test_tryShrinkMatrix3() {
        Item[] item1 = List.of(Item.get(ItemID.PLANKS), Item.get(ItemID.DIAMOND), Item.get(ItemID.TORCHFLOWER_SEEDS)).toArray(Item.EMPTY_ARRAY);
        Item[] item2 = List.of(Item.AIR, Item.get(ItemID.STICK), Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[] item3 = List.of(Item.AIR, Item.get(ItemID.STICK), Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[][] data = new Item[][]{item1, item2, item3};
        Input input = new Input(3, 3, data);
        ShapedRecipe.tryShrinkMatrix(input);
        Item[][] items = input.getData();
        Item[][] expected = items.clone();
        Assertions.assertArrayEquals(expected, items);
    }

    @Test
    void test_tryShrinkMatrix4() {
        Item[] item1 = List.of(Item.get(ItemID.STICK), Item.AIR, Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[] item2 = List.of(Item.get(ItemID.STICK), Item.AIR, Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[] item3 = List.of(Item.AIR, Item.AIR, Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[][] data = new Item[][]{item1, item2, item3};
        Input input = new Input(3, 3, data);
        ShapedRecipe.tryShrinkMatrix(input);
        Item[][] items = input.getData();
        //                                row0                      row1
        Item[][] expected = new Item[][]{{Item.get(ItemID.STICK)}, {Item.get(ItemID.STICK)}};
        Assertions.assertArrayEquals(expected, items);
    }

    @Test
    void test_tryShrinkMatrix5() {
        Item[] item1 = List.of(Item.AIR, Item.AIR, Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[] item2 = List.of(Item.AIR, Item.AIR, Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[] item3 = List.of(Item.get(ItemID.PLANKS), Item.get(ItemID.PLANKS), Item.get(ItemID.PLANKS)).toArray(Item.EMPTY_ARRAY);
        Item[][] data = new Item[][]{item1, item2, item3};
        Input input = new Input(3, 3, data);
        ShapedRecipe.tryShrinkMatrix(input);
        Item[][] items = input.getData();
        //                                row0                      row1                          row2
        Item[][] expected = new Item[][]{{Item.get(ItemID.PLANKS), Item.get(ItemID.PLANKS), Item.get(ItemID.PLANKS)}};
        Assertions.assertArrayEquals(expected, items);
    }

    @Test
    void test_tryShrinkMatrix6() {
        Item[] item1 = List.of(Item.AIR, Item.AIR, Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[] item2 = List.of(Item.get(ItemID.PLANKS), Item.AIR, Item.get(ItemID.PLANKS)).toArray(Item.EMPTY_ARRAY);
        Item[] item3 = List.of(Item.get(ItemID.PLANKS), Item.AIR, Item.get(ItemID.PLANKS)).toArray(Item.EMPTY_ARRAY);
        Item[][] data = new Item[][]{item1, item2, item3};
        Input input = new Input(3, 3, data);
        ShapedRecipe.tryShrinkMatrix(input);
        Item[][] items = input.getData();
        //         row0                      row1              row2
        Item[][] expected = new Item[][]{
                {Item.get(ItemID.PLANKS), Item.AIR, Item.get(ItemID.PLANKS)},
                {Item.get(ItemID.PLANKS), Item.AIR, Item.get(ItemID.PLANKS)}
        };
        Assertions.assertArrayEquals(expected, items);
    }

    @Test
    void test_tryShrinkMatrix7() {
        Item[] item1 = List.of(Item.AIR, Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[] item2 = List.of(Item.get(ItemID.PLANKS), Item.AIR).toArray(Item.EMPTY_ARRAY);
        Item[][] data = new Item[][]{item1, item2};
        Input input = new Input(2, 2, data);
        ShapedRecipe.tryShrinkMatrix(input);
        Item[][] items = input.getData();
        //         row0
        Item[][] expected = new Item[][]{
                {Item.get(ItemID.PLANKS)}
        };
        Assertions.assertArrayEquals(expected, items);
    }
}
