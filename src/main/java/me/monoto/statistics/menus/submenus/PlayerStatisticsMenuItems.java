package me.monoto.statistics.menus.submenus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.monoto.statistics.Statistics;
import me.monoto.statistics.menus.GlobalMenu;
import me.monoto.statistics.menus.utils.Pagination;
import me.monoto.statistics.utils.BlockCache;
import me.monoto.statistics.utils.Formatters;
import me.monoto.statistics.utils.FormattersV2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class PlayerStatisticsMenuItems {

    public static void getItemPreview (String type, Player player, OfflinePlayer target) {

        PaginatedGui gui = Gui.paginated().title(Formatters.miniMulti(Formatters.lang().getString("gui.main.title-player", "<black><player> <type> stats"), List.of("player", "type"), List.of(
                Component.text(Formatters.getPossessionString(Objects.requireNonNull(target.getName()))),
                Component.text(type)
        ))).rows(4).pageSize(27).create();
        Pagination.getPaginatedUtil(gui, target, "player", type);

        gui.setItem(31, ItemBuilder.skull().owner(target).name(Formatters.mini(Formatters.lang().getString("gui.main.player_head.title", "<player>"), "player", Component.text(Formatters.getPossessionString(Objects.requireNonNull(target.getName())))).decoration(TextDecoration.ITALIC, false))
                .asGuiItem(event -> GlobalMenu.initialise((Player) event.getWhoClicked(), target)));

        gui.clearPageItems();

        switch (type) {
            case "fishing" -> getFish(gui, target);
            case "killing" -> getKills(gui, target);
            case "travelling" -> getMovements(gui, target);
            case "mining", "placing" -> getBlocks(gui, type, target);
            case "crops" -> getCropHarvestStats(gui, target);
        }

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.open(player);
    }

    private static void getFish(PaginatedGui gui, OfflinePlayer target) { // PDC Currently doesn't allow for OfflinePlayers
        if (target != null && target.getPlayer() != null) {

            try {
                Player onlineTarget = target.getPlayer();
                if (onlineTarget == null) return;

                String data = onlineTarget.getPersistentDataContainer().get(
                        new NamespacedKey(Statistics.getInstance(), "FISHING_STATS"),
                        PersistentDataType.STRING
                );

                if (data == null) return;

                JsonObject json = com.google.gson.JsonParser.parseString(data).getAsJsonObject();

                for (String key : json.keySet()) {
                    ItemStack itemStack = new ItemStack(Material.valueOf(key), 1);

                    if (key.equals("POTION")) {
                        itemStack = new ItemStack(Material.POTION);
                        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
                        potionMeta.setBasePotionType(PotionType.WATER);
                        itemStack.setItemMeta(potionMeta);
                    }

                    int amount = json.get(key).getAsInt();

                    GuiItem item = ItemBuilder.from(itemStack)
                            .lore(Formatters.mini(
                                    Formatters.lang().getString("gui.main.fishing.lore", "<white>Total: <amount>"),
                                    "amount", Component.text(amount)
                            ).decoration(TextDecoration.ITALIC, false))
                            .asGuiItem();

                    gui.addItem(item);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static void getKills(PaginatedGui gui, OfflinePlayer target) {
        for (EntityType type : EntityType.values()) {
            try {
                if (target.getStatistic(Statistic.KILL_ENTITY, type) > 0) {
                    int statAmount = target.getStatistic(Statistic.KILL_ENTITY, type);

                    Material material;

                    try {
                        material = Material.valueOf(type + "_SPAWN_EGG");
                    } catch (IllegalArgumentException exception) {
                        material = switch (type) {
                            case IRON_GOLEM -> Material.IRON_INGOT;
                            case SNOW_GOLEM -> Material.SNOWBALL;
                            case WITHER -> Material.WITHER_SKELETON_SKULL;
                            default -> null;
                        };
                    }

                    if (material != null) {
                        GuiItem item = ItemBuilder.from(material).name(Component.translatable(type).decoration(TextDecoration.ITALIC, false))
                                .lore(Formatters.mini(Formatters.lang().getString("gui.main.killing.lore", "<white>Total: <amount>"), "amount", Component.text(statAmount)).decoration(TextDecoration.ITALIC, false)).asGuiItem();
                        gui.addItem(item);
                    }
                }
            } catch (IllegalArgumentException ignored) {} // Catch unknown EntityTypes for example, Player, etc.
        }
    }

    private static void getMovements(PaginatedGui gui, OfflinePlayer target) {
        ArrayList<String> itemNames = new ArrayList<>(Arrays.asList("Distance Walked", "Distance Sprinted", "Distance Swum", "Distance Walked on Water", "Distance Walked under Water", "Distance Climbed", "Distance Crouched", "Distance Fallen", "Distance by Elytra", "Distance by Boat", "Distance by Horse", "Distance by Minecart", "Distance by Pig", "Distance by Strider", "Times Jumps"));
        ArrayList<Statistic> statistics = new ArrayList<>(Arrays.asList(Statistic.WALK_ONE_CM, Statistic.SPRINT_ONE_CM, Statistic.SWIM_ONE_CM, Statistic.WALK_ON_WATER_ONE_CM, Statistic.WALK_UNDER_WATER_ONE_CM, Statistic.CLIMB_ONE_CM, Statistic.CROUCH_ONE_CM, Statistic.FALL_ONE_CM, Statistic.AVIATE_ONE_CM, Statistic.BOAT_ONE_CM, Statistic.HORSE_ONE_CM, Statistic.MINECART_ONE_CM, Statistic.PIG_ONE_CM, Statistic.STRIDER_ONE_CM, Statistic.JUMP));
        ArrayList<Material> materials = new ArrayList<>(Arrays.asList(Material.LEATHER_BOOTS, Material.GOLDEN_BOOTS, Material.WATER_BUCKET, Material.ICE, Material.DIAMOND_HELMET, Material.LADDER, Material.LEATHER_BOOTS, Material.LINGERING_POTION, Material.ELYTRA, Material.OAK_BOAT, Material.DIAMOND_HORSE_ARMOR, Material.MINECART, Material.PIG_SPAWN_EGG, Material.STRIDER_SPAWN_EGG, Material.IRON_BOOTS));

        for (int index = 0; index < itemNames.size(); index++) {
            int statAmount = target.getStatistic(Statistic.JUMP);
            if (statistics.get(index) != Statistic.JUMP) {
                statAmount = (int) (double) target.getStatistic(statistics.get(index)) / 100;
            }

            GuiItem item = ItemBuilder.from(materials.get(index)).lore(Formatters.mini(Formatters.lang().getString("gui.main.killing.lore", "<white>Total: <amount>"), "amount", Component.text(statAmount)).decoration(TextDecoration.ITALIC, false))// .flags(ItemFlag.)
                    .name(Component.text(itemNames.get(index)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .asGuiItem();
            gui.addItem(item);
        }
    }

    private static void getBlocks(PaginatedGui gui, String type, OfflinePlayer target) {
            Statistic statistic = type.equals("mining") ? Statistic.MINE_BLOCK : Statistic.USE_ITEM;
            String typeLore = type.equals("mining") ? "gui.main.mining.lore" : "gui.main.placing.lore";

            List<Material> relevantMaterials = BlockCache.getPlaceableItems();

            for (Material material : relevantMaterials) {
                if (!material.isItem()) continue;

                int statAmount = target.getStatistic(statistic, material);
                if (statAmount <= 0) continue;

                Bukkit.getLogger().info(material.name() + ": " + statAmount);


                GuiItem item = ItemBuilder.from(material)
                        .lore(Formatters.mini(
                                        Formatters.lang().getString(typeLore, "<white>Total: <amount>"),
                                        "amount", Component.text(statAmount))
                                .decoration(TextDecoration.ITALIC, false))
                        .asGuiItem();

                gui.addItem(item);
        };
    }

    private static void getCropHarvestStats(PaginatedGui gui, OfflinePlayer target) {
        Statistic statistic = Statistic.MINE_BLOCK;
        String typeLore = "gui.main.crops.lore";

        Player onlineTarget = target.getPlayer();
        Set<Material> processedMaterials = new HashSet<>();

        // Helper to fetch additional counts from persistent JSON data
        BiFunction<Material[], Player, Integer> getAdditionalCount = (materials, player) -> {
            int extraCount = 0;
            if (player == null) return 0;

            try {
                String jsonData = player.getPersistentDataContainer().get(
                        new NamespacedKey(Statistics.getInstance(), "CUSTOM_CROP_STATS"),
                        PersistentDataType.STRING
                );
                if (jsonData != null) {
                    JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                        JsonObject cropData = entry.getValue().getAsJsonObject();
                        String matName = cropData.get("material").getAsString();
                        int count = cropData.get("count").getAsInt();

                        for (Material m : materials) {
                            if (matName.equalsIgnoreCase(m.name())) {
                                extraCount += count;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return extraCount;
        };

        // 1. Process paired crops from BI_MAP
        for (Material crop : BlockCache.BI_MAP.keySet()) {
            if (!crop.isItem()) continue;
            if (crop.name().endsWith("_SEEDS") || !processedMaterials.add(crop)) continue;

            Material pair = BlockCache.resolve(crop);
            processedMaterials.add(pair);

            int totalStat = target.getStatistic(statistic, crop) + target.getStatistic(statistic, pair);
            totalStat += getAdditionalCount.apply(new Material[]{crop, pair}, onlineTarget);

            if (totalStat <= 0) continue;

            GuiItem item = ItemBuilder.from(crop)
                    .lore(Formatters.mini(
                                    Formatters.lang().getString(typeLore, "<white>Total: <amount>"),
                                    "amount", Component.text(totalStat))
                            .decoration(TextDecoration.ITALIC, false))
                    .asGuiItem();

            gui.addItem(item);
        }

        // 2. Process allowed crops not already processed
        for (Material crop : BlockCache.getAllowedCrops()) {
            if (!crop.isItem()) continue;
            if (processedMaterials.contains(crop)) continue;

            Material pair = BlockCache.BI_MAP.getOrDefault(crop, crop);
            if (!processedMaterials.add(crop)) continue;
            processedMaterials.add(pair);

            int totalStat = target.getStatistic(statistic, crop) + target.getStatistic(statistic, pair);
            totalStat += getAdditionalCount.apply(new Material[]{crop, pair}, onlineTarget);

            if (totalStat <= 0) continue;

            GuiItem item = ItemBuilder.from(crop)
                    .lore(Formatters.mini(
                                    Formatters.lang().getString(typeLore, "<white>Total: <amount>"),
                                    "amount", Component.text(totalStat))
                            .decoration(TextDecoration.ITALIC, false))
                    .asGuiItem();

            gui.addItem(item);
        }

        // 3. Process wild crops stats from WILD_CROPS_STATS
        if (onlineTarget != null) {
            try {
                String extraData = onlineTarget.getPersistentDataContainer().get(
                        new NamespacedKey(Statistics.getInstance(), "WILD_CROPS_STATS"),
                        PersistentDataType.STRING
                );

                if (extraData != null) {
                    JsonObject json = JsonParser.parseString(extraData).getAsJsonObject();

                    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                        JsonObject cropData = entry.getValue().getAsJsonObject();
                        int count = cropData.get("count").getAsInt();
                        Material cropMaterial = Material.valueOf(cropData.get("material").getAsString());

                        if (!cropMaterial.isItem() || !processedMaterials.add(cropMaterial)) continue;

                        String displayName = cropData.get("name").getAsString();

                        GuiItem item = ItemBuilder.from(cropMaterial)
                                .name(FormattersV2.format(displayName))
                                .lore(Formatters.mini(
                                                Formatters.lang().getString(typeLore, "<white>Total: <amount>"),
                                                "amount", Component.text(count))
                                        .decoration(TextDecoration.ITALIC, false))
                                .asGuiItem();

                        gui.addItem(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
