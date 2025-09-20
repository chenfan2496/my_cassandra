package org.miniCassandra.config;

import org.junit.Test;

public class YamlConfigurationLoaderTest {
    @Test
    public void getLoaderUrl(){
        YamlConfigurationLoader loader = new YamlConfigurationLoader();
        Config conf =  loader.loadConfig();
        System.out.println(conf.num_tokens);
    }
}
