package org.plugin.pluginTeste.commands;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WindProjectil implements CommandExecutor {
    private JavaPlugin plugin;
    private FileConfiguration config;

    public WindProjectil(JavaPlugin plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Este commando so pode ser usado por jogadores");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("windcharge-configure")){
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando");
            return true;
        }

        if(args.length < 2){
            player.sendMessage("Uso: /windcharge <propriedade> <valor>");
            return true;
        }

        String property = args[0].toLowerCase();
        String value = args[1];

        switch (property) {
            case "explosion":
                try {
                    double explosionPower = Double.parseDouble(value);
                    config.set("explosion-power", explosionPower);
                    player.sendMessage("Força da explosão definida para "+ explosionPower);
                }catch (NumberFormatException e){
                    player.sendMessage("Valor invalido para força de explosão");
                }
                break;

            case "velocity":
                try {
                    double velocity = Double.parseDouble(value);
                    config.set("projectile-velocity", velocity);
                    player.sendMessage("Velocidade do projetil definida para "+velocity);
                } catch (IllegalArgumentException e){
                    player.sendMessage("Valor invalido para velocida");
                }
                break;
            case "particle":
                try{
                    Particle.valueOf(value.toUpperCase());
                    config.set("particle-type", value.toUpperCase());
                    player.sendMessage("Tipo de particula definido para "+ value.toLowerCase());
                }catch (IllegalArgumentException e){
                    player.sendMessage("Tipo de particula invalido");
                }
                break;
            default:
                player.sendMessage("Propriedade desconhecida. use explosion, velocity ou particle");
        }
        plugin.saveConfig();
        plugin.reloadConfig();
        return true;
    }
}
