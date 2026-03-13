package cn.nukkit.network.protocol.types.ddui;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DataStoreUpdate extends DataStoreChangeInfo {

    private String dataStoreName;

    private String property;

    private String path;

    private DataStorePropertyType type;

    private Object data;

    private int propertyUpdateCount;
    private int pathUpdateCount;

    @Override
    public Type getChangeType() {
        return Type.UPDATE;
    }
}