package cn.nukkit.utils;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class PersonaPieceTint {
    public final String pieceType;
    public final ImmutableList<String> colors;

    public PersonaPieceTint(String pieceType, List<String> colors) {
        this.pieceType = pieceType;
        this.colors = ImmutableList.copyOf(colors);
    }
}
