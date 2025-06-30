package me.monoto.statistics.utils;

import me.monoto.statistics.Statistics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Formatters {
    public static String getPossessionString(String string) {
        return string.endsWith("s") ? string : string + "'s";
    }

    public static Component getPlayerSkullTitle(OfflinePlayer player) {
        return Formatters.mini(Formatters.lang().getString("gui.main.player-head.title", "<player> statistics"), "player", Component.text(Formatters.getPossessionString(Objects.requireNonNull(player.getName()))).decoration(TextDecoration.ITALIC, false));
    }

    public static String getDistanceFormatter(double value) {
        String[] arr = {"", "km", "m", "mi", "yd", "ft"};
        int index = 0;
        while ((value / 1000) >= 1) {
            value = value / 1000;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]);
    }

    public static String capitaliseEachWord(String string) {
        return Arrays.stream(string.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public static YamlConfiguration lang() {
        return Statistics.getInstance().getLanguage();
    }

    public static Component mini(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }

    public static Component mini(String message, String placeholder, Component replacement) {
        return MiniMessage.miniMessage().deserialize(message, Placeholder.component(placeholder, replacement));
    }

    public static Component miniMulti(String message, List<String> placeholders, List<Component> replacements) {
        if (placeholders.size() != replacements.size()) {
            throw new IllegalArgumentException("Placeholder and replacement list sizes must match");
        }

        TagResolver.Builder builder = TagResolver.builder();
        for (int i = 0; i < placeholders.size(); i++) {
            builder.resolver(Placeholder.component(placeholders.get(i), replacements.get(i)));
        }

        return MiniMessage.miniMessage().deserialize(message, builder.build());
    }

    public static Component miniWithPrefix(String message) {
        return ComponentUtils.PREFIX.append(mini(message));
    }

    public static Component miniWithPrefix(String message, String placeholder, Component replacement) {
        return ComponentUtils.PREFIX.append(mini(message, placeholder, replacement));
    }

    public static Component miniMultiWithPrefix(String message, List<String> placeholders, List<Component> replacements) {
        return ComponentUtils.PREFIX.append(miniMulti(message, placeholders, replacements));
    }
}
