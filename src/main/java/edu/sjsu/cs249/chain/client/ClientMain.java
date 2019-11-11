package edu.sjsu.cs249.chain.client;

import edu.sjsu.cs249.chain.util.Utils;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;
import java.util.Scanner;

/**
 * Replicated Incremental HashTable Service Client
 */
public class ClientMain {

    private TailChainClient client;

    private String getUsageStr() {
        String str = "\n"
                + "+--------------------------------------------------------+\n"
                + "|           Replicated Hashtable Service Client          |\n"
                + "| Usage: get key | inc key value | del key | quit | help |\n"
                + "+--------------------------------------------------------+\n";
        return str;
    }

    private void repl() {
        System.out.println(getUsageStr());
        Scanner scanner = new Scanner(System.in);
        String line;
        do {
            System.out.print("cli>>> ");
            line = scanner.nextLine();
            if (line.trim().length() == 0) {
                continue;
            }
            String input[] = line.split(" ");
            String op = input[0];
            String key;
            int val;
            switch (op) {
                case "get":
                    if (input.length < 2) {
                        System.err.println("Missing arguments");
                        break;
                    }
                    key = input[1];
                    // todo: call get api
                    val = 2812;
                    System.out.println(val);
                    // handle tail change
                    break;
                case "inc":
                    if (input.length < 3) {
                        System.err.println("Missing arguments");
                        break;
                    }
                    key = input[1];
                    try {
                        val = Integer.parseInt(input[2]);
                    } catch (NumberFormatException e) {
                        // value not a number
                        System.err.println("Value should be an integer");
                        break;
                    }
                    // todo: call inc api
                    System.out.println("status: " + "ok");
                    // handle head change case
                    break;
                case "del":
                    if (input.length < 2) {
                        System.err.println("Missing arguments");
                        break;
                    }
                    key = input[1];
                    System.out.println("key deleted");
                    // handle head change case
                    break;
                case "help":
                    System.out.println("Usage: get key | inc key value | del key | quit | help");
                    break;
                case "quit":
                    System.exit(0);
                default:
                    System.out.println("Operation not supported");
            }
        } while (true);
    }

    void setup() throws IOException, InterruptedException {
        client.connectToZk();
    }

    private ClientMain(String zkAddr, String root, String host, int port) {
        client = new TailChainClient(zkAddr, root, host, port);
    }

    // todo: refactor
    private static String getHost(CommandLine cli) {
        String host;
        if (cli.hasOption(ClientClaHandler.HOST)) {
            host = cli.getOptionValue(ClientClaHandler.HOST);
        } else if (cli.hasOption(ClientClaHandler.NIF)) {
            host = Utils.getHostIp4Addr(cli.getOptionValue(ClientClaHandler.NIF));
        } else {
            host = Utils.getLocalhost();
        }
        return host;
    }

    // entry point for client
    public static void main(String[] args) throws IOException, InterruptedException {
        CommandLine cli = ClientClaHandler.parse(args);
        String zkAddr = cli.getOptionValue(ClientClaHandler.ORACLE);
        String root = cli.getOptionValue(ClientClaHandler.ZROOT);
        int port = Integer.parseInt(cli.getOptionValue(ClientClaHandler.PORT));
        String host = getHost(cli);
        ClientMain main = new ClientMain(zkAddr, root, host, port);
        main.setup();

        if (cli.hasOption(ClientClaHandler.REPL)) {
            main.repl();
        } else if (cli.hasOption(ClientClaHandler.GET)) {
            String key = cli.getOptionValue(ClientClaHandler.GET);
            // call get api
            System.out.println("OP: GET key: " + key + " val: " + 434569834);
        } else if (cli.hasOption(ClientClaHandler.INCR)) {
            String[] input = cli.getOptionValues(ClientClaHandler.INCR);
            String key = input[0];
            int val = 0;
            try {
                val = Integer.parseInt(input[1]);
            } catch (NumberFormatException e) {
                System.err.println("Value should be an integer");
                System.exit(1);
            }
            System.out.println("OP: INC key: " + key + " val: " + val);
        } else if (cli.hasOption(ClientClaHandler.DELETE)) {
            String key = cli.getOptionValue(ClientClaHandler.DELETE);
            System.out.println("OP: DEL key: " + key);
        }
    }

    private void responseHandler(String op, Response rsp) {

    }
}
