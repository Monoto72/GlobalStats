package me.monoto.statistics.utils;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandUtils {
    /**
     * @param src  the brigadier command source
     * @param perm the specific permission your command wants, e.g. "plugin.reload"
     */
    public static boolean isPermissible(CommandSourceStack src, String perm) {
        CommandSender sender = src.getSender();

        if (sender instanceof ConsoleCommandSender || sender.isOp()) {
            return true;
        }

        if (sender.hasPermission("prisonupgrades.admin")) {
            return true;
        }

        return sender.hasPermission(perm);
    }
}

