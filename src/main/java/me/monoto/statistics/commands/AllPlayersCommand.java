package me.monoto.statistics.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.monoto.statistics.menus.PlayerListMenu;
import me.monoto.statistics.utils.ComponentUtils;
import org.bukkit.entity.Player;

public class AllPlayersCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("all")
                .requires(src -> src.getSender() instanceof Player) // Ensure it's only used by players
                .executes(AllPlayersCommand::execute);
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        if (!(src.getSender() instanceof Player player)) {
            src.getSender().sendMessage(ComponentUtils.error("Only players can use this command."));
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }

        PlayerListMenu.initialise(player);
        return Command.SINGLE_SUCCESS;
    }
}
