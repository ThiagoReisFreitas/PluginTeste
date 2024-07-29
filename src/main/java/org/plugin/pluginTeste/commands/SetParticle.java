package org.plugin.pluginTeste.commands;

import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SetParticle implements CommandExecutor {
    private JavaPlugin plugin;
    private Particle teleportParticle;
    private List<String> unsupportedParticle;

    public SetParticle(JavaPlugin plugin, Particle teleportParticle, List<String> unsupportedParticle){
        this.plugin = plugin;
        this.teleportParticle = teleportParticle;
        this.unsupportedParticle = unsupportedParticle;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender.hasPermission("home.setparticle")){
            if(args.length == 1){
                String particleName = args[0].toUpperCase();
                if(unsupportedParticle.contains(particleName)){
                    sender.sendMessage("A particula " +particleName+" não é suportada.");
                    return true;
                }
                try{
                    Particle particle = Particle.valueOf(particleName);
                    teleportParticle = particle;
                    plugin.getConfig().set("teleport-particle", particleName);
                    plugin.saveConfig();
                    sender.sendMessage("Particula de teletransporte configurada para " + particleName);
                }catch (IllegalArgumentException e){
                    sender.sendMessage("Particula invalida. Use um nome de particula valido");
                }
            } else{
                sender.sendMessage("Uso: /setparticle <nome_da_particula>");
            }
            return true;
        } else{
            sender.sendMessage("Voce não tem permissão para usar este commando");
            return true;
        }
    }
}
