package com.sohu.smc.core.json;

import com.google.common.base.Charsets;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.*;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class YamlConverter {
    private final Logger log = LoggerFactory.getLogger(YamlConverter.class.getName());

    private final Json json;
    private final JsonFactory factory;

    YamlConverter(Json json, JsonFactory factory) {
        this.factory = factory;
        this.json = json;
    }

    JsonNode convert(File file) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final JsonGenerator generator = factory.createJsonGenerator(output).useDefaultPrettyPrinter();
        final Reader reader = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8);
        try {
            final Node yaml = new org.yaml.snakeyaml.Yaml().compose(reader);
            build(yaml, generator);
            generator.close();
            log.debug("Parsed {} as:\n {}", file, output.toString());
            return json.readValue(output.toByteArray(), JsonNode.class);
        } finally {
            reader.close();
        }
    }


    JsonNode convert(InputStream inputStream) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final JsonGenerator generator = factory.createJsonGenerator(output).useDefaultPrettyPrinter();
        final Reader reader = new InputStreamReader(inputStream, Charsets.UTF_8);
        try {
            final Node yaml = new org.yaml.snakeyaml.Yaml().compose(reader);
            build(yaml, generator);
            generator.close();
            return json.readValue(output.toByteArray(), JsonNode.class);
        } finally {
            reader.close();
        }
    }

    private void build(Node yaml, JsonGenerator json) throws IOException {
        if (yaml instanceof MappingNode) {
            final MappingNode mappingNode = (MappingNode) yaml;
            json.writeStartObject();
            for (NodeTuple tuple : mappingNode.getValue()) {
                if (tuple.getKeyNode() instanceof ScalarNode) {
                    json.writeFieldName(((ScalarNode) tuple.getKeyNode()).getValue());
                }

                build(tuple.getValueNode(), json);
            }
            json.writeEndObject();
        } else if (yaml instanceof SequenceNode) {
            json.writeStartArray();
            for (Node node : ((SequenceNode) yaml).getValue()) {
                build(node, json);
            }
            json.writeEndArray();
        } else if (yaml instanceof ScalarNode) {
            final ScalarNode scalarNode = (ScalarNode) yaml;
            final String className = scalarNode.getTag().getClassName();
            if ("bool".equals(className)) {
                json.writeBoolean(Boolean.parseBoolean(scalarNode.getValue()));
            } else if ("int".equals(className)) {
                json.writeNumber(Long.parseLong(scalarNode.getValue()));
            } else if ("float".equals(className)) {
                json.writeNumber(Double.parseDouble(scalarNode.getValue()));
            } else if ("null".equals(className)) {
                json.writeNull();
            } else {
                json.writeString(supportSystemProperty(scalarNode.getValue()));
            }
        }
    }

    /**
     * 用系统变量替换${var}
     *
     * @param nodeValue
     * @return
     */
    private String supportSystemProperty(String nodeValue) {
        if (!nodeValue.contains("${")) {
            return nodeValue;
        }

        Pattern p = null;
        Matcher m = null;
        String var_str = null;

        p = Pattern.compile("(\\$\\{[^\\}]*\\})");
        m = p.matcher(nodeValue);
        String ret = nodeValue;

        //把找到的所有符合匹配规则的字串都替换
        while (m.find()) {
            var_str = m.group(0);

            String tmp = System.getProperty(var_str.replace("${", "").replace("}", ""));
            if (tmp != null && !tmp.equals("")) {
                ret = ret.replace(var_str, tmp);
            }
        }

        return ret;
    }

}
