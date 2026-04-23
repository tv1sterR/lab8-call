package ru.university.p2p;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PNode {

    private final String nickname;
    private final int tcpPort;
    private final int udpPort;

    private Thread serverThread;

    private final AudioSender audioSender = new AudioSender();
    private final AudioReceiver audioReceiver = new AudioReceiver();

    public P2PNode(String nickname) {
        this.nickname = nickname;
        this.tcpPort = PortUtil.findFreePort();
        this.udpPort = PortUtil.findFreePort();
    }

    public String getNickname() {
        return nickname;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void startListening() {
        // TCP-сервер
        serverThread = new Thread(this::runServer, "TCP-Server-Thread");
        serverThread.start();

        // ВСЕГДА слушаем свой UDP-порт (двусторонний звук)
        audioReceiver.startReceiving(udpPort);
    }

    private void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {
            System.out.println("[TCP] Ожидание входящих соединений на порту " + tcpPort);

            while (!Thread.currentThread().isInterrupted()) {
                Socket client = serverSocket.accept();
                System.out.println("[TCP] Входящее соединение от " + client.getRemoteSocketAddress());
                CallSignaling.handleIncomingConnection(this, client);
            }
        } catch (IOException e) {
            System.err.println("[TCP] Ошибка сервера: " + e.getMessage());
        }
    }

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

    // запуск ТОЛЬКО отправки аудио (приём уже запущен в startListening)
    public void startAudio(String peerHost, int peerUdpPort) {
        audioSender.startSending(peerHost, peerUdpPort);
    }

    public void stopAudio() {
        audioSender.stop();
        audioReceiver.stop();
    }
}
