package ru.university.p2p;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PNode {

    private final String nickname;
    private final int tcpPort;
    private final int udpPort;

    private ServerSocket serverSocket;
    private Thread serverThread;

    public P2PNode(String nickname) {
        this.nickname = nickname;
        this.tcpPort = PortUtil.findFreePort();      // индивидуальный вариант
        this.udpPort = PortUtil.findFreePort();      // можно другой свободный
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public String getNickname() {
        return nickname;
    }

    // запуск ожидания входящих TCP-соединений
    public void startListening() {
        serverThread = new Thread(this::runServer, "TCP-Server-Thread");
        serverThread.start();
    }

    private void runServer() {
        try (ServerSocket ss = new ServerSocket(tcpPort)) {
            this.serverSocket = ss;
            System.out.println("[TCP] Ожидание входящих соединений на порту " + tcpPort);

            while (!Thread.currentThread().isInterrupted()) {
                Socket client = ss.accept();
                System.out.println("[TCP] Входящее соединение от " + client.getRemoteSocketAddress());
                // передаём в обработчик сигнализации
                CallSignaling.handleIncomingConnection(this, client);
            }
        } catch (IOException e) {
            System.err.println("[TCP] Ошибка сервера: " + e.getMessage());
        }
    }

    // исходящее соединение к собеседнику
    public void callPeer(String host, int port) {
        new Thread(() -> {
            try {
                System.out.println("[TCP] Подключение к " + host + ":" + port);
                Socket socket = new Socket(host, port);
                CallSignaling.initiateCall(this, socket);
            } catch (IOException e) {
                System.err.println("[TCP] Не удалось подключиться: " + e.getMessage());
            }
        }, "TCP-Client-Thread").start();
    }
}
