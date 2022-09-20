package com.server.dddatabase.sql;

import com.server.dddatabase.DDdatabase;
import org.bukkit.entity.Player;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ExperienceGetter {

    private DDdatabase plugin;

    public ExperienceGetter(DDdatabase plugin) {
        this.plugin = plugin;
    }

    public void createTable() {
        PreparedStatement ps;
        try {
            ps = plugin.expSQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS levelsystem " +
                    "(NAME VARCHAR(100), " +
                    "UUID VARCHAR(100), " +
                    "EXPERIENCE INT(100), " +
                    "PRIMARY KEY (NAME))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayer(UUID uuid, String name) {
        try {
            if (!exists(uuid)) {
                PreparedStatement ps2 = plugin.expSQL.getConnection().prepareStatement("INSERT IGNORE INTO levelsystem (NAME,UUID,EXPERIENCE) VALUES (?,?,?)");
                ps2.setString(1, name);
                ps2.setString(2, uuid.toString());
                ps2.setInt(3, 0);
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getExperience(UUID uuid) {
        try {
            PreparedStatement ps = plugin.expSQL.getConnection().prepareStatement("SELECT EXPERIENCE FROM levelsystem WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            int experience = 0;
            if (results.next()) {
                experience = results.getInt("EXPERIENCE");
                return experience;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setExperience(UUID uuid, int amount) {
        try {
            PreparedStatement ps = plugin.expSQL.getConnection().prepareStatement("UPDATE levelsystem SET EXPERIENCE=? WHERE UUID=?");
            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUsername(UUID uuid) {
        try {
            PreparedStatement ps = plugin.expSQL.getConnection().prepareStatement("SELECT NAME FROM levelsystem WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            String name = "";
            if (results.next()) {
                name = results.getString("NAME");
                return name;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public HashMap<UUID, Integer> getMap() {
        try {
            PreparedStatement ps = plugin.expSQL.getConnection().prepareStatement("SELECT UUID FROM levelsystem");
            ResultSet results = ps.executeQuery();
            List<UUID> uuids = new ArrayList<>();
            while (results.next()) {
                String uuid = results.getString("UUID");
                uuids.add(UUID.fromString(uuid));
            }
            HashMap<UUID, Integer> map = new HashMap<>();
            for (UUID u : uuids) {
                map.put(u, getExperience(u));
            }
            return map;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean exists(UUID uuid) {
        try {
            PreparedStatement ps = plugin.expSQL.getConnection().prepareStatement("SELECT * FROM levelsystem WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
