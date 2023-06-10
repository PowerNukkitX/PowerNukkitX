package cn.nukkit.command.data;

import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PowerNukkitXDifference(since = "1.19.50-r1", info = "Use List<Flag> instead")
public class CommandData implements Cloneable {

    public String description = "description";
    public CommandEnum aliases = null;
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
    @Since("1.19.50-r1")
    public enum Flag {
        TEST_USAGE, // 1
        HIDDEN_FROM_COMMAND_BLOCK, // 2
        HIDDEN_FROM_PLAYER, // 4
        HIDDEN_FROM_AUTOMATION, // 8
        LOCAL_SYNC, // 16
        EXECUTE_DISALLOWED, // 32
        MESSAGE_TYPE, // 64
        NOT_CHEAT,// 128
        ASYNC // 256
        //EDITOR // 512
    }
}
