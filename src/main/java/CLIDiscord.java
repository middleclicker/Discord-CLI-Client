import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CLIDiscord {
    public static boolean userInput = false;
    public static boolean displayMessages = false;
    public static boolean doneCloneServer = false;
    public static User bot;
    public static Server server;
    public static TextChannel channel;
    public static String currentText;
    public static String botName;

    public static void main(String[] args) {
        String token;

        System.out.println("Please input your token: ");

        Scanner scanner = new Scanner(System.in);
        token = scanner.nextLine();

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        botName = api.getYourself().getName();
        bot = api.getYourself();

        api.addListener(new MessageListener());
    }

    public static class MessageListener implements MessageCreateListener {

        @Override
        public void onMessageCreate(MessageCreateEvent event) {
            if (displayMessages) {
                currentText = event.getMessageAuthor().getDisplayName() + ": " + event.getMessageContent();
                System.out.println(currentText);
            }

            if (event.getMessageContent().equalsIgnoreCase("!toggleUserInput")) {
                userInput = !userInput;
                channel = event.getChannel();

                userInputThread object = new userInputThread();
                if (userInput) {
                    System.out.println("User Input: " + userInput);
                    object.start();
                } else {
                    object.stop();
                    System.out.println("User Input: " + userInput);
                }
            }

            if (event.getMessageContent().equalsIgnoreCase("!toggleDisplayMessages")) {
                displayMessages = !displayMessages;
            }

            // Clone the server to local folders
            if (event.getMessageContent().equalsIgnoreCase("!cloneServer")) {
                doneCloneServer = false;

                server = event.getServer().get();
                System.out.println("Cloning server.");

                cloneServerThread object = new cloneServerThread();
                object.start();
            }

            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("I'm alive mf now stop pinging me.");
            }

            if (event.getMessageContent().equalsIgnoreCase("!help")) {
                event.getChannel().sendMessage("Fuck off, here are your shitty commands: toggleDisplayMessages, ping, ratio, clearConsole");
            }

            if (event.getMessageContent().equalsIgnoreCase("!ratio")) {
                event.getChannel().sendMessage("Reddit fag? Heres your ratio anyways: " + getRandomNumber(1, 100) + "/" + getRandomNumber(1, 100));
            }

            if (event.getMessageContent().equalsIgnoreCase("!cope")) {
                event.getChannel().sendMessage("Cope.");
            }

            if (detectDadJoke(event.getMessageContent()) != null && event.getMessageAuthor().getId() != bot.getId()) {
                String response = event.getMessageContent();
                double randomNumber = Math.random();
                if (randomNumber < 0.5) {
                    if (response.contains("andy") || response.contains("middleclicker")) {
                        event.getChannel().sendMessage("hi " + event.getMessageAuthor().getName() + "\n" + "middleclicker is around here somewhere");
                    } else if (response.contains("steven") || response.contains("deadbean")) {
                        event.getChannel().sendMessage("hi " + event.getMessageAuthor().getName() + "\n" + "steven is gay as fuck am i right?");
                    } else if (response.contains("casi") || response.contains("shitcoder")) {
                        event.getChannel().sendMessage("hi " + event.getMessageAuthor().getName() + "\n" + "hes cool i guess");
                    } else {
                        event.getChannel().sendMessage("hi " + detectDadJoke(response) + "\n" + "dad's around here somewhere");
                    }

                } else {
                    if (response.contains("andy") || response.contains("middleclicker")) {
                        event.getChannel().sendMessage("hello " + event.getMessageAuthor().getName() + "\n" + "middleclicker is around here somewhere");
                    } else if (response.contains("steven") || response.contains("deadbean")) {
                        event.getChannel().sendMessage("hello " + event.getMessageAuthor().getName() + "\n" + "steven is gay as fuck am i right?");
                    } else if (response.contains("casi") || response.contains("shitcoder")) {
                        event.getChannel().sendMessage("hello " + event.getMessageAuthor().getName() + "\n" + "hes cool i guess");
                    } else {
                        event.getChannel().sendMessage("hello " + detectDadJoke(response) + "\n" + "dad's around here somewhere");
                    }
                }

            }

            if (event.getMessageContent().equalsIgnoreCase("!clearConsole")) {
                event.getChannel().sendMessage("Not yet... sadly");
            }

        }

    }

    public static String detectDadJoke(String message) {
        String[] splitList = new String[]{"I'm", "Im", "i'm", "im"};
        for (int i = 0; i < 2; i++) {
            if (i == 0) { // Beginner level: I'm ____________.
                for (String s : splitList) {
                    if (message.startsWith(s + " ")) {
                        // System.out.println("Passed beginner level test 1.");
                        String[] split = message.split(" ");
                        // System.out.println("Split is " + Arrays.toString(split));
                        if (split.length - 1 <= 3) {
                            // System.out.println("Passed beginner level test 2.");
                            return getAndProcessSubArray(1, split.length, split);
                        }
                    }
                }
            } else { // Intermediate level: Hello, I'm _________.
                for (String s : splitList) {
                    String temp = " " + s + " ";
                    // System.out.println(temp);
                    if (message.contains(temp)) {
                        // System.out.println("Passed intermediate test 1.");
                        String[] split = message.split(temp);
                        // System.out.println("Split1 is " + Arrays.toString(split));
                        if (split.length > 1) {
                            String[] split2 = split[1].split(" ");
                            // System.out.println("Split2 is " + Arrays.toString(split));
                            if (split2.length <= 3) {
                                return getAndProcessSubArray(0, split2.length, split2);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getAndProcessSubArray(int start, int end, String[] array) {
        ArrayList<String> result = new ArrayList<>();
        if (start == end) {
            return array[start];
        }
        for (int i = start; i < end; i++) {
            result.add(array[i]);
            result.add(" ");
        }
        if (result.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (String s : result) {
            builder.append(s);
        }

        return builder.toString();
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static class cloneServerThread extends Thread {
        public void run() {
            try {
                // System.out.println(server.getChannels());
                List<ServerChannel> serverChannelsList = server.getChannels();
                String currentCategory = "";
                String currentThread = "";
                System.out.println("Output folder is the current directory.");
                for (ServerChannel s : serverChannelsList) {
                    String folderName = s.getName() + " - " + s.getType();
                    // System.out.println(folderName);
                    switch (s.getType()) {
                        case CHANNEL_CATEGORY:
                            new File(server.getName() + "/" + folderName).mkdirs();
                            currentCategory = server.getName() + "/" + folderName;
                            continue;
                        case SERVER_TEXT_CHANNEL:
                            if (currentThread.isEmpty()) {
                                new File(server.getName() + "/" + folderName).mkdirs();
                                currentThread = server.getName() + "/" + folderName + "/";
                            } else {
                                new File(currentCategory + "/" + folderName).mkdirs();
                                currentThread = currentCategory + "/" + folderName + "/";
                            }

                            List<Message> messages = ((TextChannel) s).getMessagesAsStream().collect(Collectors.toList());
                            processMessages(messages, currentThread);

                            continue;
                        case SERVER_VOICE_CHANNEL:
                            new File(currentCategory + "/" + folderName).mkdirs();
                            continue;
                        case SERVER_PUBLIC_THREAD:
                            new File(currentThread + "/" + folderName).mkdirs();
                            messages = ((TextChannel) s).getMessagesAsStream().collect(Collectors.toList());
                            processMessages(messages, currentThread);
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

    public static void processMessages(List<Message> messages, String path) throws IOException {
        File file = new File(path + "content.txt");
        if (!file.createNewFile()) {
            file.delete();
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file);
        for (Message message : messages) {
            writer.write(message.getCreationTimestamp().toString().replace("T", " ") + " " + message.getAuthor().getName() + ": " + message.getContent() + "\n");
        }
    }

    public static class userInputThread extends Thread {
        public void run() {
            try {
                while (userInput) {
                    Scanner scanner = new Scanner(System.in);
                    String s = scanner.nextLine();
                    if (!s.isEmpty()) {
                        channel.sendMessage(s);
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception in User Input Thread.");
                e.printStackTrace();
            }
        }
    }
}
