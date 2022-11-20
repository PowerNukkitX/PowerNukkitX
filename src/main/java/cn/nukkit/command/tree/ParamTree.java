package cn.nukkit.command.tree;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.node.*;

import java.util.HashMap;
import java.util.Map;

public class ParamTree {
    private final Command command;
    private final Map<String, ParamList> root;

    public ParamTree(Command command) {
        this.command = command;
        var root = new HashMap<String, ParamList>();
        for (Map.Entry<String, CommandParameter[]> entry : command.getCommandParameters().entrySet()) {
            var paramList = new ParamList();
            for (CommandParameter parameter : entry.getValue()) {
                if (parameter.enumData == null) {
                    switch (parameter.type) {
                        case INT -> {
                            paramList.add(new IntNode(parameter.optional));
                        }
                        case WILDCARD_INT -> {
                            if (parameter.name.contains("max")) {
                                paramList.add(new WildcardIntNode(Integer.MAX_VALUE, parameter.optional));
                            } else paramList.add(new WildcardIntNode(parameter.optional));
                        }
                        case FLOAT, VALUE -> {
                            paramList.add(new FloatNode(parameter.optional));
                        }
                        case POSITION -> {//(?<=\s|^)([~^]?-?\d+\.?\d*(?=\s|$))
                            paramList.add(new FloatPositionNode(parameter.optional));
                        }
                        case BLOCK_POSITION -> {
                            paramList.add(new IntPositionNode(parameter.optional));
                        }
                        case TARGET -> {
                            //todo 4
                        }
                        case WILDCARD_TARGET -> {
                            //todo 5
                        }
                        case STRING, FILE_PATH, OPERATOR -> {
                            paramList.add(new StringNode(parameter.optional));
                        }
                        case MESSAGE, TEXT, COMMAND, RAWTEXT, JSON -> {
                            //todo 6
                        }
                    }
                } else {
                    paramList.add(new EnumNode(parameter.optional, parameter.enumData));
                }
            }
            root.put(entry.getKey(), paramList);
        }
        this.root = root;
    }

    public ParamList matchAndParse(CommandSender sender, String[] args) {//成功条件 命令链与参数长度相等 命令链必选参数全部有结果
        this.root.forEach((key, value) -> value.reset());

        for (Map.Entry<String, ParamList> entry : this.root.entrySet().stream().filter(e -> e.getValue().error == -1).toList()) {
            for (var node : entry.getValue()) {
                if (entry.getValue().index == args.length) break;//已经用完输入参数
                switch (node.type()) {//代表所需要的参数数量
                    case STRING, INT, FLOAT, WILDCARD_INT, ENUM -> {//1
                        try {
                            node.fill(args[entry.getValue().index++]);
                        } catch (CommandSyntaxException e) {
                            entry.getValue().error = entry.getValue().index;//logger
                        }
                    }
                    case BLOCK_POSITION, POSITION -> {//3
                        byte i = 0;
                        while (!node.hasResult()) {
                            try {
                                node.fill(args[entry.getValue().index], sender, i);
                                entry.getValue().index++;
                                i++;
                            } catch (CommandSyntaxException | ArrayIndexOutOfBoundsException e) {
                                entry.getValue().error = entry.getValue().index;//logger
                                break;
                            }
                        }
                    }
                }
            }
        }
        var filterList = this.root.entrySet().stream().filter(e -> e.getValue().error == -1).toList();
        if (filterList.isEmpty()) {//全部不成功
            System.out.println("全部不成功:" + this.root.entrySet().stream().map(e -> e.getValue().error).max(Integer::compareTo).get());//logger
        } else {
            var filterList2 = filterList.stream().filter(e -> {
                if (e.getValue().index == args.length) {
                    return e.getValue().stream().allMatch(node -> !node.isOptional() && node.hasResult());
                }
                return false;
            }).toList();
            if (filterList2.isEmpty()) {//参数不足或参数过多
                System.out.println("参数不足或参数过多:" + filterList.stream().map(e -> e.getValue().index).max(Integer::compareTo).get());//logger
            } else {//成功结果
                return filterList2.get(0).getValue();
            }
        }
        return null;
    }
}
