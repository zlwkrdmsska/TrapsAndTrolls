package io.github.yehan2002.Traps;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Command implements CommandExecutor {

    private Economy econ;
    TrapShop trapShop;

    Command(JavaPlugin plugin){
        setupEconomy(plugin);
        trapShop = new TrapShop(econ);
        trapShop.initializeItems();
    }

    private boolean setupEconomy(JavaPlugin plugin) {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0 | (args.length == 1 && args[0].equalsIgnoreCase("help"))){
                p.sendMessage(ChatColor.DARK_AQUA + "-----------                -----------");
                p.sendMessage(ChatColor.GOLD + "                Traps & Trolls            ");
                p.sendMessage(ChatColor.DARK_AQUA + "-----------                -----------");
                p.sendMessage("");
                p.sendMessage(ChatColor.DARK_GREEN + "Created by " + ChatColor.GREEN + "yj22k");
                p.sendMessage(ChatColor.GREEN + "Website : http://www.github.com/yehan2002");
                p.sendMessage("");
                p.sendMessage(ChatColor.GREEN + "Commands:");
                p.sendMessage("/trap shop");
                p.sendMessage("/trap help");
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("shop")){
                trapShop.openInventory(p);
            }
            return true;
        }
        return false;
    }
}
