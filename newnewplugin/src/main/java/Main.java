import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;

import java.io.IOException;
import java.util.logging.Logger;

public class Main extends JavaPlugin implements Listener{

    private static Main instance;
    private IODevice device;
    private Player player;
    private int dirt = 0;

    public void onEnable(){
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        device = new FirmataDevice("COM4");

        getCommand("vault").setExecutor(new VaultCommand());

        try{
            device.start();
            device.ensureInitializationIsDone();
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e){
        player = (Player) e.getPlayer();
        getLogger().info("got player");
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) throws InterruptedException {
        if(e.getEntity() instanceof Player){
            shock();
        }
    }

    @Override
    public void onDisable() {
        try{
            device.stop();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    static void turnOn(IODevice device){
        try{
            device.getPin(13).setMode(Pin.Mode.OUTPUT);
            device.getPin(13).setValue(1);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    static void turnOff(IODevice device){
        try{
            device.getPin(13).setMode(Pin.Mode.INPUT);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private BukkitTask task = null;

    public void shock(){
        new BukkitRunnable(){
            @Override
            public void run() {
                int i = 0;
                for (ItemStack is : player.getInventory().getContents()) {
                    if (is != null && is.getType() == Material.DIRT) {
                        i = i + is.getAmount();
                    }
                }
                dirt = i;
                getLogger().info(""+dirt+"");
                if(dirt % 2 == 0){
                    turnOn(device);

                    try{
                        Thread.sleep(100);
                        turnOff(device);
                    } catch (Exception e) {
                        turnOff(device);
                    }

                    if(task != null && !task.isCancelled()){
                        task.cancel();
                    }
                }
            }
        }.runTask(this);
    }
}
