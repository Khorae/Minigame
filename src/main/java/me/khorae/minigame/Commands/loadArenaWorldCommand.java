package me.khorae.minigame.Commands;

import me.khorae.minigame.Minigame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class loadArenaWorldCommand implements CommandExecutor {

    private final Minigame plugin;

    public loadArenaWorldCommand(Minigame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.loadOrCreateArenaWorld();  // Llamar al método para cargar el mundo
        sender.sendMessage("Intentando cargar el mundo 'Arena'. Revisa los logs para más detalles.");
        return true;
    }
}

