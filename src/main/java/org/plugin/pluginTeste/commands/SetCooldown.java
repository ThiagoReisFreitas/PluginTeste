package org.plugin.pluginTeste.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SetCooldown implements CommandExecutor {
    private int cooldownseconds;
    private JavaPlugin plugin;

    public SetCooldown(int cooldownseconds, JavaPlugin plugin){
        this.cooldownseconds = cooldownseconds;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (sender.hasPermission("home.setcooldown")){
            if(args.length == 1){
                try {
                    int newCooldown = Integer.parseInt(args[0]);
                    if(newCooldown < 0){
                        sender.sendMessage("O cooldown não pode ser negativo.");
                        return true;
                    }
                    cooldownseconds = newCooldown;
                    plugin.getConfig().set("cooldown-seconds", newCooldown);
                    plugin.saveConfig();
                    sender.sendMessage("Cooldown do comando /home definido para "+newCooldown+" segundos");
                }catch (NumberFormatException e){
                    sender.sendMessage("Por favor, insira um número válido de segundos");
                }
            }else{
                sender.sendMessage("Uso: /setcooldown <segundos>");
            }
            return true;
        }else {
            sender.sendMessage("Você não tem permissão para usar este commando.");
            return true;
        }
    }
}
