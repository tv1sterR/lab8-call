package ru.university.p2p;

import java.io.IOException;
import java.net.ServerSocket;

public class PortUtil {

    public static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort(); // ОС сама выберет свободный порт
        } catch (IOException e) {
            throw new RuntimeException("Не удалось найти свободный порт", e);
        }
    }
}
