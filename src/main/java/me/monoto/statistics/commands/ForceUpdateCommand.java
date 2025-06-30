package me.monoto.statistics.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.database.DatabaseManager;
import me.monoto.statistics.utils.CommandUtils;
import me.monoto.statistics.utils.Formatters;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;

public class ForceUpdateCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("update")
                .requires(src -> CommandUtils.isPermissible(src, "globalstats.update"))
                .executes(ForceUpdateCommand::execute);
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        Statistics plugin = Statistics.getInstance();

        Bukkit.getScheduler().runTask(plugin, () -> {
            AtomicInteger count = new AtomicInteger();

            Bukkit.getOnlinePlayers().forEach(player -> {
                DatabaseManager.updatePlayer(player.getUniqueId());
                count.incrementAndGet();
            });

            DatabaseManager.getAllStatistics();

            plugin.getLogger().info("Successfully saved " + count + " players' statistics.");
            src.getSender().sendMessage(
                    Formatters.miniWithPrefix("<green>Successfully updated the global leaderboard.")
            );
        });

        return Command.SINGLE_SUCCESS;
    }
}

