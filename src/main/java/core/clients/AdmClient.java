package core.clients;

import com.annimon.stream.Stream;
import core.enums.Command;
import core.wrappers.RESTWrapper;
import rest.beans.Taxi;
import rest.beans.responses.AdmClientResponse;
import utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

class AdmClient {

    private static void help() {
        System.out.println("\nLIST OF COMMANDS:\n" +
                "-a OR -all TO SHOW THE LIST OF ALL TAXIS CURRENTLY PRESENT IN THE NETWORK\n" +
                "-l OR -last WITH [n] [taxiID] TO SHOW THE LAST n STATISTICS OF GIVEN taxiId \n" +
                "-tf OR - timeframe WITH [ts1] [ts2] TO SHOW THE STATISTICS OF ALL TAXIS IN TIMEFRAME BETWEEN ts1 AND ts2\n" +
                "-h OR -help OR -? TO SHOW HELP MENU\n" +
                "-e OR -exit TO EXIT\n");
    }

    private static void error() {
        System.out.println("\nERROR, UNKNOWN COMMAND.\n");
        showHelpLine();
    }

    private static void all() {
        System.out.println("\nLIST OF ALL TAXIS IN THE NETWORK:\n");
        List<Taxi> taxiList = RESTWrapper.getInstance().getTaxis(Constants.ADM_SERVER_ADDRESS);
        Stream.of(taxiList)
                .forEach(System.out::println);
        showHelpLine();
    }

    private static void last(String line) {
        System.out.println("\nLAST N MEANS FROM TAXI:\n");

        String[] values = line.split(" ");
        int n = Integer.parseInt(values[1]);
        int taxiId = Integer.parseInt(values[2]);

        System.out.println("\nLAST N MEANS FROM TAXI ID " + taxiId + " STATISTICS:\n");

        AdmClientResponse response = RESTWrapper.getInstance().getLastNStatisticsForTaxi(Constants.ADM_SERVER_ADDRESS, n, taxiId);

        System.out.println(response.toString());

        showHelpLine();
    }

    private static void timeframe(String line) {
        System.out.println("\nTIMEFRAME:\n");

        String[] values = line.split(" ");
        long ts1 = Long.parseLong(values[1]);
        long ts2 = Long.parseLong(values[2]);

        AdmClientResponse response = RESTWrapper.getInstance().getAllStatisticsInTimeFrame(Constants.ADM_SERVER_ADDRESS, ts1, ts2);

        System.out.println(response.toString());

        showHelpLine();
    }

    private static void exit() {
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

    private static void showHelpLine() {
        System.out.println("TYPE -h OR -help TO SHOW HELP MENU\n");
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("ADMINISTRATION CLIENT RUNNING...\n");
        showHelpLine();

        boolean exit = false;
        while (!exit) {
            String line = br.readLine();
            Command command = parseCommand(line);
            switch (command) {
                case HELP:
                    help();
                    break;
                case ALL:
                    all();
                    break;
                case LAST_N:
                    last(line);
                    break;
                case TIMEFRAME:
                    timeframe(line);
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