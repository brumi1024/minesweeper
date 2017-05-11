package hu.bme.minesweeper.datamodel;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private final static Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private final static String DATABASE_URL = "jdbc:sqlite:highscores.db";

    private static Dao<HighScores, Integer> highscoreDao;

    private static ConnectionSource connectionSource;

    /**
     * Initializes the database.
     */
    public static void initDatabase() {
        try {
            connectionSource = null;
            connectionSource = new JdbcConnectionSource(DATABASE_URL);
            setupDatabase(connectionSource);

            LOGGER.log(Level.FINE, "Database connection established");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not create database connection. {0}", e.toString());
        }
    }

    /**
     * Setup the database and DAOs, create the table if it does not exists.
     *
     * @param connectionSource ConnectionSource param
     * @throws Exception throws an error if the table creation failed
     */
    private static void setupDatabase(ConnectionSource connectionSource) throws Exception {

        highscoreDao = DaoManager.createDao(connectionSource, HighScores.class);
        TableUtils.createTableIfNotExists(connectionSource, HighScores.class);
    }

    /**
     * Write out the data to the database.
     *
     * @param newHighScore new highscore to write
     * @throws SQLException throws an exception if an SQL error occured
     */
    public static void writeOne(HighScores newHighScore) throws SQLException {
        highscoreDao.create(newHighScore);

        LOGGER.log(Level.FINE, "Write data to database successful.");
    }

    /**
     * Write out the data to the database.
     *
     * @throws SQLException throws an exception if an SQL error occured
     */
    public static void deleteLastOne(String difficulty) throws SQLException {
        DeleteBuilder<HighScores, Integer> deleteBuilder = highscoreDao.deleteBuilder();
        deleteBuilder.where().eq("time", readLastTime(difficulty));
        deleteBuilder.delete();

        LOGGER.log(Level.FINE, "Delete data from the database is successful.");
    }

    /**
     * Read the slowest recorded completion time based on difficulty.
     *
     * @return slowest time of the database
     * @throws SQLException throws an exception if an SQL error occurred
     */
    public static int readCount(String difficulty) throws SQLException {
        QueryBuilder<HighScores, Integer> qb = highscoreDao.queryBuilder();
        Where where = qb.where();
        where.eq("difficulty", difficulty);
        qb.setCountOf(true);
        PreparedQuery<HighScores> preparedQuery = qb.prepare();

        return (int) highscoreDao.countOf(preparedQuery);
    }

    /**
     * Read the slowest recorded completion time based on difficulty.
     *
     * @return slowest time of the database
     * @throws SQLException throws an exception if an SQL error occurred
     */
    public static int readLastTime(String difficulty) throws SQLException {
        LOGGER.log(Level.FINE, "Reading last value from the database.");

        String tableName = DatabaseTableConfig.extractTableName(HighScores.class);
        String selectQuery = "SELECT MAX(time) FROM " + tableName + " WHERE difficulty = '" + difficulty + "';";

        return (int) highscoreDao.queryRawValue(selectQuery);
    }

    /**
     * Read all the data from the database.
     *
     * @return an List<HighScores> that contains the rows.
     * @throws SQLException throws an exception if an SQL error occured
     */
    public static List<HighScores> readAll(String difficulty) throws SQLException {
        LOGGER.log(Level.FINE, "Reading the values from the database.");

        QueryBuilder<HighScores, Integer> qb = highscoreDao.queryBuilder();
        Where where = qb.where();
        where.eq("difficulty", difficulty);
        qb.orderBy("time", true);

        PreparedQuery<HighScores> preparedQuery = qb.prepare();

        return highscoreDao.query(preparedQuery);
    }

    /**
     * Checks if the connection is open to the database.
     *
     * @return true if it is open, false if not.
     */
    public static boolean isConnected() {
        LOGGER.log(Level.FINE, "Connection to the database: {0}.", connectionSource.isOpen(
                DatabaseTableConfig.extractTableName(HighScores.class)));

        return connectionSource.isOpen(DatabaseTableConfig.extractTableName(HighScores.class));
    }
}
