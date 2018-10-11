package io.github.yehan2002.Traps;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


import java.util.logging.Logger;


@SuppressWarnings("unused")
public class Main extends JavaPlugin{


    @Override
    public void onEnable() {

        if (!this.checkVault()){
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.registerDefault();
        Command cmd = new Command(this);
        this.getServer().getPluginManager().registerEvents(cmd.trapShop, this);
        this.getServer().getPluginManager().registerEvents(new TrapListener(this),  this);
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getCommand("trap").setExecutor(cmd);

    }

    private boolean checkVault(){
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        if (rsp.getProvider() == null){
            return false;
        }
        return true;
    }

    private void registerDefault(){
        TrapManager.registerTrap("Fire Trap", "Sets the enemy on Fire", 1000);
        TrapManager.registerTrap("Fake Diamond Troll", "Spawns a diamond that can't be picked up.", 1000);
        TrapManager.registerTrap("Poison Trap", "Poisons the player", 1500);

        if (PotionEffectType.getByName("GLOWING") != null) {
            TrapManager.registerTrap("Glow Trap", "Makes the Player Glow like a beacon", 1500);
            TrapManager.registerTrap("Herobrine Troll", "A Herobrine Jump-scare.", 2000);
        } else {
            this.getLogger().info("Disabled Glow Trap, Herobrine Troll due to old version of Spigot.");
        }

        TrapManager.registerTrap("Creative Mode Troll", "Fake Creative mode.", 2000);
        TrapManager.registerTrap("Fake Op troll", "Fake Op", 2000);
        TrapManager.registerTrap("Launch Trap", "Sends the player flying!!", 2500);
        TrapManager.registerTrap("TNT Trap", "Summons Primed TNT", 5000);
        TrapManager.registerTrap("Lightning Trap", "Summons a Lightning bolt", 7500);
        TrapManager.registerTrap("Thief Trap", "Steals the item the player is holding", 10000);
        TrapManager.registerTrap("Lava Trap", "Slowly sends the player into lava.", 20000);
        TrapManager.registerTrap("Cage Trap", "Traps the player in a cage", 25000);

    }

    @Override
    public void onDisable() {

        Logger.getGlobal().info("Disabled Successfully.");

    }
}
