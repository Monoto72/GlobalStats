package me.monoto.statistics.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.utils.CommandUtils;
import me.monoto.statistics.utils.ComponentUtils;
import me.monoto.statistics.utils.Formatters;
import me.monoto.statistics.utils.LanguageManager;

public class ReloadCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("reload")
                .requires(src -> CommandUtils.isPermissible(src, "globalstats.reload"))
                .executes(ReloadCommand::execute);
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        try {
            Statistics.getInstance().setLanguage(new LanguageManager(Statistics.getInstance()).getFileConfig());
            Statistics.getInstance().reloadConfig();

            src.getSender().sendMessage(Formatters.miniWithPrefix(Formatters.lang().getString("reload.success", "Configurations reloaded.")));
        } catch (Exception e) {
            src.getSender().sendMessage(
                    ComponentUtils.error("An error occurred while reloading: " + e.getMessage())
            );
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }
        return Command.SINGLE_SUCCESS;
    }
}