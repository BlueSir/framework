package com.sohu.smc.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-29
 * Time: 下午4:44
 * To change this template use File | Settings | File Templates.
 */
public class PropertyUtil {

    public static Properties load(String path) {
        return load(path, PropertyUtil.class);
    }

    public static Properties load(String fileName, Class clazz) {
        InputStream inputStream = null;
        try {
            inputStream = getResource(fileName, clazz).openStream();
            Properties prop = new Properties();
            prop.load(inputStream);
            System.out.println("load property file from path[" + fileName + "]");

            return prop;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return null;
    }


    private static URL getResource(String resourceName, Class clazz) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);

        if (url == null) {
            url = clazz.getClassLoader().getResource(resourceName);
        }

        return url;
    }

}
