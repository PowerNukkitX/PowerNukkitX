package cn.powernukkitx.bootstrap.gui.exception.model;

import cn.powernukkitx.bootstrap.gui.model.DataKey;

public final class InvalidDataClassException extends ModelException {
    private final DataKey<?> enumDataKey;

    public InvalidDataClassException(DataKey<?> enumDataKey) {
        super(enumDataKey.getId().name() + " (" + enumDataKey.getClazz().getName() + ")");
        this.enumDataKey = enumDataKey;
    }

    public DataKey<?> getEnumDataKey() {
        return enumDataKey;
    }
}
