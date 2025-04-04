package org.caique.mcgames.database;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.caique.mcgames.database.DatabaseManager;

public class PlayerData {
    private final DatabaseManager db;

    public PlayerData(DatabaseManager db) {
        this.db = db;
    }

    public void registerPlayer(Player player, String password) throws SQLException {
        String sql = "INSERT INTO players (uuid, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, password); // Em produção, use hash (ex.: BCrypt)
            stmt.executeUpdate();
        }
    }

    public boolean isRegistered(Player player) throws SQLException {
        String sql = "SELECT 1 FROM players WHERE uuid = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean checkPassword(Player player, String password) throws SQLException {
        String sql = "SELECT password FROM players WHERE uuid = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password").equals(password); // Em produção, compare hash
            }
            return false;
        }
    }

    public void setLoggedIn(Player player, boolean loggedIn) throws SQLException {
        String sql = "UPDATE players SET is_logged_in = ? WHERE uuid = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setBoolean(1, loggedIn);
            stmt.setString(2, player.getUniqueId().toString());
            stmt.executeUpdate();
        }
    }

    public boolean isLoggedIn(Player player) throws SQLException {
        String sql = "SELECT is_logged_in FROM players WHERE uuid = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getBoolean("is_logged_in");
        }
    }
}