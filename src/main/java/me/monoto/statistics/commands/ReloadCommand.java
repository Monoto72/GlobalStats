package me.monoto.statistics.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.utils.Formatters;
import me.monoto.statistics.utils.LanguageManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends MainCommand {

    Statistics plugin;

    private static final String PERMISSION = "globalstats.reload";

    public ReloadCommand(@NotNull Statistics main) {
        plugin = main;
    }

    @SubCommand("reload")
    @Permission(PERMISSION)
    public void executor(CommandSender sender) {
        Statistics.getInstance().setLanguage(new LanguageManager(this.plugin).getFileConfig());
        this.plugin.reloadConfig();
        sender.sendMessage(Formatters.mini(Formatters.lang().getString("reload.success", "Configurations reloaded.")));
    }
}
