package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;

public interface Component {
    void execute(CLI cli, Object... args);
}
