package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.EntitySelectorAPI;
import cn.nukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * 解析全部剩余参数拼接为{@code String}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#MESSAGE MESSAGE}
 * 如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class MessageStringNode extends ParamNode<String> {

    private final List<String> TMP = new ArrayList<>();

    @Override
    public void fill(String arg) {
        if (this.parent.getIndex() != this.parent.parent.getArgs().length) TMP.add(arg);
        else {
            TMP.add(arg);

            var str = String.join(" ", TMP);
            var match = EntitySelectorAPI.getENTITY_SELECTOR().matcher(str);
            try {
                this.value = match.replaceAll(r -> {
                    var start = Math.max(0, match.start() - 1);
                    var end = Math.min(str.length(), match.end());
                    if (start != 0) {
                        char before = str.charAt(start);
                        if (before == '”' || before == '\'' || before == '\\' || before == ';') return match.group();
                    }
                    if (end != str.length()) {
                        char after = str.charAt(end);
                        if (after == '”' || after == '\'' || after == '\\' || after == ';') return match.group();
                    }
                    var m = match.group();
                    if (EntitySelectorAPI.getAPI().checkValid(m)) {
                        StringJoiner join = new StringJoiner(", ");
                        for (Entity entity : EntitySelectorAPI.getAPI().matchEntities(this.parent.parent.getSender(), m)) {
                            join.add(entity.getName());
                        }
                        return join.toString();
                    } else return m;
                });
            } catch (SelectorSyntaxException e) {
                error(e.getMessage());
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        TMP.clear();
    }
}
