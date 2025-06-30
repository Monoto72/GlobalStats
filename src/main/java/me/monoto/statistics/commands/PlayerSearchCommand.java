package me.monoto.statistics.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.menus.GlobalMenu;
import me.monoto.statistics.stats.StatisticsManager;
import me.monoto.statistics.utils.Formatters;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerSearchCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("check")
                .requires(src -> src.getSender().hasPermission("globalstats.check"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            Bukkit.getOnlinePlayers().forEach(p -> builder.suggest(p.getName()));
                            return builder.buildFuture();
                        })
                        .executes(PlayerSearchCommand::execute)
                );
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        CommandSender sender = src.getSender();
        String name = StringArgumentType.getString(ctx, "player");

        Player target = Bukkit.getPlayer(name);
        Statistics plugin = Statistics.getInstance();

        if (target != null) {
            // Player is online
            if (sender instanceof Player) {
                GlobalMenu.initialise((Player) sender, target);
            } else {
                plugin.getLogger().info("Too many statistics for you to view in console.");
            }
            return Command.SINGLE_SUCCESS;
        }

        // Try offline
        OfflinePlayer oPlayer = Bukkit.getOfflinePlayerIfCached(name);
        if (oPlayer != null && oPlayer.getName() != null) {
            if (sender instanceof Player) {
                if (StatisticsManager.getOfflinePlayerStatistics().get(oPlayer.getUniqueId()) == null) {
                    StatisticsManager.setOfflinePlayerStatistics(
                            oPlayer.getUniqueId(),
                            oPlayer.getName(),
                            oPlayer.getStatistic(Statistic.FISH_CAUGHT),
                            StatisticsManager.getTotalBlocks(oPlayer, Statistic.MINE_BLOCK),
                            oPlayer.getStatistic(Statistic.MOB_KILLS),
                            StatisticsManager.getTotalBlocks(oPlayer, Statistic.USE_ITEM),
                            StatisticsManager.getTotalBlocksTraversed(oPlayer),
                            StatisticsManager.getHarvestedCrops(oPlayer.getUniqueId())

                    );
                }
                GlobalMenu.initialise((Player) sender, oPlayer);
            } else {
                plugin.getLogger().info("Too many statistics for you to view in console.");
            }
        } else {
            sender.sendMessage(Formatters.miniWithPrefix(
                    Formatters.lang().getString("error.never_played", "<player> has never played"),
                    "player", Component.text(name)
            ));
        }

        return Command.SINGLE_SUCCESS;
    }
}
