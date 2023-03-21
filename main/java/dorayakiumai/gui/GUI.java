package dorayakiumai.gui;

import dorayakiumai.gui.backpack.CBackpack;
import dorayakiumai.gui.backpack.CBackpackListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public final class GUI extends JavaPlugin {
    private static GUI gui;
    public GUI (){gui = this;}
    public static GUI inst() {return gui;}

    public Connection connection;

    public static Map<Player, Inventory> inventoryMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        Objects.requireNonNull(getCommand("backpack")).setExecutor(new CBackpack());
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new CBackpackListener(), this);
        for (Player player: Bukkit.getOnlinePlayers()) {
            new CBackpackListener().setItem(player);
        }
        this.getLogger().info("バックパックのPLが読み込まれました");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
