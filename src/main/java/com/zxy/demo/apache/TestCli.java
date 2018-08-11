package com.zxy.demo.apache;

import org.apache.commons.cli.*;

/**
 * CLI 即Command Line Interface，也就是"命令行接口"，它为Java 程序访问和解析命令行
 */
public class TestCli {
    public static void main(String[] args) throws Exception {
        String[] arg = {"-h", "-c", "config.xml"};
        testOptions(arg);
    }

    public static void testOptions(String[] args) {
        Options options = new Options();
        Option opt = new Option("h", "help", false, "Print help");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("c", "configFile", true, "Name server config properties file");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("p", "printConfigItem", false, "Print all config item");
        opt.setRequired(false);
        options.addOption(opt);

        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        CommandLine commandLine;
        CommandLineParser parser = new DefaultParser();
        try {
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption('h')) {
                // 打印使用帮助
                hf.printHelp("testApp", options, true);
            }

            // 打印opts的名称和值
            System.out.println("--------------------------------------");
            Option[] opts = commandLine.getOptions();
            if (opts != null) {
                for (Option opt1 : opts) {
                    String name = opt1.getLongOpt();
                    String value = commandLine.getOptionValue(name);
                    System.out.println(name + "=>" + value);
                }
            }
        } catch (ParseException e) {
            hf.printHelp("testApp", options, true);
        }
    }
}
