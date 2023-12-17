package cn.nukkit.utils.functional;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.Objects;


public interface BooleanConsumer {


    void accept(boolean value);


    default BooleanConsumer andThen(BooleanConsumer after) {
        Objects.requireNonNull(after);
        return (boolean t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
