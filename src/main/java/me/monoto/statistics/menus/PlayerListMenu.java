package me.monoto.statistics.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.monoto.statistics.menus.utils.Pagination;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import me.monoto.statistics.utils.Formatters;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerListMenu {
    public static void initialise(Player player) {
        PaginatedGui gui = Gui.paginated().title(Component.text("Statistics | Page: 1")).rows(4).pageSize(27).create();
        populateMenu(gui);

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.open(player);
    }

    public static void populateMenu(PaginatedGui gui) {

        Pagination.getPaginatedUtil(gui, null);
        gui.setItem(31, ItemBuilder.from(Material.CHEST).name(Component.text("Global Statistics").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .asGuiItem(event -> GlobalMenu.initialise((Player) event.getWhoClicked())));

        if (!StatisticsManager.getPlayerStatistics().isEmpty()) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());

                GuiItem item = ItemBuilder.skull().owner(player).name(Formatters.getPlayerSkullTitle(player)).asGuiItem(event -> {
                    if(Bukkit.getPlayer(stats.getPlayerUUID()) == null)  {
                        initialise((Player) event.getWhoClicked());
                    } else {
                        PlayerMenu.initialise((Player) event.getWhoClicked(), player);
                    }
                });

                gui.addItem(item);
            });
        }
    }
}
