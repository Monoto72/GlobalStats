package me.monoto.statistics.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import me.monoto.statistics.stats.StatisticsManager;
import me.monoto.statistics.utils.Formatters;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GlobalMenu {
    public static void initialise (Player player) {
        Gui gui = Gui.gui().rows(3).title(Component.text("Global Statistics")).create();

        populateMenu(gui);

        gui.setDefaultClickAction(event -> event.setCancelled(true));
        gui.open(player);
    }

    private static void populateMenu (Gui gui) {

        gui.setItem(10, ItemBuilder.from(Material.CHEST).name(Component.text("All Players")).lore(Component.text("Click me to view all Players")).asGuiItem(event -> PlayerListMenu.initialise((Player) event.getWhoClicked())));

        gui.setItem(12, ItemBuilder.from(Material.FISHING_ROD).name(Component.text("Fishing").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)).lore(getLore("fished")).asGuiItem());
        gui.setItem(13, ItemBuilder.from(Material.DIAMOND_PICKAXE).name(Component.text("Mining").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)).lore(getLore("mined")).asGuiItem());
        gui.setItem(14, ItemBuilder.from(Material.DIAMOND_SWORD).name(Component.text("Killing").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)).lore(getLore("killed")).asGuiItem());
        gui.setItem(15, ItemBuilder.from(Material.LEATHER_BOOTS).name(Component.text("Traversed Blocks").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)).lore(getLore("traversed")).asGuiItem());
        gui.setItem(16, ItemBuilder.from(Material.STONE).name(Component.text("Placing").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)).lore(getLore("placed")).asGuiItem());

        gui.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());
    }

    private static List<Component> getLore(String type) {
        List<Component> lore = new ArrayList<>();
        String globalStatistic = switch (type) {
            case "fished" -> String.valueOf(StatisticsManager.getGlobalStatistics().getFishedFish());
            case "mined" -> String.valueOf(StatisticsManager.getGlobalStatistics().getMinedBlocks());
            case "killed" -> String.valueOf(StatisticsManager.getGlobalStatistics().getMobsKilled());
            case "traversed" -> Formatters.getIntFormatter(StatisticsManager.getGlobalStatistics().getTraversedBlocks());
            case "placed" -> String.valueOf(StatisticsManager.getGlobalStatistics().getPlacedBlocks());
            default -> "0";
        };

        TextComponent amount = Component.text().content("Total " + type + ": ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .append(Component.text().content(globalStatistic).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                .build();

        lore.add(amount);
        lore.add(Component.text(" "));

        return lore;
    }

}
