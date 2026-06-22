package cn.nukkit.entity.data.property;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.packet.SyncActorPropertyPacket;

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
    private static final List<NbtMap> entityPropertyCache = new ArrayList<>();
    private static NbtMap playerPropertyCache = NbtMap.EMPTY;

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
            List<NbtMap> listProperty = buildPropertyList(entry.getValue());
            NbtMap tag = NbtMap.builder().putList(PROPERTIES_KEY, NbtType.COMPOUND, listProperty).putString("type", entry.getKey()).build();
            entityPropertyCache.add(tag);
        }
    }

    public static void buildPlayerProperty() {
        List<EntityProperty> properties = entityPropertyMap.get(PLAYER_KEY);
        if (properties == null) {
            playerPropertyCache = NbtMap.EMPTY;
            return;
        }
        List<NbtMap> listProperty = buildPropertyList(properties);
        playerPropertyCache = NbtMap.builder().putList(PROPERTIES_KEY, NbtType.COMPOUND, listProperty).putString("type", PLAYER_KEY).build();
    }

    public static List<SyncActorPropertyPacket> getEntityPropertyCache() {
        return entityPropertyCache.stream().map(nbtMap -> {
            final SyncActorPropertyPacket pk = new SyncActorPropertyPacket();
            pk.setPropertyData(nbtMap);
            return pk;
        }).toList();
    }

    public static NbtMap getPlayerPropertyCache() {
        return playerPropertyCache;
    }

    public static List<EntityProperty> getEntityProperty(String identifier) {
        return entityPropertyMap.getOrDefault(identifier, new ArrayList<>());
    }

    public String getIdentifier() {
        return identifier;
    }

    public abstract NbtMap populateTag(NbtMap tag);

    public abstract boolean isClientSync();

    private static List<NbtMap> buildPropertyList(List<EntityProperty> properties) {
        final List<NbtMap> listProperty = new ObjectArrayList<>();
        for (EntityProperty entityProperty : properties) {
            // Filter out properties not meant to sync to client
            if (entityProperty instanceof BooleanEntityProperty boolProp && !boolProp.isClientSync()) continue;
            if (entityProperty instanceof EnumEntityProperty enumProp && !enumProp.isClientSync()) continue;

            NbtMap propertyTag = NbtMap.builder()
                    .putString("name", entityProperty.getIdentifier())
                    .build();
            propertyTag = entityProperty.populateTag(propertyTag);
            listProperty.add(propertyTag);
        }
        return listProperty;
    }
}
