import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import java.io.File;
import java.nio.file.*;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Threads {
    // TODO: DOESNT FUCKING WORK
    public static class fileSystemWatcherThread extends Thread {
        public void run() {
            try {
                Path filePath = Paths.get("data/" + CLIDiscord.server.getName());
                WatchService watchService = FileSystems.getDefault().newWatchService();
                filePath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

                Timer timer = new Timer();
                while (CLIDiscord.channelWatch) {
                    if (timer.passedS(10)) {
                        timer.reset();

                        WatchKey key;
                        key = watchService.poll();

                        if (key != null) {
                            key.pollEvents().forEach(System.out::println);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception in File System Watcher Thread.");
                e.printStackTrace();
            }
        }
    }

    public static class activeCloningDetectionThread extends Thread {
        public void run() {
            try {
                cloneServerThread obj = new cloneServerThread();
                obj.run();

                System.out.println("Checking for channel updates every 10 seconds.");
                Timer timer = new Timer();
                while (CLIDiscord.channelWatch) {
                    if (timer.passedS(10)) {
                        timer.reset();
                        if (!CLIDiscord.server.getChannels().equals(CLIDiscord.serverChannelsList)) {
                            System.out.println("Desynced, syncing.");
                            // System.out.println("Previous channel list: " + CLIDiscord.serverChannelsList);
                            // System.out.println("Current channel list: " + CLIDiscord.server.getChannels());

                            cloneServerThread object = new cloneServerThread();
                            object.start();
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception in Active Cloning Detection Thread.");
                e.printStackTrace();
            }
        }
    }

    public static class cloneServerThread extends Thread {
        public void run() {
            try {
                System.out.println("Cloning server.");

                Utils.deleteDirectory(new File("data/" + CLIDiscord.server.getName()));

                CLIDiscord.serverChannelsList = CLIDiscord.server.getChannels();
                String currentCategory = "";
                String currentThread = "";
                System.out.println("Output folder is the current directory.");
                for (ServerChannel s : CLIDiscord.serverChannelsList) {
                    String folderName = s.getName() + " - " + s.getType();
                    switch (s.getType()) {
                        case CHANNEL_CATEGORY:
                            new File("data/" + CLIDiscord.server.getName() + "/" + folderName).mkdirs();
                            currentCategory = "data/" + CLIDiscord.server.getName() + "/" + folderName;
                            continue;
                        case SERVER_TEXT_CHANNEL:
                            if (currentCategory.isEmpty()) {
                                new File("data/" + CLIDiscord.server.getName() + "/" + folderName).mkdirs();
                                currentThread = "data/" + CLIDiscord.server.getName() + "/" + folderName + "/";
                            } else {
                                new File(currentCategory + "/" + folderName).mkdirs();
                                currentThread = currentCategory + "/" + folderName + "/";
                            }

                            List<Message> messages = ((TextChannel) s).getMessagesAsStream().collect(Collectors.toList());
                            Utils.processMessages(messages, currentThread);

                            continue;
                        case SERVER_VOICE_CHANNEL:
                            if (currentCategory.isEmpty()) {
                                new File("data/" + CLIDiscord.server.getName() + "/" + folderName).mkdirs();
                            } else {
                                new File(currentCategory + "/" + folderName).mkdirs();
                            }
                            continue;
                        case SERVER_PUBLIC_THREAD:
                            new File(currentThread + "/" + folderName).mkdirs();
                            messages = ((TextChannel) s).getMessagesAsStream().collect(Collectors.toList());
                            Utils.processMessages(messages, currentThread);
                            continue;
                    }
                }

                System.out.println("Finished cloning server.");
            } catch (Exception e) {
                System.out.println("Exception in Clone Server Thread.");
                e.printStackTrace();
            }
        }
    }

    public static class userInputThread extends Thread {
        public void run() {
            try {
                while (CLIDiscord.userInput) {
                    Scanner scanner = new Scanner(System.in);
                    String s = scanner.nextLine();
                    if (!s.isEmpty()) {
                        CLIDiscord.channel.sendMessage(s);
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception in User Input Thread.");
                e.printStackTrace();
            }
        }
    }
}
