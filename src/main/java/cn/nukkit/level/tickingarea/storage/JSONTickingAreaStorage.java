package cn.nukkit.level.tickingarea.storage;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.tickingarea.TickingArea;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JSONTickingAreaStorage implements TickingAreaStorage {

    private static final Type type = new TypeToken<HashSet<TickingArea>>() {
    }.getType();

    protected static Gson gson = new Gson();

    /**
     * 存储常加载区域的根目录
     */
    protected Path filePath;
    //               row     column
    //            LevelName AreaName
    protected Table<String, String, TickingArea> areaMap = HashBasedTable.create();

    public JSONTickingAreaStorage(String path) {
        this.filePath = Paths.get(path);
        try {
            if (!Files.exists(this.filePath)) {
                Files.createDirectories(this.filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTickingArea(TickingArea area) {
        areaMap.put(area.getLevelName(), area.getName(), area);
        save();
    }

    @Override
    public void addTickingArea(@NotNull TickingArea... areas) {
        for (var each : areas) {
            areaMap.put(each.getLevelName(), each.getName(), each);
        }
        save();
    }

    @Override
    public Map<String, TickingArea> readTickingArea() {
        var rootDir = new File(filePath.toString());
        var aMap = new HashMap<String, TickingArea>();
        for (var each : Objects.requireNonNull(rootDir.listFiles())) {
            var jsonFile = new File(each, "tickingarea.json");
            if (jsonFile.exists()) {
                try (var fr = new FileReader(jsonFile)) {
                    Set<TickingArea> areas = gson.fromJson(fr, type);
                    for (var area : areas) {
                        areaMap.put(area.getLevelName(), area.getName(), area);
                        aMap.put(area.getName(), area);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return aMap;
    }

    @Override
    public void removeTickingArea(String name) {
        areaMap.columnMap().remove(name);
        save();
    }

    @Override
    public void removeAllTickingArea() {
        areaMap.clear();
        save();
    }

    @Override
    public boolean containTickingArea(String name) {
        return areaMap.containsColumn(name);
    }

    private void save() {
        try {
            for (Level level : Server.getInstance().getLevels().values()) {
                if (areaMap.containsRow(level.getName())) {
                    Files.writeString(Path.of(filePath.toString(), level.getName(), "tickingarea.json"), gson.toJson(areaMap.rowMap().get(level.getName()).values().toArray()));
                }else{
                    Files.deleteIfExists(Path.of(filePath.toString(), level.getName(), "tickingarea.json"));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
