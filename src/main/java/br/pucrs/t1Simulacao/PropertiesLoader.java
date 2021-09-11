package br.pucrs.t1Simulacao;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class PropertiesLoader {

    public static Map<String, Object> loadProperties(String resourceFileName) {

        final InputStream inputStream = PropertiesLoader.class
                .getClassLoader()
                .getResourceAsStream(resourceFileName);

        final Map<String, Object> data = new Yaml().load(inputStream);

        return data;
    }
}