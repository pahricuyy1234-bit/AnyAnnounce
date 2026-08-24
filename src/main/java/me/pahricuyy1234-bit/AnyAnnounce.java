package me.pahricuyy1234-bit.anyannounce;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * AnyAnnounce
 * Plugin sederhana untuk menampilkan pesan pengumuman di tengah atas layar
 * (menggunakan Boss Bar bawaan Minecraft) kepada seluruh pemain online.
 * Hanya bisa dijalankan oleh operator (op).
 */
public final class AnyAnnounce extends JavaPlugin implements CommandExecutor {

    // Berapa lama (dalam detik) boss bar pengumuman ditampilkan sebelum hilang otomatis.
    private static final long DURATION_SECONDS = 5L;

    @Override
    public void onEnable() {
        var announceCommand = getCommand("announce");
        if (announceCommand != null) {
            announceCommand.setExecutor(this);
        }
        getLogger().info("AnyAnnounce telah aktif.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("announce")) {
            return false;
        }

        // Hanya operator yang boleh menjalankan perintah ini.
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("Kamu tidak punya izin untuk menjalankan perintah ini.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Component.text("Gunakan: /announce <pesan>", NamedTextColor.YELLOW));
            return true;
        }

        String message = String.join(" ", args);
        broadcastAnnouncement(message);

        sender.sendMessage(Component.text("Pengumuman terkirim: ", NamedTextColor.GREEN)
                .append(Component.text(message, NamedTextColor.WHITE)));
        return true;
    }

    /**
     * Menampilkan pesan sebagai boss bar putih (tengah atas layar) ke semua
     * pemain yang sedang online, lalu menghapusnya otomatis setelah beberapa detik.
     */
    private void broadcastAnnouncement(String message) {
        BossBar bossBar = BossBar.bossBar(
                Component.text(message, NamedTextColor.WHITE),
                1.0f,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showBossBar(bossBar);
        }

        // Hapus boss bar secara otomatis setelah DURATION_SECONDS detik.
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.hideBossBar(bossBar);
            }
        }, DURATION_SECONDS * 20L); // 20 tick = 1 detik
    }
}
