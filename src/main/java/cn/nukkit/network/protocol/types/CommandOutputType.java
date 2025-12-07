package cn.nukkit.network.protocol.types;

import lombok.Getter;

public enum CommandOutputType {
    NONE("none"),
    LAST_OUTPUT("lastoutput"),
    SILENT("silent"),
    ALL_OUTPUT("alloutput"),
    DATA_SET("dataset");

    @Getter
    String networkname;

    CommandOutputType(String networkname) {
        this.networkname = networkname;
    }
}
