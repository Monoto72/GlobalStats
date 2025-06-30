package me.monoto.statistics.database;

import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseManager {

    public static void getAllStatistics() {
        String sql = "SELECT COALESCE(SUM(`fished`), 0) AS total_fished, " +
                "COALESCE(SUM(`mined`), 0) AS total_mined, " +
                "COALESCE(SUM(`killed`), 0) AS total_killed, " +
                "COALESCE(SUM(`placed`), 0) AS total_placed, " +
                "COALESCE(SUM(`traversed`), 0) AS total_traversed, " +
                "COALESCE(SUM(`harvested_crops`), 0) AS total_harvested_crops " +
                "FROM `player_statistics`";

        try (Connection connection = DatabaseClass.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                StatisticsManager.setGlobalStatistics(
                        resultSet.getInt("total_fished"),
                        resultSet.getInt("total_mined"),
                        resultSet.getInt("total_killed"),
                        resultSet.getInt("total_placed"),
                        resultSet.getInt("total_traversed"),
                        resultSet.getInt("total_harvested_crops")
                );
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void savePlayer(UUID uuid) {
        PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(uuid);
        if (stats == null) return;

        String sql = "INSERT INTO `player_statistics` (`uuid`, `name`, `fished`, `mined`, `killed`, `placed`, `traversed`, `harvested_crops`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseClass.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, stats.getPlayerUUID().toString());
            statement.setString(2, stats.getPlayerName());
            statement.setInt(3, stats.getFishedFish());
            statement.setInt(4, stats.getMinedBlocks());
            statement.setInt(5, stats.getMobsKilled());
            statement.setInt(6, stats.getPlacedBlocks());
            statement.setInt(7, stats.getTraversedBlocks());
            statement.setInt(8, stats.getHarvestedCrops());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayer(UUID uuid) {
        PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(uuid);
        if (stats == null) return;

        String sql = "UPDATE `player_statistics` SET " +
                "`name` = ?, " +
                "`fished` = ?, " +
                "`mined` = ?, " +
                "`killed` = ?, " +
                "`placed` = ?, " +
                "`traversed` = ?, " +
                "`harvested_crops` = ? " +
                "WHERE `uuid` = ?";

        try (Connection connection = DatabaseClass.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, stats.getPlayerName());
            statement.setInt(2, stats.getFishedFish());
            statement.setInt(3, stats.getMinedBlocks());
            statement.setInt(4, stats.getMobsKilled());
            statement.setInt(5, stats.getPlacedBlocks());
            statement.setInt(6, stats.getTraversedBlocks());
            statement.setInt(7, stats.getHarvestedCrops());
            statement.setString(8, uuid.toString());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getPlayer(UUID uuid) {
        String sql = "SELECT * FROM `player_statistics` WHERE `uuid` = ?";

        try (Connection connection = DatabaseClass.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    StatisticsManager.setPlayerStatistics(
                            UUID.fromString(resultSet.getString("uuid")),
                            resultSet.getString("name"),
                            resultSet.getInt("fished"),
                            resultSet.getInt("mined"),
                            resultSet.getInt("killed"),
                            resultSet.getInt("placed"),
                            resultSet.getInt("traversed"),
                            resultSet.getInt("harvested_crops")
                    );
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void updatePlayerName(String name, UUID uuid) {
        String sql = "UPDATE `player_statistics` SET `name` = ? WHERE `uuid` = ?";

        try (Connection connection = DatabaseClass.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, uuid.toString());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getTopFiveStatistics() {
        List<String> queries = Arrays.asList(
                "SELECT `name`, `fished` AS total FROM `player_statistics` ORDER BY `fished` DESC LIMIT 5",
                "SELECT `name`, `mined` AS total FROM `player_statistics` ORDER BY `mined` DESC LIMIT 5",
                "SELECT `name`, `killed` AS total FROM `player_statistics` ORDER BY `killed` DESC LIMIT 5",
                "SELECT `name`, `placed` AS total FROM `player_statistics` ORDER BY `placed` DESC LIMIT 5",
                "SELECT `name`, `traversed` AS total FROM `player_statistics` ORDER BY `traversed` DESC LIMIT 5",
                "SELECT `name`, `harvested_crops` AS total FROM `player_statistics` ORDER BY `harvested_crops` DESC LIMIT 5"
        );

        HashMap<String, LinkedHashMap<String, Integer>> topFive = new HashMap<>();
        String[] categories = {"fishing", "mining", "killing", "placing", "travelling", "harvested_crops"};

        for (int i = 0; i < queries.size(); i++) {
            try (Connection connection = DatabaseClass.connect();
                 PreparedStatement statement = connection.prepareStatement(queries.get(i));
                 ResultSet resultSet = statement.executeQuery()) {

                LinkedHashMap<String, Integer> playerData = new LinkedHashMap<>();

                while (resultSet.next()) {
                    playerData.put(resultSet.getString("name"), resultSet.getInt("total"));
                }

                topFive.put(categories[i], playerData);

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        StatisticsManager.setTopFiveStatistics(topFive);
    }
}
