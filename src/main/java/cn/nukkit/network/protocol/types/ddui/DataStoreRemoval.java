package cn.nukkit.network.protocol.types.ddui;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DataStoreRemoval extends DataStoreChangeInfo {

    private String dataStoreName;

    @Override
    public Type getChangeType() {
        return Type.REMOVAL;
    }
}