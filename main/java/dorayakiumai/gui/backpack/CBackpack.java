package dorayakiumai.gui.backpack;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static dorayakiumai.gui.GUI.inventoryMap;
public class CBackpack implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        try {
            if (commandSender instanceof Player player) {
                player.sendMessage(ChatColor.AQUA + "そうこをひらきました");
                player.openInventory(inventoryMap.get(player)); //playerのinventoryMapっていうインベントリを開く
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }
}
