
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;

public class YamlTest
{
    @Test
    public void rankTest()
    {
        InputStream input = getClass().getClassLoader().getResourceAsStream("en_US.yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(input));

        for (String key : config.getKeys(true))
        {
            System.out.println(key + " " + (config.isConfigurationSection(key)));
        }
    }
}
