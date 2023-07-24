package org.finalplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VaultCommand implements CommandExecutor {
    public VaultCommand() {
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            Inventory vault = Bukkit.createInventory(player, 9, "Vault");
            ItemStack item1 = new ItemStack(Material.CLAY_BALL, 2);
            vault.addItem(new ItemStack[]{item1});
            player.openInventory(vault);
        }

        return true;
    }
}
