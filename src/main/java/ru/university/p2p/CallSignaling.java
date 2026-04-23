package ru.university.p2p;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CallSignaling {

    // инициатор звонка
    public static void initiateCall(P2PNode node, Socket socket) {
        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            // отправляем команду CALL_START + свой ник + свой UDP-порт
            out.println("CALL_START " + node.getNickname() + " " + node.getUdpPort());

            // ждём ответ
            String line = in.readLine();
            if (line == null) return;

            if (line.startsWith("CALL_ACCEPTED")) {
                System.out.println("[SIGNAL] Звонок принят: " + line);
                // здесь позже запустим UDP-аудио
            } else if (line.startsWith("CALL_REJECTED")) {
                System.out.println("[SIGNAL] Звонок отклонён");
            }

        } catch (IOException e) {
            System.err.println("[SIGNAL] Ошибка при инициации звонка: " + e.getMessage());
        }
    }

    // обработка входящего соединения
    public static void handleIncomingConnection(P2PNode node, Socket socket) {
        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line = in.readLine();
            if (line == null) return;

            if (line.startsWith("CALL_START")) {
                String[] parts = line.split("\\s+");
                String peerNick = parts[1];
                int peerUdpPort = Integer.parseInt(parts[2]);

                System.out.println("[SIGNAL] Входящий звонок от " + peerNick +
                        " (UDP порт собеседника: " + peerUdpPort + ")");

                // для простоты — всегда принимаем
                out.println("CALL_ACCEPTED " + node.getNickname() + " " + node.getUdpPort());

                // здесь позже запустим UDP-аудио
            }

        } catch (IOException e) {
            System.err.println("[SIGNAL] Ошибка при обработке входящего соединения: " + e.getMessage());
        }
    }
}
