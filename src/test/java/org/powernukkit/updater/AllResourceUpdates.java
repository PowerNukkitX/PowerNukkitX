package org.powernukkit.updater;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;

/**
 * @author joserobjr
 * @since 2021-10-23
 */
public class AllResourceUpdates {
    public static void main(String[] args) {
        /*
        Pre-requisites:
        - Download: https://github.com/pmmp/BedrockData/blob/master/canonical_block_states.nbt
            into: src/main/resources/canonical_block_states.nbt
        - Download: https://github.com/pmmp/BedrockData/blob/master/required_item_list.json
            into: src/test/resources/org/powernukkit/updater/dumps/pmmp/required_item_list.json
        - Run ProxyPass with export-data in config.yml set to true, the proxy pass must be
            pointing to a vanilla BDS server from https://www.minecraft.net/en-us/download/server/bedrock
        - Connect to the ProxyPass server with the last Minecraft Bedrock Edition client
        - Copy data/biome_definitions.dat from the proxy pass dump
            into: src/main/resources/biome_definitions.dat
        - Copy data/entity_identifiers.dat from the proxy pass dump
            into: src/main/resources/entity_identifiers.dat
        - Copy data/creativeitems.json from the proxy pass dump
            into: src/main/resources/creativeitems.json
        - Copy data/runtime_item_states.json from the proxy pass dump
            into: src/test/resources/org/powernukkit/updater/dumps/proxypass/runtime_item_states.json
        - Copy data/recipes.json from the proxy pass dump
            into: src/test/resources/org/powernukkit/updater/dumps/proxypass/runtime_item_states.json
        - Run src/test/java/org/powernukkit/tools/RuntimeItemIdUpdater.java
        - Run src/test/java/org/powernukkit/dumps/ItemIdDumper.java
        - Run src/test/java/org/powernukkit/dumps/RuntimeBlockStateDumper.java
         */

        new AllResourceUpdates().execute();
    }

    private void execute() {
        init();
    }

    private void init() {
        Block.init();
        Enchantment.init();
        Item.init();


    }
}
