package me.monoto.statistics.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class FormattersV2 {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private FormattersV2() {}

    /**
     * Formats a MiniMessage string with gradient support, default italic formatting,
     * and optional placeholder replacements.
     *
     * <p>
     * The method processes gradient tags (in the format:
     * <code>&lt;gradient:#START:#END&gt;text&lt;/gradient&gt;</code>) by converting them
     * into individual color tags for each character. It also applies italic formatting
     * to the whole message. If placeholders are provided, they are injected into the message.
     * </p>
     *
     * @param message      The MiniMessage string which may include gradient tags.
     * @param placeholders A Map of placeholders to their replacement Components. Can be null.
     * @return The deserialized Component with all formatting applied.
     */
    public static @NotNull Component format(String message, Map<String, Component> placeholders) {
        // Process gradients before any other formatting
        String processedMessage = processGradients(message);
        // Apply default italic formatting
        processedMessage = "<!italic>" + processedMessage;

        if (placeholders != null && !placeholders.isEmpty()) {
            List<TagResolver> resolvers = new ArrayList<>();
            for (Map.Entry<String, Component> entry : placeholders.entrySet()) {
                resolvers.add(Placeholder.component(entry.getKey(), entry.getValue()));
            }
            return MINI_MESSAGE.deserialize(processedMessage, TagResolver.resolver(resolvers));
        }
        return MINI_MESSAGE.deserialize(processedMessage);
    }

    /**
     * Overloaded method when no placeholders are needed.
     *
     * @param message The MiniMessage string which may include gradient tags.
     * @return The deserialized Component with gradient and italic formatting applied.
     */
    public static @NotNull Component format(String message) {
        return format(message, null);
    }

    /**
     * Processes gradient tags in the message.
     * <p>
     * Expected gradient tag format: <code>&lt;gradient:#START:#END&gt;text&lt;/gradient&gt;</code>
     * Each character of "text" is wrapped with an interpolated color tag.
     * </p>
     *
     * @param message The original message possibly containing gradient tags.
     * @return A new message where each gradient tag has been replaced with color tags.
     */
    private static String processGradients(String message) {
        Pattern pattern = Pattern.compile("<gradient:(#[0-9a-fA-F]{6}):(#[0-9a-fA-F]{6})>(.*?)</gradient>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(message);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String startHex = matcher.group(1);
            String endHex = matcher.group(2);
            String content = matcher.group(3);
            String gradFormatted = gradientText(content, startHex, endHex);
            matcher.appendReplacement(sb, gradFormatted);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Converts text into a series of color tags forming a gradient from startHex to endHex.
     *
     * @param text     The text to be formatted.
     * @param startHex The starting hex color (e.g., "#FF0000").
     * @param endHex   The ending hex color (e.g., "#00FF00").
     * @return A MiniMessage formatted string where each character is prefixed with its computed color.
     */
    private static String gradientText(String text, String startHex, String endHex) {
        if (text == null || text.isEmpty()) return "";
        Color start = Color.decode(startHex);
        Color end = Color.decode(endHex);
        int length = text.length();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            double ratio = length > 1 ? (double) i / (length - 1) : 0;
            int red = (int) (start.getRed() * (1 - ratio) + end.getRed() * ratio);
            int green = (int) (start.getGreen() * (1 - ratio) + end.getGreen() * ratio);
            int blue = (int) (start.getBlue() * (1 - ratio) + end.getBlue() * ratio);
            String hexColor = String.format("#%02x%02x%02x", red, green, blue);

            result.append("<").append(hexColor).append(">").append(text.charAt(i));
        }
        return result.toString();
    }

    /**
     * Formats a duration (in seconds) into a human-readable Component.
     *
     * @param seconds The duration in seconds.
     * @return A Component representing the formatted time.
     */
    public static Component time(int seconds) {
        int[] thresholds = {29030400, 2419200, 604800, 86400, 3600, 60, 1};
        String[] units = {"year", "month", "week", "day", "hour", "minute", "second"};

        for (int i = 0; i < thresholds.length; i++) {
            if (seconds >= thresholds[i]) {
                int quantity = seconds / thresholds[i];
                return formatTime(quantity, units[i]);
            }
        }

        return Component.text("0 seconds");
    }

    /**
     * Helper method to generate a time Component with correct singular/plural unit.
     *
     * @param quantity The amount.
     * @param unit     The time unit (e.g. "minute").
     * @return A Component with the formatted time.
     */
    private static Component formatTime(int quantity, String unit) {
        String unitLabel = quantity == 1 ? unit : unit + "s";
        return Component.text(quantity + " " + unitLabel);
    }
}