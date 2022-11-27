package cn.nukkit.command.data;

import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PowerNukkitXDifference(since = "1.19.40-r4", info = "Use List<Flag> instead")
public class CommandData implements Cloneable {

    public CommandEnum aliases = null;
    public String description = "description";
    public Map<String, CommandOverload> overloads = new HashMap<>();
    public List<Flag> flags = new ArrayList<>();
    public int permission;

    @Override
    public CommandData clone() {
        try {
            return (CommandData) super.clone();
        } catch (Exception e) {
            return new CommandData();
        }
    }

    // Bit flags
    @PowerNukkitXOnly
    @Since("1.19.40-r4")
    public enum Flag {
        //标记命令为测试(debug)命令
        USAGE,
        //命令可见性，没啥用
        VISIBILITY,
        //命令执行是否同步主线程，也没啥用
        SYNC,
        //是否可被execute执行
        EXECUTE,
        //命令类型？
        TYPE,
        //是否为作弊模式命令
        CHEAT,
        //idk?
        UNKNOWN_6
    }
}
