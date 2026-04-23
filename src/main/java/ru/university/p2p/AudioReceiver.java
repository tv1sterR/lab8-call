package ru.university.p2p;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class AudioReceiver {

    private SourceDataLine speakers;
    private volatile boolean running = false;

    public void startReceiving(int port) {
        if (running) return;
        running = true;

        new Thread(() -> {
            try {
                AudioFormat format = getFormat();
                speakers = AudioSystem.getSourceDataLine(format);
                speakers.open(format);
                speakers.start();

                DatagramSocket socket = new DatagramSocket(port);
                byte[] buffer = new byte[4096];

                System.out.println("[AUDIO] Ожидание аудио на UDP порту " + port);

                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    speakers.write(packet.getData(), 0, packet.getLength());
                }

                speakers.stop();
                speakers.close();
                socket.close();

                System.out.println("[AUDIO] Приём аудио остановлен");

            } catch (LineUnavailableException | IOException e) {
                System.err.println("[AUDIO] Ошибка приёма аудио: " + e.getMessage());
            }
        }, "AudioReceiver-Thread").start();
    }

    public void stop() {
        running = false;
    }

    private AudioFormat getFormat() {
        return new AudioFormat(
                16000,
                16,
                1,
                true,
                false
        );
    }
}
