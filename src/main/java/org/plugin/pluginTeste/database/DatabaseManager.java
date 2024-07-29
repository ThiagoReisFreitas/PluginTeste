package org.plugin.pluginTeste.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    private Connection connection;
    private String host, database, username, password;
    private int port;

    public DatabaseManager(String host, int port, String database, String username, String password){
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()){
            return;
        }
        String url = "jdbc:mysql://"+host+":"+port+"/"+database+"?allowPublicKeyRetrieval=true&createDatabaseIfNotExists=true";
        connection = DriverManager.getConnection(url, username, password);
    }

    public void disconnect() throws SQLException{
        if(connection != null && !connection.isClosed()){
            connection.close();
        }
    }

    public void createTable() throws SQLException{
        String sql = "CREATE TABLE IF NOT EXISTS homes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "uuid VARCHAR(36) NOT NULL," +
                "world VARCHAR(50) NOT NULL," +
                "x DOUBLE NOT NULL," +
                "y DOUBLE NOT NULL," +
                "z DOUBLE NOT NULL," +
                "yaw FLOAT NOT NULL," +
                "pitch FLOAT NOT NULL)";
        try(Statement stmt = connection.createStatement()){
            stmt.execute(sql);
        }
    }

    public void saveHome(UUID uuid, Location location) throws SQLException{
        String sql = "INSERT INTO homes (uuid, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE world=?, x=?, y=?, z=?, yaw=?, pitch=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, location.getWorld().getName());
            pstmt.setDouble(3, location.getX());
            pstmt.setDouble(4, location.getY());
            pstmt.setDouble(5, location.getZ());
            pstmt.setFloat(6, location.getYaw());
            pstmt.setFloat(7, location.getPitch());
            pstmt.setString(8, location.getWorld().getName());
            pstmt.setDouble(9, location.getX());
            pstmt.setDouble(10, location.getY());
            pstmt.setDouble(11, location.getZ());
            pstmt.setFloat(12, location.getYaw());
            pstmt.setFloat(13, location.getPitch());

            pstmt.executeUpdate();
        }
    }

    public Location getHome(UUID uuid) throws SQLException{
        String sql = "SELECT * FROM homes WHERE uuid = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, uuid.toString());

            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    String worldName = rs.getString("world");
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    float yaw = rs.getFloat("yaw");
                    float pitch = rs.getFloat("pitch");

                    World world = Bukkit.getWorld(worldName);
                    if(world != null){
                        return new Location(world, x, y, z, yaw, pitch);
                    }
                }
            }
        }
        return null;
    }
}
