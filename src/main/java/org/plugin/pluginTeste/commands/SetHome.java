package org.plugin.pluginTeste.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.plugin.pluginTeste.database.DatabaseManager;

import java.sql.SQLException;

import static org.bukkit.Bukkit.getLogger;

public class SetHome implements CommandExecutor {
    private final DatabaseManager dbManager;

    public SetHome(DatabaseManager dbManager){
        this.dbManager = dbManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player){
            Player player = (Player) sender;
            try{
                dbManager.saveHome(player.getUniqueId(), player.getLocation());
                player.sendMessage("Casa marcada na sua localização atual!");
            }catch (SQLException e){
                player.sendMessage("Erro ao salvar a localização da casa.");
                getLogger().severe("Erro ao salvar home: " + e.getMessage());
            }
            return true;
        }
        return false;
    }
}
