package me.monoto.statistics.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import me.monoto.statistics.menus.submenus.PlayerStatisticsMenuItems;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import me.monoto.statistics.utils.Formatters;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerMenu {
    public static void initialise(Player player, OfflinePlayer target) {
        Gui gui = Gui.gui().rows(3).title(Component.text(Formatters.getPossessionString(Objects.requireNonNull(target.getName())) + " Statistics")).create();

        populateMenu(gui, player, target);

        gui.setDefaultClickAction(event -> event.setCancelled(true));
        gui.open(player);
    }

    private static void populateMenu(Gui gui, Player player, OfflinePlayer target) {

        gui.setItem(10, ItemBuilder.skull().owner(target).name(Formatters.getPlayerSkullTitle(target)).asGuiItem());

        gui.setItem(12, ItemBuilder.from(Material.FISHING_ROD).name(Component.text("Fished Fish").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)).lore(getLore("fished", target)).asGuiItem());
        gui.setItem(13, ItemBuilder.from(Material.DIAMOND_PICKAXE).name(Component.text("Mined Blocks").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)).lore(getLore("mined", target)).asGuiItem(event -> PlayerStatisticsMenuItems.getItemPreview("mined", (Player) event.getWhoClicked(), target)));
        gui.setItem(14, ItemBuilder.from(Material.DIAMOND_SWORD).name(Component.text("Mobs Killed").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)).lore(getLore("killed", target)).asGuiItem(event -> PlayerStatisticsMenuItems.getItemPreview("killed", (Player) event.getWhoClicked(), target)));
        gui.setItem(15, ItemBuilder.from(Material.LEATHER_BOOTS).name(Component.text("Movement").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)).lore(getLore("traversed", target)).asGuiItem(event -> PlayerStatisticsMenuItems.getItemPreview("moved", (Player) event.getWhoClicked(), target)));
        gui.setItem(16, ItemBuilder.from(Material.STONE).name(Component.text("Placed Blocks").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)).lore(getLore("placed", target)).asGuiItem(event -> PlayerStatisticsMenuItems.getItemPreview("placed", (Player) event.getWhoClicked(), target)));

        gui.setItem(26, ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
                .name(Component.text("Go Back")).asGuiItem(event -> PlayerListMenu.initialise(player)));

        gui.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());
    }

    private static List<Component> getLore(String type, OfflinePlayer target) {
        List<Component> lore = new ArrayList<>();
        String playerStatistic = "0";

        PlayerStatistics stats = target.getPlayer() != null ? (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(target.getUniqueId())
                : (PlayerStatistics) StatisticsManager.getOfflinePlayerStatistics().get(target.getUniqueId());

        switch (type) {
            case "fished" -> playerStatistic = String.valueOf(stats.getFishedFish());
            case "mined" -> playerStatistic = String.valueOf(stats.getMinedBlocks());
            case "killed" -> playerStatistic = String.valueOf(stats.getMobsKilled());
            case "traversed" -> playerStatistic = Formatters.getIntFormatter(stats.getTraversedBlocks());
            case "placed" -> playerStatistic = String.valueOf(stats.getPlacedBlocks());
        }

        TextComponent amount = Component.text().content("Total " + type + ": ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .append(Component.text().content(playerStatistic).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                .build();

        lore.add(amount);

        return lore;
    }


}
