package org.plugin.pluginTeste.commands;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.plugin.pluginTeste.database.DatabaseManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;

public class Home implements CommandExecutor {
    private final DatabaseManager dbManager;
    private int cooldownseconds;
    private Map<UUID, Long> lastUsed = new HashMap<>();
    private Particle teleportParticle;

    public Home(DatabaseManager dbManager, int cooldownseconds, Particle teleportParticle){
        this.dbManager = dbManager;
        this.cooldownseconds = cooldownseconds;
        this.teleportParticle = teleportParticle;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player){
            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();
            try{
                Location home = dbManager.getHome(playerId);
                if(home != null){
                    long currentTime = System.currentTimeMillis();
                    long lastUseTime = lastUsed.getOrDefault(playerId, 0L);

                    if(currentTime - lastUseTime >= cooldownseconds *1000){
                        player.getWorld().spawnParticle(teleportParticle, player.getLocation(), 50, 5, 0, 5);
                        player.teleport(home);
                        player.getWorld().spawnParticle(teleportParticle, player.getLocation(), 50, 5, 0, 5);
                        lastUsed.put(playerId, currentTime);
                        player.sendMessage("Você foi teletransportado para sua casa!");
                    } else {
                        long timeLeft = cooldownseconds - (currentTime - lastUseTime) / 1000;
                        player.sendMessage("Você precisa esperar mais "+timeLeft+" segundos antes de usar o comando novamente.");
                    }
                }else{
                    player.sendMessage("Você ainda não definiu uma casa. Use /sethome para marcar sua localização atual como casa.");
                }
            }catch (SQLException e){
                player.sendMessage("Erro ao recuperar a localização da casa.");
                getLogger().severe("Erro ao recuperar home: "+e.getMessage());
            }
            return true;
        }
        return false;
    }
}
