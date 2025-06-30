package me.monoto.statistics.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.monoto.statistics.Statistics;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseClass {

    private static Statistics plugin;
    private static HikariDataSource dataSource;
    private static boolean useSQLite;

    public DatabaseClass(Statistics main, boolean useSQLite, String dbUrl, String dbUser, String dbPassword) {
        plugin = main;
        DatabaseClass.useSQLite = useSQLite;
        setupDataSource(dbUrl, dbUser, dbPassword);
        createDatabase();
    }

    private void setupDataSource(String dbUrl, String dbUser, String dbPassword) {
        HikariConfig config = new HikariConfig();

        if (useSQLite) {
            config.setJdbcUrl("jdbc:sqlite:" + getFile());
        } else {
            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPassword);
        }

        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection connect() {
        try {
            return dataSource.getConnection();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private void createDatabase() {
        if (useSQLite) {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                plugin.getLogger().warning("Failed to create plugin data folder!");
            }
            if (!getFile().exists()) {
                try (Connection connection = connect()) {
                    if (connection != null) {
                        plugin.getLogger().info("[GlobalStats] Database has been created.");
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
        createTable();
        DatabaseManager.getAllStatistics();
        DatabaseManager.getTopFiveStatistics();
    }

    private void createTable() {
        String bountySQL = "CREATE TABLE IF NOT EXISTS `player_statistics` (" +
                "`uuid` VARCHAR(100) PRIMARY KEY, " +
                "`name` VARCHAR(16) NOT NULL, " +
                "`fished` INTEGER NOT NULL, " +
                "`mined` INTEGER NOT NULL, " +
                "`killed` INTEGER NOT NULL, " +
                "`placed` INTEGER NOT NULL, " +
                "`traversed` INTEGER NOT NULL, " +
                "`harvested_crops` INTEGER NOT NULL DEFAULT 0" +
                ")";

        try (Connection connection = connect()) {
            if (connection != null) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(bountySQL);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void query(String sql) {
        try (Connection connection = connect()) {
            if (connection != null) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sql);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private File getFile() {
        return new File(plugin.getDataFolder(), "statistics.db");
    }
}
