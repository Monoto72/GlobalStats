package me.monoto.statistics.database;

import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DatabaseManager {
    public static void getAllStatistics() {
        String sql = "SELECT COALESCE(SUM(`fished`), 0) as `total_fished`, COALESCE(SUM(`mined`), 0) as `total_mined`, COALESCE(SUM(`killed`), 0) as `total_killed`, COALESCE(SUM(`placed`), 0) as `total_placed`, COALESCE(SUM(`traversed`), 0) as `total_traversed` FROM `player_statistics`";

        try (Connection connection = DatabaseClass.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)
        ) {
            while (resultSet.next()) {
                StatisticsManager.setGlobalStatistics(
                        resultSet.getInt("total_fished"),
                        resultSet.getInt("total_mined"),
                        resultSet.getInt("total_killed"),
                        resultSet.getInt("total_placed"),
                        resultSet.getInt("total_traversed")
                );
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void savePlayer (UUID uuid) {
        PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(uuid);

        DatabaseClass.query("INSERT INTO `player_statistics` (`uuid`, `name`, `fished`, `mined`, `killed`, `placed`, `traversed`) VALUES (\"" +
                stats.getPlayerUUID() + "\", \"" +
                stats.getPlayerName() + "\", " +
                stats.getFishedFish() + ", " +
                stats.getMinedBlocks() + ", " +
                stats.getMobsKilled() + ", " +
                stats.getPlacedBlocks() + ", " +
                stats.getTraversedBlocks() + ");"
        );
    }


    public static void updatePlayer(UUID uuid) {
        PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(uuid);

        DatabaseClass.query("UPDATE `player_statistics` SET " +
                "`name`=\"" + stats.getPlayerName() + "\", " +
                "`fished`=" + stats.getFishedFish() + ", " +
                "`mined`=" + stats.getMinedBlocks() + ", " +
                "`killed`=" + stats.getMobsKilled() + ", " +
                "`placed`=" + stats.getPlacedBlocks() + ", " +
                "`traversed`=" + stats.getTraversedBlocks() + " " +
                "WHERE `uuid`=\"" + uuid + "\""
        );
    }

    public static void getPlayer(UUID uuid) {
        String sql = "SELECT * FROM `player_statistics` WHERE `uuid`=\"" + uuid + "\"";

        try (Connection connection = DatabaseClass.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)
        ) {
            while (resultSet.next()) {
                StatisticsManager.setPlayerStatistics(
                        UUID.fromString(resultSet.getString("uuid")),
                        resultSet.getString("name"),
                        resultSet.getInt("fished"),
                        resultSet.getInt("mined"),
                        resultSet.getInt("killed"),
                        resultSet.getInt("placed"),
                        resultSet.getInt("traversed")
                );
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void updatePlayerName(String name, UUID uuid) {
        DatabaseClass.query("UPDATE `player_statistics` SET `name`=\"" + name + "\" WHERE `uuid`=\"" + uuid + "\"");
    }

    public static void getTopThreeStatistics() {
        ArrayList<String> queries = new ArrayList<>();
        queries.add("SELECT `name`, `fished` as `total` FROM `player_statistics` ORDER BY `fished` DESC LIMIT 3");
        queries.add("SELECT `name`, `mined` as `total` FROM `player_statistics` ORDER BY `mined` DESC LIMIT 3");
        queries.add("SELECT `name`, `killed` as `total` FROM `player_statistics` ORDER BY `killed` DESC LIMIT 3");
        queries.add("SELECT `name`, `placed` as `total` FROM `player_statistics` ORDER BY `placed` DESC LIMIT 3");
        queries.add("SELECT `name`, `traversed` as `total` FROM `player_statistics` ORDER BY `traversed` DESC LIMIT 3");

        HashMap<String, HashMap<String, Integer>> topThree = new HashMap<>();

        for (int index = 0; index < queries.size(); index++) {
            try (Connection connection = DatabaseClass.connect();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(queries.get(index))
            ) {
                HashMap<String, Integer> playerData = new HashMap<>();
                while (resultSet.next()) {
                    playerData.put(resultSet.getString("name"), resultSet.getInt("total"));
                }

                topThree.put(index == 0 ? "fishing"
                        : (index == 1 ? "mining"
                        : (index == 2 ? "killing"
                        : (index == 3 ? "placing"
                        : "travelling"))), playerData);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        StatisticsManager.setTopThreeStatistics(topThree);
    }
}
