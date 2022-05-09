package me.byteful.plugin.areas;

import com.nthbyte.dialogue.Dialogue;
import com.nthbyte.dialogue.DialogueAPI;
import com.nthbyte.dialogue.Prompt;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.misc.FormatUtils;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AreasCommand {
  private final AreasPlugin plugin;

  public AreasCommand(AreasPlugin plugin) {
    this.plugin = plugin;
  }

  @CommandHook("create")
  public void onCreate(Player player, String name) {
    if(plugin.getAreas().containsKey(name)) {
      player.sendMessage(plugin.getMessages().get("area_already_exists"));

      return;
    }

    player.sendMessage(plugin.getMessages().get("wizard_welcome"));
    player.sendMessage(plugin.getMessages().get("wizard_exit"));

    AtomicReference<String> title = new AtomicReference<>("");
    AtomicReference<String> subtitle = new AtomicReference<>("");
    AtomicReference<String> actionbar = new AtomicReference<>("");
    List<Area.RegionPoint2D> points = new ArrayList<>();

    final Dialogue dialogue = new Dialogue.Builder()
      .addPrompt(new Prompt.Builder()
        .setText(plugin.getMessages().get("wizard_title"))
        .setOnReceiveInputAction((p, s) -> title.set(s))
      )
      .addPrompt(new Prompt.Builder()
        .setText(plugin.getMessages().get("wizard_subtitle"))
        .setOnReceiveInputAction((p, s) -> subtitle.set(s))
      )
      .addPrompt(new Prompt.Builder()
        .setText(plugin.getMessages().get("wizard_actionbar"))
        .setOnReceiveInputAction((p, s) -> actionbar.set(s))
      )
      .addPrompt(new Prompt.Builder()
        .setText(plugin.getMessages().get("wizard_set"))
        .setOnReceiveInputAction((p, s) -> {
          if(points.size() <= 3) {
            p.sendMessage(plugin.getMessages().get("not_enough_points"));

            return;
          }

          final Area area = new Area(name, title.get(), subtitle.get(), actionbar.get(), player.getWorld(), points, player.getWorld().getMinHeight(), player.getWorld().getMaxHeight());
          area.save(plugin);

          player.sendMessage(plugin.getMessages().get("success_create_area"));
        })
        .setOnValidateInputAction((s) -> {
          if(s.equalsIgnoreCase("set")) {
            points.add(new Area.RegionPoint2D(player.getLocation()));

            return false;
          }

          return s.equalsIgnoreCase("done") && points.size() >= 3;
        })
      )
      .setEscapeSequence("exit")
      .setEndAction((p, c) -> p.sendMessage(plugin.getMessages().get("exited_creation_wizard")))
      .setRepeatPrompt(true)
      .build();

    DialogueAPI.startDialogue(player, dialogue);
  }

  @CommandHook("delete")
  public void onDelete(CommandSender sender, Area area) {
    plugin.getAreas().remove(area.getName());
    sender.sendMessage(plugin.getMessages().get("success_delete_area"));
  }

  @CommandHook("list")
  public void onList(CommandSender sender) {
    sender.sendMessage(plugin.getMessages().get("areas_list_header"));
    plugin.getAreas().values().forEach(a -> sender.sendMessage(plugin.getMessages().getAndReplace("areas_list_format", a.getName())));
  }
}
