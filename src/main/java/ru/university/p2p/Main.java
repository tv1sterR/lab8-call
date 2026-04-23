package ru.university.p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Введите ваш ник: ");
        String nick = console.readLine();

        P2PNode node = new P2PNode(nick);
        System.out.println("Ваш TCP порт: " + node.getTcpPort());
        System.out.println("Ваш UDP порт: " + node.getUdpPort());

        node.startListening();

        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1 - Позвонить");
            System.out.println("0 - Выход");
            System.out.print("Выбор: ");
            String choice = console.readLine();

            if ("0".equals(choice)) {
                System.out.println("Выход...");
                System.exit(0);
            } else if ("1".equals(choice)) {
                System.out.print("IP собеседника: ");
                String host = console.readLine();
                System.out.print("TCP порт собеседника: ");
                int port = Integer.parseInt(console.readLine());

                node.callPeer(host, port);
            }
        }
    }
}
