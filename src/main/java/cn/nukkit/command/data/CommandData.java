package cn.nukkit.command.data;

import cn.nukkit.network.protocol.types.PermissionLevel;

import java.util.*;

public class CommandData implements Cloneable {

    public String description = "description";
    public CommandEnum aliases = null;
    public Map<String, CommandOverload> overloads = new HashMap<>();
    public EnumSet<Flag> flags = EnumSet.of(Flag.NOT_CHEAT);
    public PermissionLevel permission = PermissionLevel.ANY;


    public List<ChainedSubCommandData> subcommands = new ArrayList<>();

    @Override
    public CommandData clone() {
        try {
            return (CommandData) super.clone();
        } catch (Exception e) {
            return new CommandData();
        }
    }

    public enum Flag {
        NONE(0x00),
        TEST_USAGE(0x01),
        HIDDEN_FROM_COMMAND_BLOCK(0x02),
        HIDDEN_FROM_PLAYER(0x04),
        HIDDEN(0x06),
        HIDDEN_FROM_AUTOMATION(0x08),
        REMOVED(0xe),
        LOCAL_SYNC(0x10),
        EXECUTE_DISALLOWED(0x20),
        MESSAGE_TYPE(0x40),
        NOT_CHEAT(0x80),
        ASYNC(0x100),
        EDITOR(0x200);

        public final int bit;

        Flag(int bit) {
            this.bit = bit;
        }
    }
}
