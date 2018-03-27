package me.itsmas.network.server.lang;

import me.itsmas.network.api.UtilString;
import me.itsmas.network.server.Core;
import me.itsmas.network.server.module.Module;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Lang extends Module
{
    public Lang(Core core)
    {
        super(core, "Lang");
    }

    @Override
    public void onEnable()
    {
        locales = new HashMap<>();

        // FIXME: 13/09/2017 Use YAML to full potential

        registerLocale(DEFAULT_LOCALE);
    }

    /**
     * The default locale for sending messages
     */
    public static String DEFAULT_LOCALE = "en_US";

    /**
     * Fetches a formatted message for a certain local
     * If the specified locale is not available the default locale is used
     *
     * @see #DEFAULT_LOCALE
     * @param locale The locale to fetch the message for
     * @param message The ID of the message to fetch
     * @param format Optional placeholder values
     * @return The message in the requested locale (if available)
     */
    public String getMessage(String locale, String message, Object... format)
    {
        LangData data = locales.getOrDefault(locale, locales.get(DEFAULT_LOCALE));

        String langMessage = data.getMessage(message);

        if (langMessage == null)
        {
            return message;
        }

        return String.format(langMessage, format);
    }

    /**
     * Whether the server supports a certain locale
     *
     * @param locale The locale to check
     * @return Whether the locale is supported
     */
    public boolean isSupportedLocale(String locale)
    {
        for (String supported : locales.keySet())
        {
            if (locale.equalsIgnoreCase(supported))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Fetches the supported locales as a string
     *
     * @return The supported locales
     */
    public String getSupportedLocales()
    {
        return locales.keySet().stream().collect(Collectors.joining(", "));
    }

    /**
     * Map linking locale names to {@link LangData} instances
     */
    private Map<String, LangData> locales;

    /**
     * Registers a locale
     *
     * @param locale The locale to register
     */
    private void registerLocale(String locale)
    {
        FileConfiguration config = getLocaleConfig(locale);

        if (config == null)
        {
            logFatal("Unable to fetch FileConfiguration instance for locale " + locale);
            return;
        }

        LangData data = new LangData();
        locales.put(locale, data);

        for (String messageId : config.getKeys(true))
        {
            if (!config.isConfigurationSection(messageId))
            {
                String message = UtilString.colour(config.getString(messageId));

                data.addMessage(messageId.toLowerCase(), message);
            }
        }
    }

    /**
     * Fetches the {@link FileConfiguration} instance of a locale file
     *
     * @param locale The locale of the FileConfiguration to fetch
     * @return The FileConfiguration for the locale
     */
    private FileConfiguration getLocaleConfig(String locale)
    {
        InputStream input = getClass().getClassLoader().getResourceAsStream(locale + ".yml");

        if (input == null)
        {
            return null;
        }

        return YamlConfiguration.loadConfiguration(new InputStreamReader(input));
    }
}
