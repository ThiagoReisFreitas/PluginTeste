package org.plugin.pluginTeste;

import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.plugin.pluginTeste.commands.*;
import org.plugin.pluginTeste.database.DatabaseManager;
import org.plugin.pluginTeste.uteis.Configuration;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public final class PluginTeste extends JavaPlugin implements Listener {

    public FileConfiguration config;
    public DatabaseManager dbManager;
    public int cooldownseconds;
    public Particle teleportParticle;
    public Configuration configuration;
    public JavaPlugin plugin;
    public List<String> unsupportedParticle = Arrays.asList(
            "ENTITY_EFFECT", "DUST", "ITEM", "BLOCK", "FALLING_DUST",
            "SCULK_CHARGE", "SHRIEK", "DUST_COLOR_TRANSITION", "VIBRATION",
            "DUST_PILLAR", "BLOCK_MARKER"
    );

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        this.reloadConfig();
        this.configuration = new Configuration(this, teleportParticle, cooldownseconds);

        WindProjectil windProjectil = new WindProjectil(this);
        //Events events = new Events(this);

        config = getConfig();
        getServer().getPluginManager().registerEvents(this, this);
        configuration.loadConfig();

        cooldownseconds = configuration.getCooldownseconds();
        teleportParticle = configuration.getTeleportParticle();

        cooldownseconds = getConfig().getInt("cooldown-seconds", 60);
        String particleName = getConfig().getString("teleport-particle", "EXPLOSION");
        try {
            teleportParticle = Particle.valueOf(particleName);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Particula invalida no config.yml. Usando particula padrão");
            teleportParticle = Particle.EXPLOSION;
        }

        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port", 3306);
        String database = getConfig().getString("database.name", "minecraft");
        String username = getConfig().getString("database.username", "mcuser");
        String password = getConfig().getString("database.password", "mcpass");

        dbManager = new DatabaseManager(host, port, database, username, password);

        try {
            dbManager.connect();
            dbManager.createTable();
        } catch (SQLException e) {
            getLogger().severe("Não foi possivel conectar ao banco de dados: " + e.getMessage());
            return;
        }

        Home home = new Home(dbManager, cooldownseconds, teleportParticle);
        SetHome setHome = new SetHome(dbManager);
        SetCooldown setCooldown = new SetCooldown(cooldownseconds, this);
        SetParticle setParticle = new SetParticle(this, teleportParticle, unsupportedParticle);


        this.getCommand("sethome").setExecutor(setHome);
        this.getCommand("setcooldown").setExecutor(setCooldown);
        this.getCommand("home").setExecutor(home);
        this.getCommand("setparticle").setExecutor(setParticle);
        this.getCommand("windcharge").setExecutor(windProjectil);
    }

    @EventHandler
    public void onWindChargeLaunch(ProjectileLaunchEvent event) {
        if(event.getEntity() instanceof WindCharge){
            WindCharge windCharge = (WindCharge) event.getEntity();

            float explosionPower = (float) config.getDouble("explosion-power", 1.0);
            float velocity = (float) config.getDouble("projectile-velocity", 1.0);
            String particleType = config.getString("particle-type", "FLAME");

            windCharge.setMetadata("explosion-power", new FixedMetadataValue(this, explosionPower));
            windCharge.setVelocity(windCharge.getVelocity().multiply(velocity));

            try{
                Particle particle = Particle.valueOf(particleType.toUpperCase());
                windCharge.getWorld().spawnParticle(particle, windCharge.getLocation(), 10);
            }catch (IllegalArgumentException e){
                getLogger().warning("Tipo de particula invalido");
            }
        }
        this.reloadConfig();
    }

    @EventHandler
    public void onWindChargeDamage(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof WindCharge){
            WindCharge windCharge = (WindCharge) event.getDamager();
            if(windCharge.hasMetadata("explosion-power")){
                float explosionPower = windCharge.getMetadata("explosion-power").getFirst().asFloat();
                double baseDamage = event.getDamage();
                double newDamage = baseDamage*explosionPower;
                event.setDamage(newDamage);
            }
            this.reloadConfig();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            dbManager.disconnect();
        } catch (SQLException e) {
            getLogger().severe("Erro ao desconectar do banco de dados");
        }
    }
}
