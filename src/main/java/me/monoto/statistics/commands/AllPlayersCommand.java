package me.monoto.statistics.commands;

import dev.triumphteam.cmd.core.annotation.SubCommand;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.menus.PlayerListMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AllPlayersCommand extends MainCommand {
    static Statistics plugin;

    public AllPlayersCommand(@NotNull final Statistics main) {
        plugin = main;
    }

    @SubCommand("all")
    public void executor(Player player) {
        PlayerListMenu.initialise(player);
    }
}
