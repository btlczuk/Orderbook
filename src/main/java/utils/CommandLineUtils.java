package utils;

import org.apache.commons.cli.*;
import org.slf4j.Logger;

import java.util.Arrays;

public class CommandLineUtils {
    private static Options generateOptions() {
        final Option printLimitOption = Option.builder("l")
                .required(false)
                .hasArg()
                .longOpt("print_limit")
                .desc("The number of rows to show for each side of the order book.")
                .build();
        final Option currencyPairsOption = Option.builder("c")
                .required(true)
                .hasArgs()
                .longOpt("currencies")
                .desc("Currency pairs to subscribe to.")
                .build();
        final Options options = new Options();
        options.addOption(printLimitOption);
        options.addOption(currencyPairsOption);
        options.addOption(Option.builder("h").longOpt("help").build());
        return options;
    }

    public static CommandLine generateCommandLine(final String[] commandLineArguments)
    {
        final Options options = generateOptions();
        final CommandLineParser cmdLineParser = new DefaultParser();
        CommandLine commandLine;
        try
        {
            commandLine = cmdLineParser.parse(options, commandLineArguments);
        }
        catch (ParseException parseException)
        {
            showHelp(options);
            throw new IllegalStateException(
                    "Unable to parse command-line arguments "
                            + Arrays.toString(commandLineArguments) + " due to: "
                            + parseException);
        }
        return commandLine;
    }

    private static void showHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Order Book", options, true);
    }

    public static void clearConsole(Logger log){
        try{
            String operatingSystem = System.getProperty("os.name");

            if(operatingSystem.contains("Windows")){
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        } catch(Exception e){
            log.error(e.toString());
        }
    }
}
