package org.powernukkitx.recipe.unlock;

import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.registry.Registries;

import java.util.ArrayList;
import java.util.List;

class RecipeResultPayloadTest {

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        Registries.RECIPE.init();
    }

    private static List<String> resultsCarryingDamageTag() {
        final List<String> offenders = new ArrayList<>();
        final var packet = Registries.RECIPE.getCraftingPacket();
        packet.getShapedRecipes().forEach(p -> p.getResults().forEach(res -> {
            if (carriesDamageTag(res)) offenders.add(p.getRecipeId());
        }));
        packet.getShapelessRecipes().forEach(p -> p.getResults().forEach(res -> {
            if (carriesDamageTag(res)) offenders.add(p.getRecipeId());
        }));
        packet.getSmithingTransformRecipes().forEach(p -> {
            if (carriesDamageTag(p.getResult())) offenders.add(p.getRecipeId());
        });
        return offenders;
    }

    private static boolean carriesDamageTag(ItemData data) {
        return data.getTag() != null && data.getTag().containsKey("Damage");
    }

    @Test
    void noRecipeResultCarriesADamageTag() {
        final List<String> offenders = resultsCarryingDamageTag();
        Assertions.assertTrue(offenders.isEmpty(),
            () -> offenders.size() + " recipe results still carry a Damage tag, e.g. " + offenders.stream().limit(5).toList());
    }

    @Test
    void woodenSwordReachesTheClientAsAPlainItem() {
        final var sword = Registries.RECIPE.getCraftingPacket().getShapedRecipes().stream()
            .filter(p -> p.getRecipeId().equals("minecraft:wooden_sword"))
            .findFirst()
            .orElseThrow();

        Assertions.assertEquals(1, sword.getResults().size());
        final ItemData result = sword.getResults().getFirst();
        Assertions.assertEquals(ItemID.WOODEN_SWORD, result.getDefinition().getIdentifier());
        Assertions.assertNull(result.getTag(), "a pristine wooden sword must reach the client without user data");
    }

    @Test
    void stripingOnlyAppliesToPristineDamageableItems() {
        // the item itself keeps mirroring its durability, only the recipe payload drops a zero tag
        final Item pristine = Item.get(ItemID.WOODEN_SWORD, 0, 1);
        Assertions.assertTrue(pristine.hasNbt());
        Assertions.assertNull(pristine.toRecipeNetwork().getTag());

        final Item worn = Item.get(ItemID.WOODEN_SWORD, 0, 1);
        worn.setDamage(17);
        Assertions.assertNotNull(worn.toRecipeNetwork().getTag());
        Assertions.assertEquals(17, worn.toRecipeNetwork().getTag().getInt("Damage"));
    }
}
