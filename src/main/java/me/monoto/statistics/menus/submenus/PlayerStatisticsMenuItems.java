package me.monoto.statistics.menus.submenus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.monoto.statistics.menus.GlobalMenu;
import me.monoto.statistics.menus.utils.Pagination;
import me.monoto.statistics.utils.Formatters;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PlayerStatisticsMenuItems {

    public static void getItemPreview (String type, Player player, OfflinePlayer target) {

        PaginatedGui gui = Gui.paginated().title(Component.text(Formatters.getPossessionString(Objects.requireNonNull(target.getName())) + " stats | Page: 1")).rows(4).pageSize(27).create();
        Pagination.getPaginatedUtil(gui, target);

        gui.setItem(31, ItemBuilder.skull().owner(target).name(Formatters.mini(Formatters.lang().getString("gui.main.player_head.title", "<player>"), "player", Component.text(Formatters.getPossessionString(Objects.requireNonNull(target.getName())))).decoration(TextDecoration.ITALIC, false))
                .asGuiItem(event -> GlobalMenu.initialise((Player) event.getWhoClicked(), target)));

        switch (type) {
            case "fishing" -> getFish(gui);
            case "killing" -> getKills(gui, target);
            case "travelling" -> getMovements(gui, target);
            case "mining", "placing" -> getBlocks(gui, type, target);
        }

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.open(player);
    }

    private static void getFish(PaginatedGui gui) {

    }

    private static void getKills(PaginatedGui gui, OfflinePlayer target) {
        for (EntityType type : EntityType.values()) {
            try {
                if (target.getStatistic(Statistic.KILL_ENTITY, type) > 0) {
                    int statAmount = target.getStatistic(Statistic.KILL_ENTITY, type);

                    TextComponent lore = Component.text().content("Total Killed" + ": ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                            .append(Component.text().content(String.valueOf(statAmount)).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                            .build();

                    Material material;

                    try {
                        material = Material.valueOf(type + "_SPAWN_EGG");
                    } catch (IllegalArgumentException exception) {
                        material = switch (type) {
                            case IRON_GOLEM -> Material.IRON_INGOT;
                            case SNOWMAN -> Material.SNOWBALL;
                            case WITHER -> Material.WITHER_SKELETON_SKULL;
                            default -> null;
                        };
                    }

                    if (material != null) {
                        GuiItem item = ItemBuilder.from(material).name(Component.text(Formatters.capitaliseEachWord(type.name())).decoration(TextDecoration.ITALIC, false)).lore(lore).asGuiItem();
                        gui.addItem(item);
                    }
                }
            } catch (IllegalArgumentException ignore) {}
        }
    }

    private static void getMovements(PaginatedGui gui, OfflinePlayer target) {
        ArrayList<String> itemNames = new ArrayList<>(Arrays.asList("Distance Walked", "Distance Sprinted", "Distance Swum", "Distance Walked on Water", "Distance Walked under Water", "Distance Climbed", "Distance Crouched", "Distance Fallen", "Distance by Elytra", "Distance by Boat", "Distance by Horse", "Distance by Minecart", "Distance by Pig", "Distance by Strider", "Times Jumps"));
        ArrayList<Statistic> statistics = new ArrayList<>(Arrays.asList(Statistic.WALK_ONE_CM, Statistic.SPRINT_ONE_CM, Statistic.SWIM_ONE_CM, Statistic.WALK_ON_WATER_ONE_CM, Statistic.WALK_UNDER_WATER_ONE_CM, Statistic.CLIMB_ONE_CM, Statistic.CROUCH_ONE_CM, Statistic.FALL_ONE_CM, Statistic.AVIATE_ONE_CM, Statistic.BOAT_ONE_CM, Statistic.HORSE_ONE_CM, Statistic.MINECART_ONE_CM, Statistic.PIG_ONE_CM, Statistic.STRIDER_ONE_CM, Statistic.JUMP));
        ArrayList<Material> materials = new ArrayList<>(Arrays.asList(Material.LEATHER_BOOTS, Material.GOLDEN_BOOTS, Material.WATER_BUCKET, Material.ICE, Material.DIAMOND_HELMET, Material.LADDER, Material.LEATHER_BOOTS, Material.LINGERING_POTION, Material.ELYTRA, Material.OAK_BOAT, Material.DIAMOND_HORSE_ARMOR, Material.MINECART, Material.PIG_SPAWN_EGG, Material.STRIDER_SPAWN_EGG, Material.IRON_BOOTS));

        for (int index = 0; index < itemNames.size(); index++) {
            int amount = target.getStatistic(Statistic.JUMP);
            if (statistics.get(index) != Statistic.JUMP) {
                amount = (int) Math.floor(target.getStatistic(statistics.get(index))) / 100;
            }

            TextComponent lore = Component.text().content("Total: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text().content(String.valueOf(amount)).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                    .build();

            GuiItem item = ItemBuilder.from(materials.get(index)).lore(lore).flags(ItemFlag.HIDE_POTION_EFFECTS)
                    .name(Component.text(itemNames.get(index)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .asGuiItem();
            gui.addItem(item);
        }
    }

    private static void getBlocks(PaginatedGui gui, String type, OfflinePlayer target) {
        Statistic statistic = Objects.equals(type, "mined") ? Statistic.MINE_BLOCK : Statistic.USE_ITEM;
        
        for (Material material : Material.values()) {
            if (target.getStatistic(statistic, material) > 0 && material.isBlock()) {
                int statAmount = target.getStatistic(statistic, material);

                TextComponent lore = Component.text().content("Total " + type + ": ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text().content(String.valueOf(statAmount)).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                        .build();

                GuiItem item = ItemBuilder.from(material).lore(lore).asGuiItem();
                gui.addItem(item);
            }
        }
    }
}
