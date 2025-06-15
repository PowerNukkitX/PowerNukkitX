package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.SyncEntityPropertyPacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Peng_Lx
 */
public abstract class EntityProperty {
    private static final String PLAYER_KEY = "minecraft:player";
    private static final String PROPERTIES_KEY = "properties";

    private static final Map<String, List<EntityProperty>> entityPropertyMap = new HashMap<>();
    private static final List<CompoundTag> nbtCache = new ArrayList<>();
    private static CompoundTag playerPropertyCache = new CompoundTag();

    private final String identifier;

    public EntityProperty(String identifier) {
        this.identifier = identifier;
    }
    public static void init() {
        try (var stream = EntityProperty.class.getClassLoader().getResourceAsStream("gamedata/proxypass/entity_properties.nbt")) {
            CompoundTag root = NBTIO.readCompressed(stream);
            root.getTags().values().forEach(uncast -> {
                if(uncast instanceof CompoundTag tag) {
                    ListTag<CompoundTag> properties = tag.getList("properties", CompoundTag.class);
                    for(CompoundTag property : properties.getAll()) {
                        String name = property.getString("name");
                        int type = property.getInt("type");
                        EntityProperty data = switch (type) {
                            case 0 -> new IntEntityProperty(name, property.getInt("min"), property.getInt("max"), property.getInt("min"));
                            case 1 -> new FloatEntityProperty(name, property.getInt("min"), property.getInt("max"), property.getInt("min"));
                            case 2 -> new BooleanEntityProperty(name, false);
                            case 3 -> {
                                List<String> enums = new ArrayList<>();
                                for(StringTag entry : property.getList("enum", StringTag.class).getAll()) enums.add(entry.data);
                                yield new EnumEntityProperty(name, enums.toArray(String[]::new), enums.getFirst());
                            }
                            default -> throw new IllegalArgumentException("Unknown EntityProperty type " + type);
                        };
                        register(tag.getString("type"), data);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public static void buildPacketData() {
        nbtCache.clear();
        for (Map.Entry<String, List<EntityProperty>> entry : entityPropertyMap.entrySet()) {
            ListTag<CompoundTag> listProperty = buildPropertyList(entry.getValue());
            CompoundTag tag = new CompoundTag().putList(PROPERTIES_KEY, listProperty).putString("type", entry.getKey());
            nbtCache.add(tag);
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

    public static List<SyncEntityPropertyPacket> getPacketCache() {
        return nbtCache.stream().map(SyncEntityPropertyPacket::new).toList();
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

    private static ListTag<CompoundTag> buildPropertyList(List<EntityProperty> properties) {
        ListTag<CompoundTag> listProperty = new ListTag<>();
        for (EntityProperty entityProperty : properties) {
            CompoundTag propertyTag = new CompoundTag();
            propertyTag.putString("name", entityProperty.getIdentifier());
            entityProperty.populateTag(propertyTag);
            listProperty.add(propertyTag);
        }
        return listProperty;
    }
}
