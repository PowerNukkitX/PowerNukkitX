package cn.nukkit.command.data;

import cn.nukkit.api.Since;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import lombok.Data;

@Since("1.20.10-r1")
@Data
public class ChainedSubCommandData {
    private final String name;
    private final List<Value> values = new ObjectArrayList<>();

    @Data
    public static class Value {
        private final String first;
        private final String second;
    }
}
