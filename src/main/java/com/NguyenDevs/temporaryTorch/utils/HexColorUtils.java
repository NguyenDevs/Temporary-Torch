package com.NguyenDevs.temporaryTorch.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for handling hex colors and gradients in messages
 * Supports:
 * - Hex colors: <#RRGGBB>text</#RRGGBB>
 * - Gradients: <gradient:#START:#END>text</gradient>
 *
 * Requires Minecraft 1.16+ for hex color support
 */
public class HexColorUtils {

    // Pattern for hex color: <#RRGGBB>text</#RRGGBB>
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>(.*?)</#\\1>");

    // Pattern for gradient: <gradient:#START:#END>text</gradient>
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:#([A-Fa-f0-9]{6}):#([A-Fa-f0-9]{6})>(.*?)</gradient>");

    // Check if server supports hex colors (1.16+)
    private static final boolean HEX_SUPPORTED = isHexSupported();

    /**
     * Process a string with hex colors and gradients
     * Also supports legacy color codes (&a, &b, etc.)
     */
    public static String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        message = processGradients(message);
        message = processHexColors(message);
        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }

    /**
     * Process hex color tags: <#RRGGBB>text</#RRGGBB>
     */
    private static String processHexColors(String message) {
        if (!HEX_SUPPORTED) {
            return message.replaceAll("<#[A-Fa-f0-9]{6}>(.*?)</#[A-Fa-f0-9]{6}>", "$1");
        }

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            String text = matcher.group(2);

            String colored = applyHexColor(hexCode, text);
            matcher.appendReplacement(result, Matcher.quoteReplacement(colored));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Process gradient tags: <gradient:#START:#END>text</gradient>
     */
    private static String processGradients(String message) {
        if (!HEX_SUPPORTED) {
            return message.replaceAll("<gradient:#[A-Fa-f0-9]{6}:#[A-Fa-f0-9]{6}>(.*?)</gradient>", "$1");
        }

        Matcher matcher = GRADIENT_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String startHex = matcher.group(1);
            String endHex = matcher.group(2);
            String text = matcher.group(3);

            String gradient = applyGradient(startHex, endHex, text);
            matcher.appendReplacement(result, Matcher.quoteReplacement(gradient));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Apply hex color to text
     */
    private static String applyHexColor(String hexCode, String text) {
        ChatColor color = ChatColor.of("#" + hexCode);
        return color + text;
    }

    /**
     * Apply gradient from start color to end color across text
     */
    private static String applyGradient(String startHex, String endHex, String text) {
        // Remove existing color codes from text
        String cleanText = ChatColor.stripColor(text);

        if (cleanText.length() == 0) {
            return text;
        }

        Color startColor = hexToColor(startHex);
        Color endColor = hexToColor(endHex);

        StringBuilder result = new StringBuilder();
        int length = cleanText.length();

        for (int i = 0; i < length; i++) {
            char character = cleanText.charAt(i);
            float ratio = (float) i / (float) (length - 1);
            Color interpolated = interpolateColor(startColor, endColor, ratio);
            ChatColor color = ChatColor.of(interpolated);
            result.append(color).append(character);
        }

        return result.toString();
    }

    /**
     * Convert hex string to Color object
     */
    private static Color hexToColor(String hex) {
        return new Color(
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16)
        );
    }

    /**
     * Interpolate between two colors
     */
    private static Color interpolateColor(Color start, Color end, float ratio) {
        ratio = Math.max(0f, Math.min(1f, ratio));

        int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
        int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
        int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

        return new Color(red, green, blue);
    }

    /**
     * Check if server supports hex colors (Minecraft 1.16+)
     */
    private static boolean isHexSupported() {
        try {
            ChatColor.of("#FFFFFF");
            return true;
        } catch (NoSuchMethodError | Exception e) {
            return false;
        }
    }

    /**
     * Strip all color codes including hex colors and gradients
     */
    public static String stripColors(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        message = message.replaceAll("<#[A-Fa-f0-9]{6}>(.*?)</#[A-Fa-f0-9]{6}>", "$1");

        message = message.replaceAll("<gradient:#[A-Fa-f0-9]{6}:#[A-Fa-f0-9]{6}>(.*?)</gradient>", "$1");

        message = ChatColor.stripColor(message);

        return message;
    }

    /**
     * Get rainbow gradient colors
     */
    public static String rainbow(String text) {
        List<String> rainbowColors = new ArrayList<>();
        rainbowColors.add("FF0000"); // Red
        rainbowColors.add("FF7F00"); // Orange
        rainbowColors.add("FFFF00"); // Yellow
        rainbowColors.add("00FF00"); // Green
        rainbowColors.add("0000FF"); // Blue
        rainbowColors.add("4B0082"); // Indigo
        rainbowColors.add("9400D3"); // Violet

        return applyMultiGradient(rainbowColors, text);
    }

    /**
     * Apply gradient across multiple colors
     */
    private static String applyMultiGradient(List<String> hexColors, String text) {
        if (!HEX_SUPPORTED || hexColors.size() < 2) {
            return text;
        }

        String cleanText = ChatColor.stripColor(text);
        if (cleanText.length() == 0) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        int length = cleanText.length();
        int colorCount = hexColors.size();

        for (int i = 0; i < length; i++) {
            char character = cleanText.charAt(i);
            float position = (float) i / (float) (length - 1);
            float scaledPosition = position * (colorCount - 1);

            int colorIndex = (int) Math.floor(scaledPosition);
            float localRatio = scaledPosition - colorIndex;
            if (colorIndex >= colorCount - 1) {
                colorIndex = colorCount - 2;
                localRatio = 1.0f;
            }

            Color startColor = hexToColor(hexColors.get(colorIndex));
            Color endColor = hexToColor(hexColors.get(colorIndex + 1));
            Color interpolated = interpolateColor(startColor, endColor, localRatio);

            ChatColor color = ChatColor.of(interpolated);
            result.append(color).append(character);
        }

        return result.toString();
    }

    /**
     * Check if hex colors are supported on this server
     */
    public static boolean isHexColorSupported() {
        return HEX_SUPPORTED;
    }

    /**
     * Get server version for debugging
     */
    public static String getServerVersion() {
        return Bukkit.getVersion();
    }
}