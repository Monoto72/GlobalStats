package me.monoto.statistics.utils;

import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockCache {
    /*
     * This class is used to cache all the blocks that can be placed in the world.
     * Might need to be updated if new blocks are added in the future.
     * Currently a hacky solution, but it works. Though holds the main thread for a bit.
     */

    private static final ExecutorService EXEC = Executors.newSingleThreadExecutor(r -> new Thread(r, "BlockCache"));

    private static final Set<Material> DISALLOWED_BLOCKS = Set.of(
            Material.BEDROCK, Material.BARRIER, Material.COMMAND_BLOCK,
            Material.REPEATING_COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK,
            Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID, Material.JIGSAW,
            Material.DEBUG_STICK, Material.AIR, Material.VOID_AIR, Material.CAVE_AIR
    );

    private static final List<Material> ALLOWED_CROPS = List.of(
            Material.BEETROOT_SEEDS, Material.CARROT, Material.POTATO,
            Material.WHEAT_SEEDS, Material.NETHER_WART, Material.COCOA_BEANS,
            Material.BAMBOO, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
            Material.SUGAR_CANE, Material.PUMPKIN_SEEDS, Material.MELON_SEEDS,
            Material.SWEET_BERRIES, Material.CACTUS, Material.KELP, Material.TWISTING_VINES,
            Material.WARPED_WART_BLOCK, Material.SEA_PICKLE, Material.PUMPKIN,
            Material.MELON, Material.CHORUS_FRUIT
    );


    private static final CompletableFuture<Cache> CACHE_FUTURE =
            CompletableFuture.supplyAsync(BlockCache::computeAll, EXEC);

    // simple data holder
    public record Cache(
            List<Material> placeables
    ) {
    }

    public static CompletableFuture<Cache> initAsync() {
        return CACHE_FUTURE;
    }

    public static Cache getCache() {
        return CACHE_FUTURE.join();
    }

    public static List<Material> getPlaceableItems() {
        return getCache().placeables();
    }

    private static Cache computeAll() {
        Set<Material> placeSet = new HashSet<>();

        Set<Material> tagged = new HashSet<>();
        Tag<Material>[] tags = new Tag[]{
                Tag.SAPLINGS, Tag.LOGS, Tag.LEAVES, Tag.DIRT, Tag.FLOWERS, Tag.WOOL
                // Removed Tag.CROPS to exclude crop blocks
        };
        for (Tag<Material> tag : tags) {
            tagged.addAll(tag.getValues());
        }

        // Set of crops to exclude from placeables and drops
        Set<Material> cropSet = new HashSet<>(ALLOWED_CROPS);
        cropSet.addAll(BI_MAP.keySet());
        cropSet.addAll(BI_MAP.values());

        for (Material m : Material.values()) {
            if (m.isLegacy()) continue;
            if ((tagged.contains(m) || (m.isBlock() && !DISALLOWED_BLOCKS.contains(m))) && !cropSet.contains(m)) {
                Material safe = resolve(m);
                if (safe != null && !cropSet.contains(safe)) {
                    placeSet.add(safe);
                }
            }
        }

        Comparator<Material> byName = Comparator.comparing(Enum::name);
        List<Material> placeables = new ArrayList<>(placeSet);
        placeables.sort(byName);

        return new Cache(placeables);
    }



    public static final Map<Material, Material> BI_MAP;

    static {
        Map<Material, Material> m = new EnumMap<>(Material.class);
        m.put(Material.BEETROOTS, Material.BEETROOT_SEEDS);
        m.put(Material.BEETROOT_SEEDS, Material.BEETROOTS);
        m.put(Material.CARROTS, Material.CARROT);
        m.put(Material.CARROT, Material.CARROTS);
        m.put(Material.POTATOES, Material.POTATO);
        m.put(Material.POTATO, Material.POTATOES);
        m.put(Material.WHEAT, Material.WHEAT_SEEDS);
        m.put(Material.WHEAT_SEEDS, Material.WHEAT);
        m.put(Material.COCOA, Material.COCOA_BEANS);
        m.put(Material.COCOA_BEANS, Material.COCOA);
        m.put(Material.NETHER_WART, Material.NETHER_WART);

        BI_MAP = Collections.unmodifiableMap(m);
    }

    public static Material resolve(Material mat) {
        return BI_MAP.getOrDefault(mat, mat);
    }

    public static List<Material> getAllowedCrops() {
        return ALLOWED_CROPS;
    }

    /**
     * Call this from onDisable() so the executor cleanly shuts down.
     */
    public static void shutdown() {
        EXEC.shutdownNow();
    }
}