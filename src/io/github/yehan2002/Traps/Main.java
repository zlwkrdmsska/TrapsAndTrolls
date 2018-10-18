package io.github.yehan2002.Traps;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
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


        Command cmd = new Command(this);
        this.getServer().getPluginManager().registerEvents(cmd.trapShop, this);
        this.getServer().getPluginManager().registerEvents(new TrapListener(this),  this);
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getCommand("trap").setExecutor(cmd);

        System.out.println("Enabled Successfully!!");

    }

    private boolean checkVault(){
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        return rsp.getProvider() != null;
    }



    @Override
    public void onDisable() {
        Logger.getGlobal().info("Disabled Successfully.");
    }
}
