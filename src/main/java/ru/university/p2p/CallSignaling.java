package ru.university.p2p;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CallSignaling {

    public static void initiateCall(P2PNode node, Socket socket) {
        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            out.println("CALL_START " + node.getNickname() + " " + node.getUdpPort());
            System.out.println("[SIGNAL] Отправлено: CALL_START " + node.getNickname() + " " + node.getUdpPort());

            String line = in.readLine();
            if (line == null) {
                System.out.println("[SIGNAL] Соединение закрыто собеседником");
                return;
            }

            if (line.startsWith("CALL_ACCEPTED")) {
                System.out.println("[SIGNAL] Звонок принят: " + line);
                String[] parts = line.split("\\s+");
                String peerNick = parts[1];
                int peerUdpPort = Integer.parseInt(parts[2]);
                System.out.println("[SIGNAL] Собеседник: " + peerNick + ", его UDP порт: " + peerUdpPort);

                String peerHost = socket.getInetAddress().getHostAddress();
                System.out.println("[AUDIO] Запуск аудио к " + peerHost + ":" + peerUdpPort);
                node.startAudio(peerHost, peerUdpPort);

            } else if (line.startsWith("CALL_REJECTED")) {
                System.out.println("[SIGNAL] Звонок отклонён: " + line);
            } else {
                System.out.println("[SIGNAL] Неизвестная команда: " + line);
            }

        } catch (IOException e) {
            System.err.println("[SIGNAL] Ошибка при инициации звонка: " + e.getMessage());
        }
    }

    public static void handleIncomingConnection(P2PNode node, Socket socket) {
        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line = in.readLine();
            if (line == null) {
                System.out.println("[SIGNAL] Пустое соединение");
                return;
            }

            if (line.startsWith("CALL_START")) {
                String[] parts = line.split("\\s+");
                String peerNick = parts.length > 1 ? parts[1] : "UNKNOWN";
                int peerUdpPort = parts.length > 2 ? Integer.parseInt(parts[2]) : -1;

                System.out.println("[SIGNAL] Входящий звонок от " + peerNick +
                        " (UDP порт собеседника: " + peerUdpPort + ")");

                out.println("CALL_ACCEPTED " + node.getNickname() + " " + node.getUdpPort());
                System.out.println("[SIGNAL] Отправлено: CALL_ACCEPTED " + node.getNickname() + " " + node.getUdpPort());

                String peerHost = socket.getInetAddress().getHostAddress();
                System.out.println("[AUDIO] Запуск аудио к " + peerHost + ":" + peerUdpPort);
                node.startAudio(peerHost, peerUdpPort);

            } else {
                System.out.println("[SIGNAL] Неизвестная команда: " + line);
            }

        } catch (IOException e) {
            System.err.println("[SIGNAL] Ошибка при обработке входящего соединения: " + e.getMessage());
        }
    }
}
