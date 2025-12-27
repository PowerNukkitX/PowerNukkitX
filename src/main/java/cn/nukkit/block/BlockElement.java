package cn.nukkit.block;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class BlockElement extends Block {

    private final int elementId;
    private final BlockProperties properties;

    public BlockElement(int elementId) {
        this(elementId, new BlockProperties("minecraft:element_" + elementId));
    }

    private BlockElement(int elementId, BlockProperties properties) {
        super(properties.getDefaultState());
        this.elementId = elementId;
        this.properties = properties;
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return properties;
    }
}
