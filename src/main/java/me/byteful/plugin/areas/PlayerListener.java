package me.byteful.plugin.areas;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import redempt.redlib.misc.FormatUtils;

import java.util.concurrent.CompletableFuture;

public class PlayerListener implements Listener {
  private final AreasPlugin plugin;

  public PlayerListener(AreasPlugin plugin) {
    this.plugin = plugin;
    Bukkit.getScheduler()
        .scheduleAsyncRepeatingTask(
            plugin,
            () ->
                Bukkit.getOnlinePlayers()
                    .forEach(
                        player -> {
                          final Area area = plugin.getLocationArea(player.getLocation());
                          if (area == null) {
                            return;
                          }

                          final String actionbar = FormatUtils.color(area.getActionbar());
                          player
                              .spigot()
                              .sendMessage(
                                  ChatMessageType.ACTION_BAR,
                                  TextComponent.fromLegacyText(actionbar));
                        }),
            0L,
            20L);
  }

  @EventHandler
  public void onMove(PlayerMoveEvent e) {
    final Player player = e.getPlayer();
    if (e.getTo() == null || !hasMovedBlock(e.getFrom(), e.getTo())) {
      return;
    }

    CompletableFuture.runAsync(
        () -> {
          final Area area = plugin.getLocationArea(e.getTo());
          final Area lastArea = plugin.getLocationArea(e.getFrom());

          if (area == null || area.equals(lastArea)) {
            return;
          }

          final String title = FormatUtils.color(area.getTitle());
          final String subtitle = FormatUtils.color(area.getSubtitle());

          player.sendTitle(title, subtitle, 2, 66, 2);
        });
  }

  private boolean hasMovedBlock(Location from, Location to) {
    return !from.getBlock().equals(to.getBlock());
  }
}
