package me.monoto.statistics.menus.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.monoto.statistics.utils.Formatters;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Pagination {
    public static void getPaginatedUtil(PaginatedGui gui, OfflinePlayer player) {
        for (int index = 27; index < 36; index++) {
            gui.setItem(index, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());
        }

        gui.setItem(29, ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
                .name(Component.text("Previous Page")).asGuiItem(event -> {
                    gui.previous();
                    gui.updateTitle(Formatters.getPossessionString(Objects.requireNonNull(player.getName())) + " Statistics | Page: " + gui.getCurrentPageNum());
                })
        );
        gui.setItem(33, ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19")
                .name(Component.text("Next Page")).asGuiItem(event -> {
                    gui.next();
                    gui.updateTitle(Formatters.getPossessionString(Objects.requireNonNull(player.getName())) + " Statistics | Page: " + gui.getCurrentPageNum());
                })
        );

    }
}
