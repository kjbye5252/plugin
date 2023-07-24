package org.finalplugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;

import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.Pin.Mode;
import org.firmata4j.firmata.FirmataDevice;

public class FinalPlugin extends JavaPlugin implements Listener{
    private static FinalPlugin instance;
    private IODevice device;
    private Player player;
    private int dirt = 0;
    private BukkitTask task = null;

    public FinalPlugin(){
    }

    public void onEnable(){
        instance = this;
        this.getServer().getPluginManager().registerEvents(this, this);
        this.device = new FirmataDevice("COM3");
        this.getCommand("vault").setExecutor(new VaultCommand());

        try{
            device.start();
            device.ensureInitializationIsDone();
            System.out.println("yes");
        } catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
        try {
            device.start();
            System.out.println("yes");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            device.ensureInitializationIsDone();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            device.getPin(7).setMode(Pin.Mode.INPUT);
            device.getPin(13).setMode(Pin.Mode.INPUT);
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        this.player = e.getPlayer();
        this.getLogger().info("got player");
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) throws InterruptedException {
        if (e.getEntity() instanceof Player) {
            this.shock();
        }

    }

    public void onDisable() {
        try {
            this.device.stop();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    static void turnOn(IODevice device) {
        try {
            device.getPin(7).setMode(Pin.Mode.OUTPUT);
            device.getPin(7).setValue(350);
            device.getPin(13).setMode(Pin.Mode.INPUT);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    static void turnOff(IODevice device) {
        try {
            device.getPin(7).setMode(Pin.Mode.INPUT);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void shock() {
        (new BukkitRunnable() {
            public void run() {
                int i = 0;
                ItemStack[] var2 = FinalPlugin.this.player.getInventory().getContents();
                int var3 = var2.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    ItemStack is = var2[var4];
                    if (is != null && is.getType() == Material.DIRT) {
                        i += is.getAmount();
                    }
                }

                FinalPlugin.this.dirt = i;
                FinalPlugin.this.getLogger().info("" + FinalPlugin.this.dirt + "");
                if (FinalPlugin.this.dirt % 2 == 0) {
                    FinalPlugin.turnOn(FinalPlugin.this.device);

                    try {
                        Thread.sleep(100L);
                        FinalPlugin.turnOff(FinalPlugin.this.device);
                    } catch (Exception var6) {
                        FinalPlugin.turnOff(FinalPlugin.this.device);
                    }

                    if (FinalPlugin.this.task != null && !FinalPlugin.this.task.isCancelled()) {
                        FinalPlugin.this.task.cancel();
                    }
                }

            }
        }).runTask(this);
    }
}
