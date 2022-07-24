package me.monoto.statistics.utils;

import me.monoto.statistics.Statistics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Formatters {
    public static String getPossessionString(String string) {
        return string.endsWith("s") ? string : string + "'s";
    }

    public static TextComponent getPlayerSkullTitle(OfflinePlayer player) {
        return Component.text().content(getPossessionString(Objects.requireNonNull(player.getName())) + " statistics").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false).build();
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
        return MiniMessage.miniMessage().deserialize("<reset><white>" + message);
    }

    public static Component mini(String message, String placeholder, Component replacement) {
        return MiniMessage.miniMessage().deserialize("<reset><white>" + message, Placeholder.component(placeholder, replacement));
    }
}
