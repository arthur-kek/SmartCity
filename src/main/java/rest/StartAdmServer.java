package rest;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartAdmServer {



    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create(Constants.ADM_SERVER_ADDRESS);
        server.start();

        System.out.println("SERVER RUNNING!");
        System.out.println("SERVER STARTED ON: " + Constants.ADM_SERVER_ADDRESS);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        do {
            System.out.println("PLEASE, TYPE --exit TO STOP RUNNING SERVER.\n");
        } while (!br.readLine().equals("--exit"));

        System.out.println("STOPPING SERVER");
        server.stop(0);
        System.out.println("SERVER STOPPED");
    }
}
