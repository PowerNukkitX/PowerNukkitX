package cn.nukkit.utils;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class PersonaPiece {
    public final String id;
    public final String type;
    public final String packId;
    public final boolean isDefault;
    public final String productId;

    public PersonaPiece(String id, String type, String packId, boolean isDefault, String productId) {
        this.id = id;
        this.type = type;
        this.packId = packId;
        this.isDefault = isDefault;
        this.productId = productId;
    }
}
