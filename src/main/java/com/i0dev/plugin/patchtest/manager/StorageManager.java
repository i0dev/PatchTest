package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.ScoreEntry;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Storage manager, using SQLite as main storage system.
 *
 * @author Andrew Magnuson
 */
public class StorageManager extends AbstractManager {

    @Getter
    private static final StorageManager instance = new StorageManager();

    private Connection connection;

    @SneakyThrows
    @Override
    public void initialize() {
        connection = DriverManager.getConnection("jdbc:sqlite:" + PatchTestPlugin.getPlugin().getDataFolder() + "/storage.db");
        System.out.println("Connected to SQLite Database");
        createTables();
    }

    @SneakyThrows
    @Override
    public void deinitialize() {
        if (connection != null) connection.close();
    }

    /**
     * This method will create the necessary tables in order for the storage system to work properly.
     */
    @SneakyThrows
    public void createTables() {
        connection.prepareStatement("" +
                "CREATE TABLE IF NOT EXISTS `ranked_scores` (" +
                "`id`          INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, " +
                "`sessionUUID` VARCHAR(36)                           NOT NULL, " +
                "`teamSize`    VARCHAR(36)                           NOT NULL, " +
                "`lengthHeld`  BIGINT                                NOT NULL, " +
                "`timeEnded`   BIGINT                                NOT NULL" +
                ");" +
                "CREATE INDEX IF NOT EXISTS `ranked_scores_session_uuid` ON `ranked_scores` (`sessionUUID`);"
        ).execute();

        connection.prepareStatement("" +
                "CREATE TABLE IF NOT EXISTS `ranked_scores_sub_users` (" +
                "`sessionUUID` VARCHAR(36) NOT NULL, " +
                "`playerUUID`  VARCHAR(36) NOT NULL" +
                ");" +
                "CREATE INDEX IF NOT EXISTS `ranked_scores_sub_users_session_uuid` ON `ranked_scores_sub_users` (`sessionUUID`);"
        ).execute();
    }


    /**
     * Adds a new {@link ScoreEntry} to the sql database
     *
     * @param entry The entry to add.
     */
    @SneakyThrows
    public void addEntry(ScoreEntry entry) {
        connection.prepareStatement(String.format(
                "INSERT INTO ranked_scores (sessionUUID, teamSize, lengthHeld, timeEnded)" +
                        "VALUES ('%s', '%s', %s, %s);",
                entry.getSessionUUID().toString(),
                entry.getTeamSize().name(),
                entry.getLengthHeld(),
                entry.getTimestamp()
        )).execute();


        entry.getPlayers().forEach(uuid -> {
            try {
                connection.prepareStatement(String.format("" +
                                "INSERT INTO ranked_scores_sub_users(sessionUUID, playerUUID)" +
                                "VALUES ('%s', '%s')",
                        entry.getSessionUUID(),
                        uuid
                )).execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Executes a specified query to the SQL database
     *
     * @param query The query to execute.
     * @return The results of the query.
     */
    @SneakyThrows
    public ResultSet executeQuery(String query) {
        return connection.prepareStatement(query).executeQuery();
    }

}
