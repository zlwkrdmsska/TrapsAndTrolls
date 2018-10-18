package io.github.yehan2002.Traps;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Command implements CommandExecutor {

    private Economy econ;
    TrapShop trapShop;
    public static ArrayList<Player> excluded = new ArrayList<>();

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

                p.sendMessage(ChatColor.DARK_GREEN + "Created by " + ChatColor.GREEN + "yj22k (http://bit.do/eySHt)");
                p.sendMessage("");
                p.sendMessage(ChatColor.GREEN + "Commands:");
                p.sendMessage(ChatColor.GOLD + "/trap shop -"+ ChatColor.GREEN + " Open player shop.");
                p.sendMessage(ChatColor.GOLD + "/trap god -"+ ChatColor.GREEN + " Excludes the player from traps.");
                p.sendMessage(ChatColor.GOLD + "/trap help -" + ChatColor.GREEN + " Display this message.");
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("shop")){
                trapShop.openInventory(p);

            } else if (args.length == 1 && args[0].equalsIgnoreCase("god")){
                if (p.hasPermission("trapAandTrolls.exempt")) {
                    excluded.add(p);
                } else {
                    p.sendMessage(ChatColor.DARK_RED + "You do not have permission to do this");
                }
            }
            return true;
        }
        return false;
    }
}
