package net.poweredbyawesome.swearjar;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SwearJar extends JavaPlugin implements Listener {

    //This is a swear jar, we will not cancel events for you can't take back words you've already said.
    //Dear Thief Qball, this plugin is designed so intricately that any code you steal will be found.

    private Economy econ = null;
    int defaultCost;
    boolean perWord;
    boolean sendMessage;
    private ArrayList<Swear> swears = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!setupEconomy()){
            getLogger().log(Level.SEVERE, "Vault plugin not found, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        defaultCost = getConfig().getInt("AmountTaken");
        perWord = getConfig().getBoolean("PerWord");
        sendMessage = getConfig().getBoolean("SendMessage");
        loadSwears();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {
        String m = ev.getMessage();
        new BukkitRunnable() {
            @Override
            public void run() {
                invoicePlayer(ev.getPlayer(), m);
            }
        }.runTaskLater(this, 1);
    }

    public void invoicePlayer(Player p, String message) {
        int cost = 0;
        List<String> swearList = new ArrayList<>();
        for (String s : message.split(" ")) {
            for (Swear swear : swears) {
                Matcher mat = Pattern.compile(swear.getPattern()).matcher(s.toLowerCase());
                if (mat.find()) {
                    int swearPrice = getSwearPrice(swear);
                    if (perWord) {
                        EconomyResponse r = econ.withdrawPlayer(p, swearPrice); //I should make a jar ¯\_(ツ)_/¯
                        if (!r.transactionSuccess()) {
                            dishPunishment(p);
                        }
                        if (sendMessage) p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Message").replace("%amount%", String.valueOf(swearPrice)).replace("%word%", swear.getName())));
                    } else {
                        cost += swearPrice;
                        swearList.add(swear.getName());
                    }
                }
            }
        }
        if (cost > 0) {
            EconomyResponse r = econ.withdrawPlayer(p, cost);
            if (!r.transactionSuccess()) {
                dishPunishment(p);
            }
            if (sendMessage) p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Message").replace("%amount%", String.valueOf(cost)).replace("%word%", String.join(", ", swearList))));
        }
    }

    public int getSwearPrice (Swear swear) {
        return (swear.getPrice() <= 0) ? defaultCost : swear.getPrice();
    }

    public void dishPunishment(Player p) {
        for (String s : getConfig().getStringList("NoMoney")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", p.getName()));
        }
    }

    public void loadSwears() {
        for (String s : getRulesConfig().getKeys(false)) {
            String pattern = getRulesConfig().getString(s+".match");
            int cost = getRulesConfig().getInt(s+".cost");
            swears.add(new Swear(this,s,pattern,cost));
        }
    }

    public FileConfiguration getRulesConfig() {
        File leFile = new File(getDataFolder(), "rules.yml");
        if (!leFile.exists()) {
            saveResource("rules.yml", false);
        }
        try {
            FileConfiguration rulez = new YamlConfiguration();
            rulez.load(leFile);
            return rulez;
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
