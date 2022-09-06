package com.server.dddatabase;

import com.server.dddatabase.sql.ExperienceGetter;
import com.server.dddatabase.sql.ExperienceSQL;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public final class DDdatabase extends JavaPlugin implements Listener {

    public ExperienceSQL expSQL;
    public ExperienceGetter expGetter;

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
            this.getLogger().info("Inventory database connection successful!");
            expGetter.createTable();
            this.getServer().getPluginManager().registerEvents(this, this);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        expSQL.disconnect();

    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        expGetter.createPlayer(e.getPlayer());
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
            int exp = expGetter.getExperience(player.getUniqueId());
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
            expGetter.setExperience(player.getUniqueId(), amount);
            player.sendMessage("You now have " + Integer.toString(expGetter.getExperience(player.getUniqueId())) + " experience points!");
            return true;
        }

        //add exp command
        if (args[0].equalsIgnoreCase("addexp")) {
            expGetter.addExperience(player.getUniqueId(), amount);
            player.sendMessage("You now have " + Integer.toString(expGetter.getExperience(player.getUniqueId())) + " experience points!");
            return true;
        }

        //subtract exp command
        if (args[0].equalsIgnoreCase("subexp")) {
            expGetter.subtractExperience(player.getUniqueId(), amount);
            player.sendMessage("You now have " + Integer.toString(expGetter.getExperience(player.getUniqueId())) + " experience points!");
            return true;
        }

        return false;
    }

}
