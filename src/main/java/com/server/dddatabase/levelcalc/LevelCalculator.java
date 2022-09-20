package com.server.dddatabase.levelcalc;

import com.server.dddatabase.DDdatabase;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LevelCalculator extends PlaceholderExpansion {

    public DDdatabase plugin;

    public LevelCalculator(DDdatabase plugin) {
        this.plugin = plugin;
    }

    public static int getPlayerLevel(DDdatabase plugin, UUID uuid) {
        int experience = plugin.experienceMap.get(uuid);
        int counter = 1;
        while (experience >= experienceForLevel(counter)) {
            counter++;
        }
        return counter;
    }

    public static double experienceForLevel(int level) {
        return 10 * Math.pow(2.5, level - 1);
    }

    @Override
    public String getAuthor() {
        return "E";
    }

    @Override
    public String getIdentifier() {
        return "DDMCDatabase";
    }

    @Override
    public String getVersion() {
        return "69";
    }

    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("ddmc-level")){
            return String.valueOf(getPlayerLevel(plugin, player.getUniqueId()));
        }

        return ""; // Placeholder is unknown by the Expansion
    }

}
