package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.types.CodeBuilderCategoryType;
import cn.nukkit.network.protocol.types.CodeBuilderOperationType;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class CodeBuilderSourcePacket extends DataPacket {
    private CodeBuilderOperationType operation;
    private CodeBuilderCategoryType category;
    private String value;

    @Override
    public byte pid() {
        return ProtocolInfo.CODE_BUILDER_SOURCE_PACKET;
    }

    @Override
    public void decode() {
        this.operation = CodeBuilderOperationType.values()[getByte()];
        this.category = CodeBuilderCategoryType.values()[getByte()];
        this.value = getString();
    }

    @Override
    public void encode() {
        putByte((byte) operation.ordinal());
        putByte((byte) category.ordinal());
        putString(value);
    }

    public CodeBuilderOperationType getOperation() {
        return operation;
    }

    public void setOperation(CodeBuilderOperationType operation) {
        this.operation = operation;
    }

    public CodeBuilderCategoryType getCategory() {
        return category;
    }

    public void setCategory(CodeBuilderCategoryType category) {
        this.category = category;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
