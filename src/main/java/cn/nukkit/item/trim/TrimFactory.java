package cn.nukkit.item.trim;

import cn.nukkit.network.protocol.types.TrimMaterial;
import cn.nukkit.network.protocol.types.TrimPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 * @date {2023/8/9} {21:32}
 */
public class TrimFactory {

    public static final List<TrimMaterial> trimMaterials = new ArrayList<>();

    public static final List<TrimPattern> trimPatterns = new ArrayList<>();

    static {
        trimPatterns.add(new TrimPattern("minecraft:ward_armor_trim_smithing_template", "ward"));
        trimPatterns.add(new TrimPattern("minecraft:sentry_armor_trim_smithing_template", "sentry"));
        trimPatterns.add(new TrimPattern("minecraft:snout_armor_trim_smithing_template", "snout"));
        trimPatterns.add(new TrimPattern("minecraft:dune_armor_trim_smithing_template", "dune"));
        trimPatterns.add(new TrimPattern("minecraft:spire_armor_trim_smithing_template", "spire"));
        trimPatterns.add(new TrimPattern("minecraft:tide_armor_trim_smithing_template", "tide"));
        trimPatterns.add(new TrimPattern("minecraft:wild_armor_trim_smithing_template", "wild"));
        trimPatterns.add(new TrimPattern("minecraft:rib_armor_trim_smithing_template", "rib"));
        trimPatterns.add(new TrimPattern("minecraft:coast_armor_trim_smithing_template", "coast"));
        trimPatterns.add(new TrimPattern("minecraft:shaper_armor_trim_smithing_template", "shaper"));
        trimPatterns.add(new TrimPattern("minecraft:eye_armor_trim_smithing_template", "eye"));
        trimPatterns.add(new TrimPattern("minecraft:vex_armor_trim_smithing_template", "vex"));
        trimPatterns.add(new TrimPattern("minecraft:silence_armor_trim_smithing_template", "silence"));
        trimPatterns.add(new TrimPattern("minecraft:wayfinder_armor_trim_smithing_template", "wayfinder"));
        trimPatterns.add(new TrimPattern("minecraft:raiser_armor_trim_smithing_template", "raiser"));
        trimPatterns.add(new TrimPattern("minecraft:host_armor_trim_smithing_template", "host"));
        
        trimMaterials.add(new TrimMaterial("quartz", "§h", "minecraft:quartz"));
        trimMaterials.add(new TrimMaterial("iron", "§i", "minecraft:iron_ingot"));
        trimMaterials.add(new TrimMaterial("netherite", "§j", "minecraft:netherite_ingot"));
        trimMaterials.add(new TrimMaterial("redstone", "§m", "minecraft:redstone"));
        trimMaterials.add(new TrimMaterial("copper", "§n", "minecraft:copper_ingot"));
        trimMaterials.add(new TrimMaterial("gold", "§p", "minecraft:gold_ingot"));
        trimMaterials.add(new TrimMaterial("emerald", "§q", "minecraft:emerald"));
        trimMaterials.add(new TrimMaterial("diamond", "§s", "minecraft:diamond"));
        trimMaterials.add(new TrimMaterial("lapis", "§t", "minecraft:lapis_lazuli"));
        trimMaterials.add(new TrimMaterial("amethyst", "§u", "minecraft:amethyst_shard"));
    }
}
