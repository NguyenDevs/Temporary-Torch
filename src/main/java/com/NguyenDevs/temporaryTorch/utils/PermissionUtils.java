package com.NguyenDevs.temporaryTorch.utils;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PermissionUtils {

    private static final String BYPASS_PERMISSION = "temporarytorch.bypass";
    private static final String OVERRIDE_PERMISSION_PREFIX = "temporarytorch.override.";
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([smhd])");

    public static boolean hasBypassPermission(Player player) {
        return player.hasPermission(BYPASS_PERMISSION);
    }

    public static long getTorchDuration(Player player, TemporaryTorch plugin) {
        if (hasBypassPermission(player)) {
            return -1;
        }
        Long overrideDuration = getOverrideDuration(player);
        if (overrideDuration != null) {
            return overrideDuration;
        }
        return plugin.getConfigManager().getDefaultDuration();
    }

    private static Long getOverrideDuration(Player player) {
        for (org.bukkit.permissions.PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            if (permission.getPermission().startsWith(OVERRIDE_PERMISSION_PREFIX)) {
                String durationString = permission.getPermission().substring(OVERRIDE_PERMISSION_PREFIX.length());
                Long duration = parseDuration(durationString);

                if (duration != null) {
                    return duration;
                }
            }
        }

        return null;
    }

    /**
     * Parse duration string like "1h", "30m", "2d", "45s"
     * Returns duration in seconds
     */
    private static Long parseDuration(String durationString) {
        Matcher matcher = DURATION_PATTERN.matcher(durationString.toLowerCase());

        if (matcher.matches()) {
            try {
                long value = Long.parseLong(matcher.group(1));
                String unit = matcher.group(2);

                switch (unit) {
                    case "s": // seconds
                        return value;
                    case "m": // minutes
                        return value * 60;
                    case "h": // hours
                        return value * 3600;
                    case "d": // days
                        return value * 86400;
                    default:
                        return null;
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    public static boolean hasAdminPermission(Player player) {
        return player.hasPermission("temporarytorch.admin");
    }
}