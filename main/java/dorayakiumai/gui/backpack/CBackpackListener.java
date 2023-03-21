package dorayakiumai.gui.backpack;

import dorayakiumai.gui.DBConnection;
import dorayakiumai.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static dorayakiumai.gui.DBConnection.getMap1;
import static dorayakiumai.gui.GUI.inventoryMap;
public class CBackpackListener implements Listener {
    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        setItem(player);
    }
    @EventHandler
    public void PlayerOut(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (inventoryMap.get(player) != null) {
            Bukkit.getScheduler().runTaskAsynchronously(GUI.inst(), () -> new DBConnection().setbackpackdata(inventoryMap.get(player),player));
        }
    }
    @EventHandler
    public void Inventoryclose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof CBackpack) {
            Bukkit.getScheduler().runTaskAsynchronously(GUI.inst(), () -> new DBConnection().setbackpackdata(event.getInventory(), (Player) event.getPlayer()));
        }
    }

    public void setItem(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(GUI.inst(), () -> {

            Inventory inv = new DBConnection().getbackpackdata(player);
            for (int i = 0; i <= 26; i++) {
                if (inv.getItem(i) != null) {
                    ItemStack item = getMap1.get(i);
                    inv.setItem(i, item);
                }
            }
            inventoryMap.put(player, inv);
        });
    }
}
