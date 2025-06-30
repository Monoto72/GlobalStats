package me.monoto.statistics.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ComponentUtils {
    private ComponentUtils() {} // prevent instantiation

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    /**
     * A gradient “PrisonUpgrades” prefix, fading from dark gray to gold,
     * followed by a space in light gray.
     */
    public static final Component PREFIX = MINI.deserialize(
            "<gradient:#555555:#FFD700><bold>GlobalStatistics</bold></gradient><gray> "
    );

    private static final TextColor COLOR_SUCCESS = TextColor.color(0x55FF55);
    private static final TextColor COLOR_WARNING = TextColor.color(0xFFB052);
    private static final TextColor COLOR_ERROR   = TextColor.color(0xFF5555);

    /**
     * Prefix + green success message.
     */
    public static Component success(String message) {
        return PREFIX.append(Component.text(message).color(COLOR_SUCCESS));
    }

    /**
     * Prefix + yellow warning message.
     */
    public static Component warning(String message) {
        return PREFIX.append(Component.text(message).color(COLOR_WARNING));
    }

    /**
     * Prefix + red error message.
     */
    public static Component error(String message) {
        return PREFIX.append(Component.text(message).color(COLOR_ERROR));
    }

    /**
     * Overloads taking an existing Component payload (useful if you need
     * more complex formatting than plain text).
     */
    public static Component success(Component payload) {
        return PREFIX.append(payload.color(COLOR_SUCCESS));
    }

    public static Component warning(Component payload) {
        return PREFIX.append(payload.color(COLOR_WARNING));
    }

    public static Component error(Component payload) {
        return PREFIX.append(payload.color(COLOR_ERROR));
    }
}
