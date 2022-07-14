package me.monoto.statistics.database;

import me.monoto.statistics.Statistics;

import java.io.File;
import java.sql.*;

public class DatabaseClass {

    private static Statistics plugin;

    public DatabaseClass(Statistics main) {
        plugin = main;
        createDatabase();
    }

    public static Connection connect() {
        String url = "jdbc:sqlite:" + getFile();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return connection;
    }

    private static void createDatabase() {
        if (!plugin.getDataFolder().mkdirs()) {
            if (!getFile().exists()) {
                try (Connection connection = connect()) {
                    if (connection != null) {
                        System.out.println("[GlobalStats] Database has been created.");
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
            createTable();
            DatabaseManager.getAllStatistics();
        }
    }

    private static void createTable() {
        String bountySQL = "CREATE TABLE IF NOT EXISTS `player_statistics` (" +
                "`uuid` VARCHAR(100) PRIMARY KEY, " +
                "`name` VARCHAR(16) NOT NULL, " +
                "`fished` INTEGER NOT NULL, " +
                "`mined` INTEGER NOT NULL, " +
                "`killed` INTEGER NOT NULL, " +
                "`placed` INTEGER NOT NULL, " +
                "`traversed` INTEGER NOT NULL" +
                ")";

        try (
                Connection connection = connect();
                Statement statement = connection.createStatement()
        ) {
            statement.execute(bountySQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void query(String sql) {
        try (
                Connection connection = connect();
                Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static File getFile() {
        return new File(plugin.getDataFolder(), "statistics.db");
    }
}
