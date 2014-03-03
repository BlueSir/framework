package com.sohu.smc.core.config;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.sohu.smc.common.util.SystemParam;
import com.sohu.smc.core.json.Json;
import com.sohu.smc.core.validation.Validator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "CanBeFinal"})
public class ConfigurationFactory<T> {

    private static final String PROPERTY_PREFIX = "dw.";


    public static <T> ConfigurationFactory<T> forClass(String service_path, Class<T> klass, Validator validator, Iterable<Module> modules) {
        return new ConfigurationFactory<T>(service_path, klass, validator, modules);
    }

    public static <T> ConfigurationFactory<T> forClass(Class<T> klass, Validator validator, Iterable<Module> modules) {
        return new ConfigurationFactory<T>(null, klass, validator, modules);
    }

    public static <T> ConfigurationFactory<T> forClass(Class<T> klass, Validator validator) {
        return new ConfigurationFactory<T>(null, klass, validator, ImmutableList.<Module>of());
    }

    private final Class<T> klass;
    private final Json json;
    private final Validator validator;
    //   private String servicePath = "conf/service.yaml";

    private ConfigurationFactory(String servicePath, Class<T> klass, Validator validator, Iterable<Module> modules) {
        //      if(servicePath != null) this.servicePath = servicePath;
        this.klass = klass;
        this.json = new Json();
        json.enable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        for (Module module : modules) {
            json.registerModule(module);
        }
        this.validator = validator;
    }

    public T build(File file) throws IOException, ConfigurationException {
        final JsonNode node = parse(file);
        for (Map.Entry<Object, Object> pref : System.getProperties().entrySet()) {
            final String prefName = (String) pref.getKey();
            if (prefName.startsWith(PROPERTY_PREFIX)) {
                final String configName = prefName.substring(PROPERTY_PREFIX.length());
                addOverride(node, configName, System.getProperty(prefName));
            }
        }
        final T config = json.readValue(node, klass);
        validate(file, config);
        return config;
    }

    public T build(String[] args) throws IOException, ConfigurationException {

        /**    去掉yaml
         InputStream input = ConfigurationFactory.class.getClassLoader().getResourceAsStream(servicePath);
         final JsonNode node = json.readYamlValue(input, JsonNode.class);

         for (Map.Entry<Object, Object> pref : System.getProperties().entrySet()) {
         final String prefName = (String) pref.getKey();
         if (prefName.startsWith(PROPERTY_PREFIX)) {
         final String configName = prefName.substring(PROPERTY_PREFIX.length());
         addOverride(node, configName, System.getProperty(prefName));
         }
         }

         for (int i = 0; args != null && i < args.length; i++) {
         if (i == 0) {
         addOrOverride(node, "http", "port", args[i]);
         }
         if (i == 1) {
         addOrOverride(node, "http", "adminPort", args[i]);
         break;
         }
         }
         final T config = json.readValue(node, klass);
         */
        Configuration config = null;
        try {

            int port = Integer.parseInt(args[0]), admin_port, monitor_port;
            admin_port = port + 1;
            monitor_port = port + 2;
            config = (Configuration) klass.newInstance();
            config.getHttpConfiguration().setPort(port);
            config.getHttpConfiguration().setAdminPort(admin_port);
            config.getHttpConfiguration().setMonitorPort(monitor_port);
            config.getHttpConfiguration().setStaticRoot(SystemParam.getWebHome());
            config.getHttpConfiguration().setServiceName(SystemParam.getInstanceName());

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

//        validate(yaml, conf);
        return (T) config;
    }

    private void addOrOverride(JsonNode root, String tag, String name, String value) {
        ObjectNode node = (ObjectNode) root.get(tag);
        if (node == null) {
            return;
        }
        node.put(name, value);

    }

    private void addOverride(JsonNode root, String name, String value) {
        JsonNode node = root;
        final Iterator<String> keys = Splitter.on('.').trimResults().split(name).iterator();
        while (keys.hasNext()) {
            final String key = keys.next();
            if (!(node instanceof ObjectNode)) {
                throw new IllegalArgumentException("Unable to override " + name + "; it's not a valid path.");
            }

            final ObjectNode obj = (ObjectNode) node;
            if (keys.hasNext()) {
                JsonNode child = obj.get(key);
                if (child == null) {
                    child = obj.objectNode();
                    obj.put(key, child);
                }
                node = child;
            } else {
                obj.put(key, value);
            }
        }
    }

    @Deprecated
    private JsonNode parse(File file) throws IOException {
        if (file.getName().endsWith(".yaml") || file.getName().endsWith(".yml")) {
            return json.readYamlValue(file, JsonNode.class);
        }
        return json.readValue(file, JsonNode.class);
    }

    private void validate(File file, T config) throws ConfigurationException {
        final ImmutableList<String> errors = validator.validate(config);
        if (!errors.isEmpty()) {
            throw new ConfigurationException(file, errors);
        }
    }
}
