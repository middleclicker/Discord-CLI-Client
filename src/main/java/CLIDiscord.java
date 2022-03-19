import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Scanner;

public class CLIDiscord {
    public static boolean userInput = false;
    public static boolean displayMessages = false;
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

            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }

            if (event.getMessageContent().equalsIgnoreCase("!clearConsole")) {
                System.out.println("Not yet... sadly");
            }
        }

    }

    public static class userInputThread extends Thread {
        public void run() {
            try {
                while (userInput) {
                    Scanner scanner = new Scanner(System.in);
                    String s = scanner.nextLine();
                    if (!s.isEmpty()) {
                        if (displayMessages) {
                            System.out.print('\r');
                            System.out.print(botName + ": ");
                        }
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
