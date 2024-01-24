package cn.nukkit.command.function;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


@Getter
public class Function {

    private final Path fullPath;
    private List<String> commands;

    private Function(Path fullPath) {
        this.fullPath = fullPath;
        try {
            commands = Files.readAllLines(fullPath);
            commands = commands.stream().filter(s -> !s.isBlank()).map(s -> s.split("#")[0]).filter(s -> !s.isEmpty()).toList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Function fromPath(Path path) {
        return new Function(path);
    }

    public boolean dispatch(CommandSender sender) {
        boolean success = true;
        for (String command : commands) {
            if (Server.getInstance().executeCommand(sender, command) <= 0)
                success = false;
        }
        return success;
    }
}
