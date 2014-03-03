package com.sohu.smc.core.cli;

import com.sohu.smc.common.util.JarLocation;
import com.sohu.smc.core.AbstractService;
import com.sohu.smc.core.config.Configuration;
import com.sohu.smc.core.config.Environment;
import com.sohu.smc.core.server.Action;
import com.sohu.smc.core.server.builer.SingleActionMapping;
import org.apache.commons.cli.HelpFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class UsagePrinter {
    private UsagePrinter() {
        // singleton
    }

    public static void printRootHelp(AbstractService<?> service) {
        System.out.printf("java -jar %s <command> [arg1 arg2]\n\n", new JarLocation(service.getClass()));
        System.out.println("Commands");
        System.out.println("========\n");

        for (Command command : service.getCommands()) {
            printCommandHelp(command, service.getClass());
        }
    }

    public static void printCommandHelp(Command cmd, Class<?> klass) {
        printCommandHelp(cmd, klass, null);
    }

    public static void printCommandHelp(Command cmd, Class<?> klass, String errorMessage) {
        if (errorMessage != null) {
            System.err.println(errorMessage);
            System.out.println();
        }

        System.out.println(format(cmd));
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setLongOptPrefix(" --");
        helpFormatter.printHelp(String.format("java -jar %s", cmd.getUsage(klass)),
                cmd.getOptionsWithHelp());
        System.out.println("\n");
    }

    private static void printWorkerStarting() {

    }

    public static void printHttpServerStarting(Command cmd, Configuration conf, Environment environment) {
        final String title = cmd.getName() + ": " + cmd.getDescription();
        final String url = "service address: http://" + conf.getHttpConfiguration().getIp() + ":" + conf.getHttpConfiguration().getPort() ;
        final String http = "Stats admin address: http://" + conf.getHttpConfiguration().getIp() + ":" + conf.getHttpConfiguration().getAdminPort();
        final String admin_http = http + "/stats.txt";
        final String admin_http_graph = http + "/graph/";

        int[] leng = {title.length(), admin_http.length(), admin_http_graph.length(), url.length()};
        Arrays.sort(leng);
        int max = leng[leng.length - 1];

        //
        List<String> list = new ArrayList<String>();
        list.add(title);
        list.add(url);
        list.add("actions:");
        Map<String, Action> actions = SingleActionMapping.getInstance().getMapping();
        for (String entry : actions.keySet()) {
            list.add("------   "+url + entry);
        }
        list.add(admin_http);
        list.add(admin_http_graph);

        //output the info
        System.out.print(getLine(max));
        for (String str : list) {
            System.out.print("|" + str);
            for (int i = 0; i < (max - str.length()); i++) {
                System.out.print(" ");
            }
            System.out.println("|");
        }
        System.out.println(getLine(max));
    }

    private static String format(Command cmd) {
        final String title = "|" + cmd.getName() + ": " + cmd.getDescription() + "|";
        return title;
    }

    public static String getLine(int length) {
        int len = length + 2;
        final StringBuilder builder = new StringBuilder(len);
        builder.append("+");
        for (int i = 0; i < length; i++) {
            builder.append('-');
        }
        builder.append("+");
        return builder.append("\n").toString();
    }

    public static String getStar(int length) {
        int len = length + 2;
        final StringBuilder builder = new StringBuilder(len);
        builder.append("\n");
        for (int i = 0; i < len; i++) {
            builder.append('*');
        }
        return builder.append("\n").toString();
    }
}
