package dev.arctic.admiral.utilities;

import dev.arctic.admiral.Admiral;

import java.util.Scanner;

public class ConsoleListener {

    public static Thread consoleThread;

    public static void startConsoleListener(){
        consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals("restart")) {
                    try {
                        Admiral.shutdown();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Invalid input");
                }
            }
        }, "Console Listener");
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    public static void stopConsoleListener(){
        consoleThread.interrupt();
    }
}
