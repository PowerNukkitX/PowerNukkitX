package cn.nukkit.network.protocol;

import cn.nukkit.command.data.ChainedSubCommandData;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandOverload;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.CommandEnumConstraintData;
import cn.nukkit.utils.SequencedHashSet;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ObjIntConsumer;

import static cn.nukkit.utils.Utils.dynamic;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author MagicDroidX (Nukkit Project)
 */

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AvailableCommandsPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.AVAILABLE_COMMANDS_PACKET;

    private static final ObjIntConsumer<HandleByteBuf> WRITE_BYTE = (s, v) -> s.writeByte((byte) v);
    private static final ObjIntConsumer<HandleByteBuf> WRITE_SHORT = HandleByteBuf::writeShortLE;
    private static final ObjIntConsumer<HandleByteBuf> WRITE_INT = HandleByteBuf::writeIntLE;

    public static final int ARG_FLAG_VALID = 0x100000;
    public static final int ARG_FLAG_ENUM = 0x200000;
    public static final int ARG_FLAG_POSTFIX = 0x1000000;
    public static final int ARG_FLAG_SOFT_ENUM = 0x4000000;

    public static final int ARG_TYPE_INT = dynamic(1);
    public static final int ARG_TYPE_FLOAT = dynamic(3);
    public static final int ARG_TYPE_VALUE = dynamic(4);
    public static final int ARG_TYPE_WILDCARD_INT = dynamic(5);
    public static final int ARG_TYPE_OPERATOR = dynamic(6);
    public static final int ARG_TYPE_COMPARE_OPERATOR = dynamic(7);
    public static final int ARG_TYPE_TARGET = dynamic(8);
    public static final int ARG_TYPE_WILDCARD_TARGET = dynamic(10);

    public static final int ARG_TYPE_FILE_PATH = dynamic(17);

    public static final int ARG_TYPE_FULL_INTEGER_RANGE = dynamic(23);

    public static final int ARG_TYPE_EQUIPMENT_SLOT = dynamic(47);
    public static final int ARG_TYPE_STRING = dynamic(56);
    public static final int ARG_TYPE_BLOCK_POSITION = dynamic(64);
    public static final int ARG_TYPE_POSITION = dynamic(65);

    public static final int ARG_TYPE_MESSAGE = dynamic(68);
    public static final int ARG_TYPE_RAWTEXT = dynamic(70);
    public static final int ARG_TYPE_JSON = dynamic(74);
    public static final int ARG_TYPE_BLOCK_STATES = dynamic(84);
    public static final int ARG_TYPE_COMMAND = dynamic(87);

    public Map<String, CommandDataVersions> commands;
    public final List<CommandEnumConstraintData> constraints = new ObjectArrayList<>();

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        //non
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        Set<String> enumValuesSet = new ObjectOpenHashSet<>();
        SequencedHashSet<String> subCommandValues = new SequencedHashSet<>();
        Set<String> postfixSet = new ObjectOpenHashSet<>();
        SequencedHashSet<ChainedSubCommandData> subCommandData = new SequencedHashSet<>();
        Set<CommandEnum> enumsSet = new ObjectOpenHashSet<>();
        Set<CommandEnum> softEnumsSet = new ObjectOpenHashSet<>();

        // Get all enum values
        for (var entry : commands.entrySet()) {
            var data = entry.getValue().versions.get(0);
            if (data.aliases != null) {
                enumValuesSet.addAll(data.aliases.getValues());
                enumsSet.add(data.aliases);
            }

            for (ChainedSubCommandData subcommand : data.subcommands) {
                if (subCommandData.contains(subcommand)) {
                    continue;
                }

                subCommandData.add(subcommand);
                for (ChainedSubCommandData.Value value : subcommand.getValues()) {
                    if (subCommandValues.contains(value.getFirst())) {
                        subCommandValues.add(value.getFirst());
                    }

                    if (subCommandValues.contains(value.getSecond())) {
                        subCommandValues.add(value.getSecond());
                    }
                }
            }

            for (CommandParameter[] overload : data.overloads.values().stream().map(o -> o.input.parameters).toList()) {
                for (CommandParameter parameter : overload) {
                    CommandEnum commandEnumData = parameter.enumData;
                    if (commandEnumData != null) {
                        if (commandEnumData.isSoft()) {
                            softEnumsSet.add(commandEnumData);
                        } else {
                            enumValuesSet.addAll(commandEnumData.getValues());
                            enumsSet.add(commandEnumData);
                        }
                    }

                    String postfix = parameter.postFix;
                    if (postfix != null) {
                        postfixSet.add(postfix);
                    }
                }
            }
        }

        // Add Constraint Enums
        // Not need it for now
        /*for(CommandEnumData enumData : packet.getConstraints().stream().map(CommandEnumConstraintData::getEnumData).collect(Collectors.toList())) {
            if (enumData.isSoft()) {
                softEnumsSet.add(enumData);
            } else {
                enumsSet.add(enumData);
            }
            enumValuesSet.addAll(Arrays.asList(enumData.getValues()));
        }*/

        List<String> enumValues = new ObjectArrayList<>(enumValuesSet);
        List<String> postFixes = new ObjectArrayList<>(postfixSet);
        List<CommandEnum> enums = new ObjectArrayList<>(enumsSet);
        List<CommandEnum> softEnums = new ObjectArrayList<>(softEnumsSet);

        byteBuf.writeUnsignedVarInt(enumValues.size());
        for (var enumValue : enumValues) {
            byteBuf.writeString(enumValue);
        }

        byteBuf.writeUnsignedVarInt(subCommandValues.size());
        for (var subCommandValue : subCommandValues) {
            byteBuf.writeString(subCommandValue);
        }

        byteBuf.writeUnsignedVarInt(postFixes.size());
        for (var postFix : postFixes) {
            byteBuf.writeString(postFix);
        }

        this.writeEnums(byteBuf,enumValues, enums);

        byteBuf.writeUnsignedVarInt(subCommandData.size());
        for (ChainedSubCommandData chainedSubCommandData : subCommandData) {
            byteBuf.writeString(chainedSubCommandData.getName());
            byteBuf.writeUnsignedVarInt(chainedSubCommandData.getValues().size());
            for (ChainedSubCommandData.Value value : chainedSubCommandData.getValues()) {
                int first = subCommandValues.indexOf(value.getFirst());
                checkArgument(first > -1, "Invalid enum value detected: " + value.getFirst());

                int second = subCommandValues.indexOf(value.getSecond());
                checkArgument(second > -1, "Invalid enum value detected: " + value.getSecond());

                byteBuf.writeShortLE(first);
                byteBuf.writeShortLE(second);
            }
        }

        byteBuf.writeUnsignedVarInt(commands.size());
        for (var entry : commands.entrySet()) {
            this.writeCommand(byteBuf,entry, enums, softEnums, postFixes, subCommandData);
        }

        byteBuf.writeUnsignedVarInt(softEnums.size());
        for (var softEnum : softEnums) {
            this.writeCommandEnum(byteBuf, softEnum);
        }

        // Constraints
        // Not need it for now
        /*helper.writeArray(buffer, packet.getConstraints(), (buf, constraint) -> {
            helper.writeCommandEnumConstraints(buf, constraint, enums, enumValues);
        });*/

        byteBuf.writeUnsignedVarInt(0);
    }

    private void writeEnums(HandleByteBuf byteBuf, List<String> values, List<CommandEnum> enums) {
        // Determine width of enum index
        ObjIntConsumer<HandleByteBuf> indexWriter;
        int valuesSize = values.size();
        if (valuesSize < 0x100) {//256
            indexWriter = WRITE_BYTE;
        } else if (valuesSize < 0x10000) {//65536
            indexWriter = WRITE_SHORT;
        } else {
            indexWriter = WRITE_INT;
        }
        // Write enums
        byteBuf.writeUnsignedVarInt(enums.size());
        for (CommandEnum commandEnum : enums) {
            byteBuf.writeString(commandEnum.getName());

            byteBuf.writeUnsignedVarInt(commandEnum.getValues().size());
            for (String value : commandEnum.getValues()) {
                int index = values.indexOf(value);
                Preconditions.checkArgument(index > -1, "Invalid enum value detected: " + value);
                indexWriter.accept(byteBuf, index);
            }
        }
    }

    private void writeCommand(HandleByteBuf byteBuf, Map.Entry<String, CommandDataVersions> commandEntry, List<CommandEnum> enums, List<CommandEnum> softEnums, List<String> postFixes, List<ChainedSubCommandData> subCommands) {
        var commandData = commandEntry.getValue().versions.get(0);
        byteBuf.writeString(commandEntry.getKey());
        byteBuf.writeString(commandData.description);
        int flags = 0;
        for (CommandData.Flag flag : commandData.flags) {
            flags |= flag.bit;
        }
        byteBuf.writeShortLE(flags);
        byteBuf.writeByte((byte) commandData.permission);

        byteBuf.writeIntLE(commandData.aliases == null ? -1 : enums.indexOf(commandData.aliases));

        byteBuf.writeUnsignedVarInt(subCommands.size());
        for (ChainedSubCommandData subcommand : subCommands) {
            int index = subCommands.indexOf(subcommand);
            checkArgument(index > -1, "Invalid subcommand index: " + subcommand);
            byteBuf.writeShortLE(index);
        }

        Collection<CommandOverload> overloads = commandData.overloads.values();
        byteBuf.writeUnsignedVarInt(overloads.size());
        for (CommandOverload overload : overloads) {
            byteBuf.writeBoolean(overload.chaining);
            byteBuf.writeUnsignedVarInt(overload.input.parameters.length);
            for (CommandParameter param : overload.input.parameters) {
                this.writeParameter(byteBuf, param, enums, softEnums, postFixes);
            }
        }
    }

    private void writeParameter(HandleByteBuf byteBuf, CommandParameter param, List<CommandEnum> enums, List<CommandEnum> softEnums, List<String> postFixes) {
        byteBuf.writeString(param.name);

        int index;
        if (param.postFix != null) {
            index = postFixes.indexOf(param.postFix) | ARG_FLAG_POSTFIX;
        } else if (param.enumData != null) {
            if (param.enumData.isSoft()) {
                index = softEnums.indexOf(param.enumData) | ARG_FLAG_SOFT_ENUM | ARG_FLAG_VALID;
            } else {
                index = enums.indexOf(param.enumData) | ARG_FLAG_ENUM | ARG_FLAG_VALID;
            }
        } else if (param.type != null) {
            index = param.type.getId() | ARG_FLAG_VALID;
        } else {
            throw new IllegalStateException("No param type specified: " + param);
        }

        byteBuf.writeIntLE(index);
        byteBuf.writeBoolean(param.optional);

        byte options = 0;
        if (param.paramOptions != null) {
            for (CommandParamOption option : param.paramOptions) {
                options |= 1 << option.ordinal();
            }
        }
        byteBuf.writeByte(options);
    }

    private void writeCommandEnum(HandleByteBuf byteBuf, CommandEnum enumData) {
        Preconditions.checkNotNull(enumData, "enumData");

        byteBuf.writeString(enumData.getName());

        List<String> values = enumData.getValues();
        byteBuf.writeUnsignedVarInt(values.size());
        for (String value : values) {
            byteBuf.writeString(value);
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
