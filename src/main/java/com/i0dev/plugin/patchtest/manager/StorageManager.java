package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.ScoreEntry;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Storage manager
 *
 * @author Andrew Magnuson
 */
public class StorageManager extends AbstractManager {

    @Getter
    private static final StorageManager instance = new StorageManager();

    private Connection connection;

    @Override
    public void initialize() {
        connect();
        createTables();
    }

    @Override
    public void deinitialize() {
        disconnect();
    }

    @SneakyThrows
    public void connect() {
        //    Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + PatchTestPlugin.getPlugin().getDataFolder() + "/storage.db");
        System.out.println("Connected to SQLite");
    }

    @SneakyThrows
    public void disconnect() {
        if (connection != null) connection.close();
    }

    @SneakyThrows
    public void createTables() {
        connection.prepareStatement("" +
                "CREATE TABLE IF NOT EXISTS `ranked_scores` (" +
                "`id`          INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, " +
                "`sessionUUID` VARCHAR(36)                           NOT NULL, " +
                "`creatorUUID` VARCHAR(36)                           NOT NULL, " +
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


    @SneakyThrows
    public void addEntry(ScoreEntry entry) {
        connection.prepareStatement(String.format(
                "INSERT INTO ranked_scores (creatorUUID, sessionUUID, teamSize, lengthHeld, timeEnded)" +
                        "VALUES ('%s', '%s', '%s', %o, %o);",
                entry.getCreator().toString(),
                entry.getSessionUUID().toString(),
                entry.getTeamSize().name(),
                entry.getLengthHeld(),
                entry.getTimestamp()
        )).execute();


        entry.getPlayers().stream().filter(uuid -> !uuid.equals(entry.getCreator())).forEach(uuid -> {
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
}
