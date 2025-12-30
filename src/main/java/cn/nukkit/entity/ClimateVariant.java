package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.BiomeRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;


public interface ClimateVariant {

    String PROPERTY_STATE = "minecraft:climate_variant";

    String[] coldTags = new String[]{BiomeTags.TAIGA, BiomeTags.EXTREME_HILLS, BiomeTags.FROZEN, BiomeTags.DEEP_DOWN, BiomeTags.THE_END};
    String[] warmTags = new String[]{BiomeTags.SAVANNA, BiomeTags.JUNGLE, BiomeTags.MESA, BiomeTags.DESERT, BiomeTags.LUKEWARM, BiomeTags.SWAMP, BiomeTags.NETHER};

    default Variant getBiomeVariant(int biomeId) {
        BiomeDefinition definition = Registries.BIOME.get(biomeId);
        Set<String> tags = definition.getTags();
        for(String tag : coldTags) if(tags.contains(tag)) return Variant.COLD;
        for(String tag : warmTags) if(tags.contains(tag)) return Variant.WARM;
        return Variant.TEMPERATE;
    }

    default Variant getVariant() {
        if(this instanceof Entity entity) {
            String var = entity.getEnumEntityProperty(PROPERTY_STATE);
            if(var == null) return null;
            return Arrays.stream(Variant.VALUES).filter(variant -> variant.getName().equals(var)).findFirst().get();
        }
        return null;
    }

    default void setVariant(Variant variant) {
        if(this instanceof Entity entity) {
            entity.setEnumEntityProperty(PROPERTY_STATE, variant.getName());
            entity.sendData(entity.getViewers().values().toArray(Player[]::new));
            entity.namedTag.putString("variant", variant.getName());
        }
    }

    enum Variant {

        TEMPERATE("temperate"),
        WARM("warm"),
        COLD("cold");

        @Getter
        private final String name;
        Variant(String s) {
            name = s;
        }

        public static Variant get(String name) {
            return Arrays.stream(Variant.VALUES).filter(variant -> variant.getName().equals(name)).findFirst().get();
        }

        public final static Variant[] VALUES = values();
    }

}
