package dev.arctic.admiral.utilities;

import dev.arctic.admiral.Admiral;

import java.util.Scanner;

public class ConsoleListener {

    public static Thread consoleThread;

    public static void startConsoleListener() {
        consoleThread = new Thread(() -> {
            System.out.println("Starting console listener...");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    String line = scanner.nextLine().trim();
                    if (line.equalsIgnoreCase("restart")) {
                        Admiral.shutdown();
                    } else {
                        System.out.println("Invalid input");
                    }
                } catch (Exception e) {
                    System.out.println("[Console] Listener shutting down.");
                    break;
                }
            }
        }, "Console Listener");

        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    public static void stopConsoleListener() {
        if (consoleThread != null && consoleThread.isAlive()) {
            consoleThread.interrupt(); // will break nextLine() with an exception
        }
    }
}
