package me.monoto.statistics.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Formatters {
    public static String getPossessionString(String string) {
        return string.endsWith("s") ? string : string + "'s";
    }

    public static TextComponent getPlayerSkullTitle(Player player) {
        return Component.text().content(getPossessionString(player.getName()) + " statistics").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false).build();
    }

    public static String getIntFormatter(double value) {
        String[] arr = {"", "k", "m", "b", "t", "p", "e"};
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
}
