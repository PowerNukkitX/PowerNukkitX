package cn.nukkit.command.function;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class Function {

    private final Path fullPath;
    private List<String> commands;

    private Function(Path fullPath){
        this.fullPath = fullPath;
        try {
            commands = Files.readAllLines(fullPath);
            commands = commands.stream().filter(s -> !s.isEmpty()).map(s -> s.split("#")[0]).toList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Function fromPath(Path path){
        return new Function(path);
    }

    public boolean dispatch(CommandSender sender){
        boolean success = true;
        for (String command : commands) {
            if (!Server.getInstance().dispatchCommand(sender, command))
                success = false;
        }
        return success;
    }
}
