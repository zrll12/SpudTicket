package fun.spud.zrll.util;

import fun.spud.zrll.HelloMinecraft;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public class MySql {
    static private String Ip, Username, Password, DBName;
    static private int Port;

    /**
     * Update the information of the MySql connection.(Will automatically save to file and initialize the tables)
     * @param username The username of MySql connection
     * @param password The password of MySql connection
     */
    public static void UpdateInformation(String username, String password){
        Ip = HelloMinecraft.config.getString("MySql.ip");
        Port = HelloMinecraft.config.getInt("MySql.port");
        Username = username;
        Password = password;
        DBName = HelloMinecraft.config.getString("MySql.dbname");
        HelloMinecraft.instance.getConfig().set("MySql.ip", HelloMinecraft.config.getString("MySql.ip", "127.0.0.1"));
        HelloMinecraft.instance.getConfig().set("MySql.port", HelloMinecraft.config.getInt("MySql.port", 3306));
        HelloMinecraft.instance.getConfig().set("MySql.username", HelloMinecraft.config.getString("MySql.username", "user"));
        HelloMinecraft.instance.getConfig().set("MySql.password", HelloMinecraft.config.getString("MySql.password", "0000"));
        HelloMinecraft.instance.getConfig().set("MySql.dbname", HelloMinecraft.config.getString("MySql.dbname", "spudticket"));
        HelloMinecraft.instance.saveConfig();
        InitTable();
    }

    /**
     * Get the connection to the MySql(as username and password are private).
     * @return The connection
     * @throws ClassNotFoundException Could not find the driver class
     * @throws SQLException MySql returns with en error
     */
    public static Connection getSQLConnection() throws ClassNotFoundException, SQLException {
        final String JDBC_DRIVER = HelloMinecraft.config.getString("MySql.jdbcname", "com.mysql.jdbc.Driver");
        final String DB_URL = "jdbc:mysql://" + Ip + ":" + Port + "/" + DBName + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        Connection connection;
        Class.forName(JDBC_DRIVER);
        connection = DriverManager.getConnection(DB_URL, Username, Password);
        return connection;
    }

    /**
     * Initialize all the tables which used in this plugin.
     */
    public static void InitTable(){
        new BukkitRunnable(){
            @Override
            public void run() {
                Statement statement;
                Connection connection;
                try {
                    connection = getSQLConnection();
                    statement = connection.createStatement();
                    statement.execute("CREATE TABLE IF NOT EXISTS `ticket`  (\n" +
                            "  `serial` INTEGER AUTO_INCREMENT,\n" +
                            "  `tid` TEXT NOT NULL,\n" +
                            "  `money` double NOT NULL,\n" +
                            "  PRIMARY KEY (`serial`)\n" +
                            ");");
                    statement.execute("CREATE TABLE IF NOT EXISTS `deals`  (\n" +
                            "  `serial` INTEGER AUTO_INCREMENT,\n" +
                            "  `time` BIGINT NOT NULL,\n" +
                            "  `tid` TEXT NOT NULL,\n" +
                            "  `action` TEXT NOT NULL,\n" +
                            "  `station` TEXT NOT NULL,\n" +
                            "  `money` double NULL,\n" +
                            "  PRIMARY KEY (`serial`)\n" +
                            ");");
                    statement.close();
                    connection.close();
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(HelloMinecraft.instance);
    }
}
