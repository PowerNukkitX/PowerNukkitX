package cn.nukkit.entity.data.property;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.SyncEntityPropertyPacket;

import java.util.*;

/**
 * @author Peng_Lx
 */
public abstract class EntityProperty {

    private final String identifier;

    private static List<SyncEntityPropertyPacket> packetCache;

    public EntityProperty(String identifier) {
        this.identifier = identifier;
    }

    private static final Map<String, List<EntityProperty>> entityPropertyMap = new HashMap<>();

    public static boolean register(String entityIdentifier, EntityProperty property) {
        List<EntityProperty> entityProperties = entityPropertyMap.getOrDefault(entityIdentifier, new ArrayList<>());

        for(EntityProperty entityProperty : entityProperties) {
            if(Objects.equals(entityProperty.identifier, property.identifier)) return false;
        }

        entityProperties.add(property);
        entityPropertyMap.put(entityIdentifier, entityProperties);
        return true;
    }

    public static void buildPacket() {
        List<SyncEntityPropertyPacket> packetList = new ArrayList<>();

        for (Map.Entry<String, List<EntityProperty>> entry : entityPropertyMap.entrySet()) {
            String entity = entry.getKey();
            List<EntityProperty> properties = entry.getValue();

            ListTag<CompoundTag> listProperty = new ListTag<>();

            for (EntityProperty entityProperty : properties) {
                CompoundTag propertyTag = new CompoundTag();
                propertyTag.putString("name", entityProperty.identifier);
                entityProperty.populateTag(propertyTag);
                listProperty.add(propertyTag);
            }

            CompoundTag nbt = new CompoundTag()
                    .putList("properties", listProperty)
                    .putString("name", entity);

            SyncEntityPropertyPacket packet = new SyncEntityPropertyPacket();
            packet.setData(nbt);
            packetList.add(packet);
        }

        packetCache = packetList;
    }

    public static List<SyncEntityPropertyPacket> getPacketCache() {
        return packetCache;
    }
    public static List<EntityProperty> getEntityProperty(String identifier) {
        return entityPropertyMap.getOrDefault(identifier, new ArrayList<>());
    }

    public abstract void populateTag(CompoundTag tag);

    public String getIdentifier() {
        return identifier;
    }
}