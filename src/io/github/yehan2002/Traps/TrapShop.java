package io.github.yehan2002.Traps;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class TrapShop implements Listener {

    private Economy econ;
    private final Inventory inv;


    TrapShop(Economy econ){
        this.econ = econ;
        int num = (int) (Math.ceil(TrapManager.trapList.size() / 9F) * 9);
        inv = Bukkit.createInventory(null, num, ChatColor.GREEN + "Trap Shop");
    }

    void initializeItems() {
        for (String trap: TrapManager.trapList) {
            String desc = TrapManager.trapDescription.get(trap);
            Integer price = TrapManager.trapPrices.getOrDefault(trap, 1000);
            inv.addItem(createGuiItem(trap, desc , price));

        }
    }

    private ItemStack createGuiItem(String name, String desc, Integer price) {
        ItemStack i = new ItemStack(Material.STRING, 1);
        ItemMeta iMeta = i.getItemMeta();
        iMeta.setDisplayName(name);
        iMeta.setLore(new ArrayList<>(Arrays.asList(desc, ChatColor.GREEN + "Price: $" + price)));
        i.setItemMeta(iMeta);
        return i;
    }

    void openInventory(Player p) {
        p.openInventory(inv);
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        String invName = e.getInventory().getName();
        if (!invName.equals(inv.getName())) {
            return;
        }

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null) {
            return;
        }

        if (!clickedItem.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();

        if (!meta.hasDisplayName()) {
            return;
        }
        if (econ.has(p, TrapManager.trapPrices.get(meta.getDisplayName()))) {
            econ.withdrawPlayer(p, TrapManager.trapPrices.get(meta.getDisplayName()));
            p.getInventory().addItem(this.newTrap(meta.getDisplayName()));
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough money to buy this item.");
        }

    }

    private ItemStack newTrap(String name) {
        ItemStack drop = new ItemStack(Material.STRING);
        ItemMeta meta = drop.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(new ArrayList<>(Arrays.asList(TrapManager.trapDescription.get(name).split("\n"))));
        drop.setItemMeta(meta);
        return drop;
    }
}

