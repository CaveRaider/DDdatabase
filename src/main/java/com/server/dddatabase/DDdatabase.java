package com.server.dddatabase;

import com.server.dddatabase.levelcalc.LevelCalculator;
import com.server.dddatabase.sql.ExperienceGetter;
import com.server.dddatabase.sql.ExperienceSQL;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class DDdatabase extends JavaPlugin implements Listener {

    public ExperienceSQL expSQL;
    public ExperienceGetter expGetter;

    public HashMap<UUID,Integer> experienceMap = new HashMap<>();
    public List<String> nameList = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.expSQL = new ExperienceSQL();
        this.expGetter = new ExperienceGetter(this);
        try {
            expSQL.connect();
        } catch (ClassNotFoundException | SQLException e) {
            this.getLogger().warning("Inventory database connection failed!");
        }
        if (expSQL.isConnected()) {

            //if connection is successful
            this.getLogger().info("Inventory database connection successful!");
            expGetter.createTable();
            this.getServer().getPluginManager().registerEvents(this, this);
            this.experienceMap = expGetter.getMap();
            for (UUID u : experienceMap.keySet()) {
                nameList.add(expGetter.getUsername(u));
            }
            expSQL.disconnect();
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            /*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */
            Bukkit.getPluginManager().registerEvents(this, this);
            new LevelCalculator(this).register();
        } else {
            /*
             * We inform about the fact that PlaceholderAPI isn't installed and then
             * disable this plugin to prevent issues.
             */
            getLogger().info("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.expSQL = new ExperienceSQL();
        this.expGetter = new ExperienceGetter(this);
        try {
            expSQL.connect();
        } catch (SQLException | ClassNotFoundException e) {
            this.getLogger().warning("Inventory database connection failed!");
        }
        if (expSQL.isConnected()) {

            //if connection successful
            this.getLogger().info("Inventory database connection successful!");
            expGetter.createTable();
            int c = 0;
            for (UUID u : experienceMap.keySet()) {
                expGetter.createPlayer(u, this.nameList.get(c));
                expGetter.setExperience(u, experienceMap.get(u));
                c++;
            }
            expSQL.disconnect();
        }
    }


    @EventHandler
    public void join(PlayerJoinEvent e) {
        //create player in expmap
        if (experienceMap.containsKey(e.getPlayer().getUniqueId())) return;
        experienceMap.put(e.getPlayer().getUniqueId(), 0);
        nameList.add(e.getPlayer().getName());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //make sure it's a player
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        //check for proper use of our command
        if (!label.equalsIgnoreCase("dd")) return false;
        if (args.length < 1) {
            player.sendMessage("Please use a valid command!");
            return false;
        }

        //get exp command
        if (args[0].equalsIgnoreCase("getexp")) {
            int exp = experienceMap.get(player.getUniqueId());
            player.sendMessage("You have " + Integer.toString(exp) + " experince points!");
            return true;
        }

        //make sure a valid number was entered for commands below
        if (args.length < 2) {
            player.sendMessage("Please use a valid command!");
            return false;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Enter a valid number!");
            return false;
        }
        if (amount < 0) {
            player.sendMessage("Enter a valid number!");
            return false;
        }

        //set exp command
        if (args[0].equalsIgnoreCase("setexp")) {
            experienceMap.put(player.getUniqueId(), amount);
            player.sendMessage("You now have " + Integer.toString(experienceMap.get(player.getUniqueId())) + " experience points!");
            return true;
        }

        //add exp command
        if (args[0].equalsIgnoreCase("addexp")) {
            experienceMap.put(player.getUniqueId(), amount + experienceMap.get(player.getUniqueId()));
            player.sendMessage("You now have " + Integer.toString(experienceMap.get(player.getUniqueId())) + " experience points!");
            return true;
        }

        //subtract exp command
        if (args[0].equalsIgnoreCase("subexp")) {
            int newExp = experienceMap.get(player.getUniqueId()) - amount;
            if (newExp < 0) newExp = 0;
            experienceMap.put(player.getUniqueId(), newExp);
            player.sendMessage("You now have " + Integer.toString(experienceMap.get(player.getUniqueId())) + " experience points!");
            return true;
        }
        return false;
    }

}
