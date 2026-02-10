package com.NguyenDevs.temporaryTorch.manager;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import com.NguyenDevs.temporaryTorch.utils.HexColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageManager {

    private final TemporaryTorch plugin;
    private File messageFile;
    private FileConfiguration messages;

    public MessageManager(TemporaryTorch plugin) {
        this.plugin = plugin;
    }

    public void loadMessages() {
        messageFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messageFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messageFile);

        updateMessages();
    }

    private void updateMessages() {
        boolean updated = false;

        FileConfiguration defaultMessages = YamlConfiguration.loadConfiguration(
                new InputStreamReader(plugin.getResource("messages.yml"), StandardCharsets.UTF_8)
        );

        for (String key : defaultMessages.getKeys(true)) {
            if (!messages.contains(key)) {
                messages.set(key, defaultMessages.get(key));
                updated = true;
                plugin.getLogger().info("Added missing message key: " + key);
            }
        }

        if (updated) {
            saveMessages();
        }
    }

    public void saveMessages() {
        try {
            messages.save(messageFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save messages.yml: " + e.getMessage());
        }
    }

    public void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(messageFile);
        updateMessages();
    }

    public String getMessage(String path) {
        String message = messages.getString(path, "&cMessage not found: " + path);
        return HexColorUtils.colorize(message);
    }

    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }

        return message;
    }

    /**
     * Get raw message without color processing
     */
    public String getRawMessage(String path) {
        return messages.getString(path, "Message not found: " + path);
    }

    /**
     * Strip all colors from a message
     */
    public String stripColors(String message) {
        return HexColorUtils.stripColors(message);
    }
}