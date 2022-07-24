package me.monoto.statistics.menus.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.monoto.statistics.utils.Formatters;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Pagination {
    public static void getPaginatedUtil(PaginatedGui gui, OfflinePlayer player) {
        for (int index = 27; index < 36; index++) {
            gui.setItem(index, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());
        }

        /*
        Component guiTitle;

        if (Objects.equals(type, "player")) {
            guiTitle = Formatters.mini(Formatters.lang().getString("gui.players.title", "Online users | Page: <page>"), "page", Component.text(gui.getCurrentPageNum()));
        } else {
            guiTitle = Formatters.mini(Formatters.lang().getString(gui.))
        }
         */

        gui.setItem(29, ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
                .name(Formatters.mini(Formatters.lang().getString("gui.util.page.backward", "Go Back")).decoration(TextDecoration.ITALIC, false)).asGuiItem(event -> {
                    gui.previous();
                    gui.updateTitle(Formatters.getPossessionString(Objects.requireNonNull(player.getName())) + " stats | Page: " + gui.getCurrentPageNum());
                })
        );
        gui.setItem(33, ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19")
                .name(Formatters.mini(Formatters.lang().getString("gui.util.page.forward", "Go Forward")).decoration(TextDecoration.ITALIC, false)).asGuiItem(event -> {
                    gui.next();
                    gui.updateTitle(Formatters.getPossessionString(Objects.requireNonNull(player.getName())) + " stats | Page: " + gui.getCurrentPageNum());
                })
        );

    }
}
