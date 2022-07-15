package me.monoto.statistics.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class ForceUpdateCommand extends MainCommand {

    static Statistics plugin;

    private static final String PERMISSION = "globalstats.update";

    public ForceUpdateCommand(@NotNull final Statistics main) {
        plugin = main;
    }

    @SubCommand("update")
    @Permission(PERMISSION)
    public void executor(CommandSender sender) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            AtomicInteger count = new AtomicInteger();

            Bukkit.getOnlinePlayers().forEach(player -> {
                DatabaseManager.updatePlayer(player.getUniqueId());
                count.getAndIncrement();
            });

            DatabaseManager.getAllStatistics();

            plugin.getLogger().info("has successfully saved " + count + " players statistics.");
            sender.sendMessage("Successfully updated the global leaderboard");
        });
    }
}
