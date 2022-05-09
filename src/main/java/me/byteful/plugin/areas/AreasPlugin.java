package me.byteful.plugin.areas;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nthbyte.dialogue.DialogueAPI;
import me.byteful.lib.ppd.PersistentPluginData;
import me.byteful.lib.ppd.StorageLocation;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.commandmanager.Messages;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public final class AreasPlugin extends JavaPlugin {
  private final Gson gson = new GsonBuilder().setPrettyPrinting()/*.registerTypeAdapter(Area.class, new AreaAdapter())*/.disableHtmlEscaping().serializeNulls().setLenient().create();
  private final HashMap<String, Area> areas = new HashMap<>();
  private PersistentPluginData ppd;
  Messages messages;

  @Override
  public void onEnable() {
    DialogueAPI.hook(this);
    getDataFolder().mkdirs();
    if (!Files.exists(getDataFolder().toPath().resolve("messages.txt"))) {
      saveResource("messages.txt", false);
    }
    messages = Messages.load(this);
    if(!Files.exists(getDataFolder().toPath().resolve("data.json"))) {
      try {
        Files.createFile(getDataFolder().toPath().resolve("data.json"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    ppd = new PersistentPluginData(gson, StorageLocation.INSIDE_PLUGIN_FOLDER, this, "data", true);
    if(!ppd.exists("areas")) {
      ppd.set("areas", new HashMap<String, Area>());
    }
    areas.putAll(ppd.get("areas", new TypeToken<Map<String, Area>>() {}).orElse(new HashMap<>()));
    getServer().getScheduler().scheduleAsyncRepeatingTask(this, () -> {
      try {
        ppd.set("areas", areas);
        ppd.save();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }, 20L, 20L);
    new CommandParser(getResource("command.rdcml"), messages)
      .setArgTypes(ArgType.of("area", areas))
      .parse()
      .register(this, "areas", new AreasCommand(this));
    getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
  }

  @Override
  public void onDisable() {
    try {
      ppd.set("areas", areas);
      ppd.save();
      areas.clear();
      ppd = null;
    } catch (IOException e) {
      e.printStackTrace();
    }
    messages = null;
  }

  public Map<String, Area> getAreas() {
    return areas;
  }

  public PersistentPluginData getData() {
    return ppd;
  }

  public Messages getMessages() {
    return messages;
  }

  @Nullable
  public Area getLocationArea(@NotNull Location location) {
    return getAreas().values().stream().filter(area -> area.contains(location)).findFirst().orElse(null);
  }
}