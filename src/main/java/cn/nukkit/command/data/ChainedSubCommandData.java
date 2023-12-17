package cn.nukkit.command.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;

import java.util.List;


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
