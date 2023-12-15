package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdater_1_16_0 implements BlockStateUpdater {
    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_16_0();

    private static int convertFacingDirectionToDirection(int facingDirection) {
        switch (facingDirection) {
            case 2:
                return 2;
            case 3:
            default:
                return 0;
            case 4:
                return 1;
            case 5:
                return 3;
        }
    }

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        context.addUpdater(1, 16, 0)
                .match("name", "jigsaw")
                .visit("states")
                .tryAdd("rotation", 0);

        context.addUpdater(1, 16, 0)
                .match("name", "minecraft:blue_fire")
                .edit("name", helper -> {
                    helper.replaceWith("name", "soul_fire");
                });

        context.addUpdater(1, 16, 0)
                .match("name", "minecraft:blue_nether_wart_block")
                .edit("name", helper -> {
                    helper.replaceWith("name", "warped_wart_block");
                });

        context.addUpdater(1, 16, 0)
                .match("name", "minecraft:shroomlight_block")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:shroomlight");
                });

        context.addUpdater(1, 16, 0)
                .match("name", "minecraft:weeping_vines_block")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:weeping_vines");
                });

        context.addUpdater(1, 16, 0)
                .match("name", "minecraft:basalt_block")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:basalt");
                });

        context.addUpdater(1, 16, 0)
                .match("name", "minecraft:polished_basalt_block")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:polished_basalt");
                });

        context.addUpdater(1, 16, 0)
                .match("name", "minecraft:soul_soil_block")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:soul_soil");
                });

        context.addUpdater(1, 16, 0)
                .match("name", "minecraft:target_block")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:target");
                });

        context.addUpdater(1, 16, 0)
                .match("name", "minecraft:crimson_trap_door")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:crimsom_trapdoor");
                });

        context.addUpdater(1, 16, 0)
                .match("name", "minecraft:lodestone_block")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:lodestone");
                });

        context.addUpdater(1, 16, 0)
                .match("name", "minecraft:twisted_vines_block")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:twisted_vines");
                });

        // This is not a vanilla state updater. In vanilla 1.16, the invalid block state is updated when the chunk is
        // loaded in so it can generate the connection data however the state set below should never occur naturally.
        // Checking for this block state instead means we don't have to break our loading system in order to support it.
        this.addLegacyWallUpdater(context, "minecraft:.+_wall");
        this.addLegacyWallUpdater(context, "minecraft:border_block");

        this.addWallUpdater(context, "minecraft:blackstone_wall");
        this.addWallUpdater(context, "minecraft:polished_blackstone_brick_wall");
        this.addWallUpdater(context, "minecraft:polished_blackstone_wall");

        this.addBeeHiveUpdater(context, "minecraft:beehive");
        this.addBeeHiveUpdater(context, "minecraft:bee_nest");

        this.addRequiredValueUpdater(context, "minecraft:pumpkin_stem", "facing_direction", 0);
        this.addRequiredValueUpdater(context, "minecraft:melon_stem", "facing_direction", 0);
    }

    private void addLegacyWallUpdater(CompoundTagUpdaterContext context, String name) {
        context.addUpdater(1, 16, 0)
                .regex("name", name)
                .tryEdit("states", helper -> {
                    Map<String, Object> states = helper.getCompoundTag();
                    states.put("wall_post_bit", (byte) 0);
                    states.put("wall_connection_type_north", "none");
                    states.put("wall_connection_type_east", "none");
                    states.put("wall_connection_type_south", "none");
                    states.put("wall_connection_type_west", "none");
                });
    }

    private void addWallUpdater(CompoundTagUpdaterContext context, String name) {
        context.addUpdater(1, 16, 0)
                .match("name", name)
                .visit("states")
                .remove("wall_block_type");
    }

    private void addBeeHiveUpdater(CompoundTagUpdaterContext context, String name) {
        context.addUpdater(1, 16, 0)
                .match("name", name)
                .visit("states")
                .edit("facing_direction", helper -> {
                    int facingDirection = (int) helper.getTag();
                    helper.replaceWith("direction", convertFacingDirectionToDirection(facingDirection));
                });
    }

    private void addRequiredValueUpdater(CompoundTagUpdaterContext contex, String name, String state, Object value) {
        contex.addUpdater(1, 16, 0)
                .match("name", name)
                .visit("states")
                .tryAdd(state, value);
    }
}
