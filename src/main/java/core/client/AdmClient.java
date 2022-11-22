package core.client;

import core.enums.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class AdmClient {

    private static void help(){
        System.out.println("\nLIST OF COMMANDS:\n" +
                "-a OR -all TO SHOW THE LIST OF ALL TAXIS CURRENTLY PRESENT IN THE NETWORK\n" +
                "-l OR -last WITH [n] [taxiID] TO SHOW THE LAST n STATISTICS OF GIVEN taxiId \n" +
                "-tf OR - timeframe WITH [ts1] [ts2] TO SHOW THE STATISTICS OF ALL TAXIS IN TIMEFRAME BETWEEN ts1 AND ts2\n" +
                "-h OR -help OR -? TO SHOW HELP MENU\n" +
                "-e OR -exit TO EXIT\n");
    }

    private static void error(){
        System.out.println("\nSERVICE ERROR:\n");
    }

    private static void all(){
        System.out.println("\nALL:\n");
    }

    private static void last(){
        System.out.println("\nLAST:\n");
    }

    private static void timeframe(){
        System.out.println("\nTIMEFRAME:\n");
    }

    private static void exit(){
        System.out.println("\nSERVER IS SHUTTING DOWN...\n");
    }

    private static Command parseCommand(String line) {
        if (!line.isEmpty()) {

            String command = line.split(" ")[0];

            if (command.equals("-h") || command.equals("-help")) {
                return Command.HELP;
            }

            if (command.equals("-a") || command.equals("-all")) {
                return Command.ALL;
            }

            if (command.equals("-l") || command.equals("-last")) {
                return Command.LAST_N;
            }

            if (command.equals("-tf") || command.equals("-timeframe")) {
                return Command.TIMEFRAME;
            }

            if (command.equals("-e") || command.equals("-exit")) {
                return Command.EXIT;
            }
        }
        return Command.ERROR;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("ADMINISTRATION CLIENT RUNNING...\n");
        System.out.println("TYPE -h OR -help TO SHOW HELP MENU\n");

        boolean exit = false;
        while (!exit) {
            Command command = parseCommand(br.readLine());
            switch (command) {
                case HELP:
                    help();
                    break;
                case ALL:
                    all();
                    break;
                case LAST_N:
                    last();
                    break;
                case TIMEFRAME:
                    timeframe();
                    break;
                case ERROR:
                    error();
                    break;
                case EXIT:
                    exit = true;
                    exit();
                    break;
            }
        }
        br.close();
        System.exit(0);
    }
}