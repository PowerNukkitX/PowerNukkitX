package cn.nukkit.network.protocol.types.ddui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class DataStoreChange extends DataStoreChangeInfo {

    private String dataStoreName;
    private String property;
    private int updateCount;
    private DataStorePropertyValue theNewPropertyValue;

    @Override
    public Type getChangeType() {
        return Type.CHANGE;
    }
}