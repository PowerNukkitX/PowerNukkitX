package cn.nukkit.level.tickingarea.storage;

import cn.nukkit.level.tickingarea.TickingArea;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JSONTickingAreaStorage implements TickingAreaStorage{

    private static Type type = new TypeToken<HashSet<TickingArea>>(){}.getType();

    protected static Gson gson = new Gson();

    protected Path filePath;
    protected Map<String,TickingArea> areaMap = new HashMap<>();

    public JSONTickingAreaStorage(String path){
        this.filePath = Paths.get(path);
        try {
            if (!Files.exists(this.filePath)) {
                Files.createFile(this.filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTickingArea(TickingArea area) {
        areaMap.put(area.getName(),area);
        save();
    }

    @Override
    public void addTickingArea(TickingArea[] areas) {
        for (TickingArea area : areas){
            addTickingArea(area);
        }
    }

    @Override
    public Map<String, TickingArea> readTickingArea() {
        try {
            if (Files.readString(filePath).isEmpty()){
                save();
                return new HashMap<>();
            }else {
                Set<TickingArea> areas = gson.fromJson(Files.readString(filePath),type);
                Map<String, TickingArea> aMap = new HashMap<>();
                for (TickingArea area : areas) aMap.put(area.getName(), area);
                return aMap;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeTickingArea(String name) {
        areaMap.remove(name);
        save();
    }

    @Override
    public void removeAllTickingArea() {
        areaMap.clear();
        save();
    }

    @Override
    public boolean containTickingArea(String name) {
        return areaMap.containsKey(name);
    }

    private void save(){
        String json = gson.toJson(areaMap.values());
        try {
            Files.writeString(filePath,json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
