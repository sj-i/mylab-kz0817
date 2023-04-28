/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package sqliteexp;
import java.sql.*;

public class App implements AutoCloseable {

    private final String dbFilePath;
    private Connection connection;
    private Statement statement;

    App(final String[] args) {
        if (args.length < 1) {
            System.err.println("Needs a database file path.");
            System.exit(1);
        }
        dbFilePath = args[0];
        System.out.println("DB: " + dbFilePath);

        initDB();
    }

    @Override
    public void close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (final Exception e) {
                showStackTraceAndExit(e);
            }
        }
    }

    static private void showStackTraceAndExit(final Exception e) {
        e.printStackTrace();
        System.exit(1);
    }

    private void initDB() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (final ClassNotFoundException e) {
            showStackTraceAndExit(e);
        }

        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFilePath);
            this.connection.setAutoCommit(false);
            this.statement = this.connection.createStatement();
            createTable();
            fillDB();
        } catch (final SQLException e) {
            showStackTraceAndExit(e);
        }
    }

    private void createTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS table1(" +
                           "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                           "  name TEXT UNIQUE" +
                           ")";
        this.statement.execute(sql);
    }

    private void fillDB() throws SQLException {
        final String[] names = {"dog", "cat", "curry", "hot dog"};
        for (final String name: names) {
            insertRecord(name);
        }
    }

    private void takeOne(final ResultSet rs) throws SQLException {
        if (!rs.next()) {
            System.out.println("All selected records have been taken.");
            return;
        }
        System.out.format("%d: %s\n", rs.getInt("id"), rs.getString("name"));
    }

    private void removeRecord(final String name) throws SQLException {
        final String selectStmt = "DELETE FROM table1 WHERE name='" + name + "'";
        try (final Statement stmt = this.connection.createStatement()) {
            stmt.execute(selectStmt);
            System.out.format("Deleted: %s\n", name);
        }
    }

    private void insertRecord(final String name) throws SQLException {
        final String insertStmt = "INSERT INTO table1(name) VALUES('" + name + "')" +
                                  "  ON CONFLICT(name) DO NOTHING";
        try (final Statement stmt = this.connection.createStatement()) {
            stmt.execute(insertStmt);
            System.out.format("Inserted: %s\n", name);
        }
    }

    private void mainAction() throws SQLException {
        final String selectStmt = "SELECT id,name FROM table1 ORDER BY id";
        final ResultSet rs = this.statement.executeQuery(selectStmt);
        takeOne(rs);
        takeOne(rs);
        removeRecord("curry");
        takeOne(rs);
        insertRecord("Bread");
        takeOne(rs);
        takeOne(rs);
    }

    public static void main(String[] args) {
        try (final App app = new App(args)) {
            app.mainAction();
        } catch (final Exception e) {
            showStackTraceAndExit(e);
        }
    }
}