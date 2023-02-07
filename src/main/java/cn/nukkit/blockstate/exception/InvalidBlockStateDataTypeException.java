package cn.nukkit.blockstate.exception;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class InvalidBlockStateDataTypeException extends IllegalArgumentException {
    private static final long serialVersionUID = 6883758182474914542L;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockStateDataTypeException(@NotNull Number blockData) {
        super("The block data " + blockData + " has an unsupported type " + blockData.getClass());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockStateDataTypeException(@NotNull Number blockData, @Nullable Throwable cause) {
        super("The block data " + blockData + " has an unsupported type " + blockData.getClass(), cause);
    }
}
