package org.plugin.pluginTeste.uteis;

import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {
    private JavaPlugin plugin;
    private Particle teleportParticle;
    private int cooldownseconds;

    public Configuration(JavaPlugin plugin, Particle teleportParticle, int cooldownseconds){
        this.plugin =  plugin;
        this.teleportParticle = teleportParticle;
        this.cooldownseconds = cooldownseconds;
    }

    public void loadConfig(){
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        cooldownseconds = config.getInt("cooldown-seconds", 60);
        String particleName = config.getString("teleport-particle", "EXPLOSION").toUpperCase();
        try {
            teleportParticle = Particle.valueOf(particleName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Configuração do tipo de partícula é inválida. Usando padrão EXPLOSION_NORMAL.");
            teleportParticle = Particle.EXPLOSION;
        }
    }

    public int getCooldownseconds() {
        return cooldownseconds;
    }

    public Particle getTeleportParticle() {
        return teleportParticle;
    }
}
