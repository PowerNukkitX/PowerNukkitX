package cn.nukkit.level.generator.terra.handles;

import cn.nukkit.block.BlockAir;
import cn.nukkit.level.generator.terra.PNXAdapter;
import cn.nukkit.level.generator.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.generator.terra.delegate.PNXEntityType;
import cn.nukkit.level.generator.terra.mappings.JeBlockState;
import cn.nukkit.level.generator.terra.mappings.MappingRegistries;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.entity.EntityType;
import com.dfsek.terra.api.handle.WorldHandle;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class PNXWorldHandle implements WorldHandle {
    public static final PNXBlockStateDelegate AIR = new PNXBlockStateDelegate(BlockAir.STATE);

    @Override
    @NotNull
    public BlockState createBlockState(@NotNull String s) {
        if (!s.startsWith("minecraft:")) {
            s = "minecraft:" + s;
        }
        //修正部分属性缺失以能正确获取对应基岩版映射
        switch (s) {
            case "minecraft:water" -> s = "minecraft:water[level=0]";
            case "minecraft:lava" -> s = "minecraft:lava[level=0]";
            case "minecraft:deepslate" -> s = "minecraft:deepslate[axis=y]";
            case "minecraft:grass_block" -> s = "minecraft:grass_block[snowy=false]";
            case "minecraft:podzol" -> s = "minecraft:podzol[snowy=false]";
            case "minecraft:mycelium" -> s = "minecraft:mycelium[snowy=false]";
            case "minecraft:sugar_cane" -> s = "minecraft:sugar_cane[age=0]";
            case "minecraft:brown_mushroom_block[down=false]" ->
                    s = "minecraft:brown_mushroom_block[down=false,east=true,north=true,south=true,up=true,west=true]";
            case "minecraft:cactus" -> s = "minecraft:cactus[age=0]";
            case "minecraft:mushroom_stem" ->
                    s = "minecraft:mushroom_stem[down=true,east=true,north=true,south=true,up=true,west=true]";
            case "minecraft:jungle_wood" -> s = "minecraft:jungle_wood[axis=y]";
            case "minecraft:redstone_ore" -> s = "minecraft:redstone_ore[lit=false]";
            case "minecraft:deepslate_redstone_ore" -> s = "minecraft:deepslate_redstone_ore[lit=false]";
            case "minecraft:basalt" -> s = "minecraft:basalt[axis=y]";
            case "minecraft:snow" -> s = "minecraft:snow[layers=1]";
            case "minecraft:cave_vines" -> s = "minecraft:cave_vines[age=0,berries=true]";
            case "minecraft:polished_basalt" -> s = "minecraft:polished_basalt[axis=y]";
            case "minecraft:azalea_leaves[persistent=true]" ->
                    s = "minecraft:azalea_leaves[distance=1,persistent=true,waterlogged=false]";
            case "minecraft:flowering_azalea_leaves[persistent=true]" ->
                    s = "minecraft:flowering_azalea_leaves[distance=1,persistent=true,waterlogged=false]";
            case "minecraft:deepslate_tile_wall" ->
                    s = "minecraft:deepslate_tile_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]";
            case "minecraft:farmland" -> s = "minecraft:farmland[moisture=7]";
            case "minecraft:cobbled_deepslate_wall" ->
                    s = "minecraft:cobbled_deepslate_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]";
            case "minecraft:polished_deepslate_wall" ->
                    s = "minecraft:polished_deepslate_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]";
            case "minecraft:sculk_shrieker[can_summon=true]" ->
                    s = "minecraft:sculk_shrieker[can_summon=true,shrieking=false,waterlogged=false]";
            case "minecraft:sculk_catalyst" -> s = "minecraft:sculk_catalyst[bloom=false]";
            case "minecraft:dark_oak_fence" ->
                    s = "minecraft:dark_oak_fence[east=false,north=false,south=false,waterlogged=false,west=false]";
            case "minecraft:sculk_sensor" ->
                    s = "minecraft:sculk_sensor[power=0,sculk_sensor_phase=inactive,waterlogged=false]";
            case "minecraft:deepslate_tile_stairs" ->
                    s = "minecraft:deepslate_tile_stairs[facing=north,half=top,shape=straight,waterlogged=false]";
        }
        JeBlockState jeBlockState = new JeBlockState(s);
        var jeBlockIdentifier = jeBlockState.getIdentifier();
        var jeBlockAttributes = jeBlockState.getAttributes();
        if (jeBlockIdentifier.contains("log") || jeBlockIdentifier.contains("wood")) {
            jeBlockAttributes.putIfAbsent("axis", "y");
        }
        if (jeBlockIdentifier.contains("minecraft:leaves")) {
            jeBlockAttributes.putIfAbsent("distance", "7");
            jeBlockAttributes.putIfAbsent("persistent", "true");
        }
        if (jeBlockIdentifier.equals("minecraft:bee_nest"))
            jeBlockAttributes.putIfAbsent("honey_level", "0");
        if (jeBlockIdentifier.equals("minecraft:vine")) {
            jeBlockAttributes.putIfAbsent("east", "false");
            jeBlockAttributes.putIfAbsent("north", "false");
            jeBlockAttributes.putIfAbsent("south", "false");
            jeBlockAttributes.putIfAbsent("up", "false");
            jeBlockAttributes.putIfAbsent("west", "false");
        }
        var bedrockBlockState = MappingRegistries.BLOCKS.getPNXBlock(jeBlockState);
        //若未获取到属性，排除掉含水再次尝试
        if (bedrockBlockState == null) {
            jeBlockState.setEqualsIgnoreWaterlogged(true);
            bedrockBlockState = MappingRegistries.BLOCKS.getPNXBlock(jeBlockState);
        }
        //排除所有属性再次尝试
        if (bedrockBlockState == null) {
            jeBlockState.setEqualsIgnoreAttributes(true);
            bedrockBlockState = MappingRegistries.BLOCKS.getPNXBlock(jeBlockState);
        }
        if (bedrockBlockState == null) {
            log.error("[Terra] Can't find block mapping for" + jeBlockState);
            return AIR;
        }
        return PNXAdapter.adapt(bedrockBlockState);
    }

    @Override
    @NotNull
    public BlockState air() {
        return AIR;
    }

    @Override
    @NotNull
    public EntityType getEntity(@NotNull String s) {
        var entityType = new PNXEntityType(s);
        if (entityType.getHandle() == null) {
            throw new IllegalArgumentException("Unknown entity type!");
        }
        return entityType;
    }
}
