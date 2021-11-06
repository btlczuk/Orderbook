import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.MessageBuildUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static utils.CommandLineUtils.generateCommandLine;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        CommandLine commandLine = generateCommandLine(args);
        String[] currencyPairs = commandLine.getOptionValues('c');
        int rowLimit = Integer.parseInt(commandLine.getOptionValue('l', "10"));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> log.info("Shutting down ...")));
        Client client = new Client(new URI("wss://ws-feed.exchange.coinbase.com"), List.of(currencyPairs), rowLimit);
        client.connectBlocking(10, TimeUnit.SECONDS);
        String jsonString = MessageBuildUtils.createSubscribeMessage(List.of(currencyPairs), List.of("level2"));
        client.send(jsonString);
    }
}