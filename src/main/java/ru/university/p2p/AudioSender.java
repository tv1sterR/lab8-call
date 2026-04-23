package ru.university.p2p;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AudioSender {

    private TargetDataLine microphone;
    private boolean running = false;

    public void startSending(String host, int port) {
        running = true;

        new Thread(() -> {
            try {
                AudioFormat format = getFormat();
                microphone = AudioSystem.getTargetDataLine(format);
                microphone.open(format);
                microphone.start();

                byte[] buffer = new byte[4096];

                DatagramSocket socket = new DatagramSocket();
                InetAddress address = InetAddress.getByName(host);

                System.out.println("[AUDIO] Начата отправка аудио на " + host + ":" + port);

                while (running) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length);
                    DatagramPacket packet = new DatagramPacket(buffer, bytesRead, address, port);
                    socket.send(packet);
                }

                microphone.stop();
                microphone.close();
                socket.close();

            } catch (LineUnavailableException | IOException e) {
                System.err.println("[AUDIO] Ошибка отправки аудио: " + e.getMessage());
            }
        }, "AudioSender-Thread").start();
    }

    public void stop() {
        running = false;
    }

    private AudioFormat getFormat() {
        return new AudioFormat(
                16000,   // частота дискретизации
                16,      // битность
                1,       // моно
                true,    // signed
                false    // little-endian
        );
    }
}
