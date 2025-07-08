package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.SyncEntityPropertyPacket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Peng_Lx
 */
public abstract class EntityProperty {
    private static final String PLAYER_KEY = "minecraft:player";
    private static final String PROPERTIES_KEY = "properties";

    private static final Map<String, List<EntityProperty>> entityPropertyMap = new LinkedHashMap<>();
    private static final List<CompoundTag> entityPropertyCache = new ArrayList<>();
    private static CompoundTag playerPropertyCache = new CompoundTag();

    private final String identifier;

    public EntityProperty(String identifier) {
        this.identifier = identifier;
    }

    public static boolean register(String entityIdentifier, EntityProperty property) {
        List<EntityProperty> entityProperties = entityPropertyMap.getOrDefault(entityIdentifier, new ArrayList<>());
        for (EntityProperty entityProperty : entityProperties) {
            if (Objects.equals(entityProperty.identifier, property.identifier)) return false;
        }
        entityProperties.add(property);
        entityPropertyMap.put(entityIdentifier, entityProperties);
        return true;
    }

    public static void buildEntityProperty() {
        entityPropertyCache.clear();
        for (Map.Entry<String, List<EntityProperty>> entry : entityPropertyMap.entrySet()) {
            ListTag<CompoundTag> listProperty = buildPropertyList(entry.getValue());
            CompoundTag tag = new CompoundTag().putList(PROPERTIES_KEY, listProperty).putString("type", entry.getKey());
            entityPropertyCache.add(tag);
        }
    }

    public static void buildPlayerProperty() {
        List<EntityProperty> properties = entityPropertyMap.get(PLAYER_KEY);
        if (properties == null) {
            playerPropertyCache = new CompoundTag();
            return;
        }
        ListTag<CompoundTag> listProperty = buildPropertyList(properties);
        playerPropertyCache = new CompoundTag().putList(PROPERTIES_KEY, listProperty).putString("type", PLAYER_KEY);
    }

    public static List<SyncEntityPropertyPacket> getEntityPropertyCache() {
        return entityPropertyCache.stream().map(SyncEntityPropertyPacket::new).toList();
    }

    public static CompoundTag getPlayerPropertyCache() {
        return playerPropertyCache;
    }

    public static List<EntityProperty> getEntityProperty(String identifier) {
        return entityPropertyMap.getOrDefault(identifier, new ArrayList<>());
    }

    public String getIdentifier() {
        return identifier;
    }

    public abstract void populateTag(CompoundTag tag);

    public abstract boolean isClientSync();

    private static ListTag<CompoundTag> buildPropertyList(List<EntityProperty> properties) {
        ListTag<CompoundTag> listProperty = new ListTag<>();
        for (EntityProperty entityProperty : properties) {
            // Filter out properties not meant to sync to client
            if (entityProperty instanceof BooleanEntityProperty boolProp && !boolProp.isClientSync()) continue;
            if (entityProperty instanceof EnumEntityProperty enumProp && !enumProp.isClientSync()) continue;

            CompoundTag propertyTag = new CompoundTag();
            propertyTag.putString("name", entityProperty.getIdentifier());
            entityProperty.populateTag(propertyTag);
            listProperty.add(propertyTag);
        }
        return listProperty;
    }
}
