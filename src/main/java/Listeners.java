import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;

import java.util.Optional;

public class Listeners {
    public static class MessageListener implements MessageCreateListener {
        @Override
        public void onMessageCreate(MessageCreateEvent event) {
            String message = event.getMessageContent();

            if (CLIDiscord.displayMessages) {
                CLIDiscord.currentText = event.getMessageAuthor().getDisplayName() + ": " + message;
                System.out.println(CLIDiscord.currentText);
            }

            if (message.equalsIgnoreCase("!toggleUserInput")) {
                CLIDiscord.userInput = !CLIDiscord.userInput;
                CLIDiscord.channel = event.getChannel();

                Threads.userInputThread object = new Threads.userInputThread();
                if (CLIDiscord.userInput) {
                    System.out.println("Enabled user input.");
                    object.start();
                } else {
                    object.stop();
                    System.out.println("Disabled user input.");
                }
            }

            if (message.equalsIgnoreCase("!toggleDisplayMessages")) {
                CLIDiscord.displayMessages = !CLIDiscord.displayMessages;
            }

            if (message.equalsIgnoreCase("!toggleChannelWatch")) {
                CLIDiscord.channelWatch = !CLIDiscord.channelWatch;
                CLIDiscord.server = event.getServer().get();

                // event.getChannel().sendMessage("W.I.P.");
                Threads.activeCloningDetectionThread object = new Threads.activeCloningDetectionThread();
                Threads.fileSystemWatcherThread object2 = new Threads.fileSystemWatcherThread();
                if (CLIDiscord.channelWatch) {
                    object.start();
                    object2.start();
                }
            }

            // Clone the server to local folders
            if (message.equalsIgnoreCase("!cloneServer")) {
                CLIDiscord.server = event.getServer().get();

                Threads.cloneServerThread object = new Threads.cloneServerThread();
                object.start();
            }

            if (message.equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("I'm alive mf now stop pinging me.");
            }

            if (message.equalsIgnoreCase("!help")) {
                event.getChannel().sendMessage("Fuck off, here are your shitty commands: toggleUserInput, toggleChannelWatch, toggleDisplayMessages, cloneServer, ping, help, ratio, cope, penis, amongus, fortniteBurger, clearConsole");
            }

            if (message.equalsIgnoreCase("!ratio")) {
                event.getChannel().sendMessage("Reddit fag? Heres your ratio anyways: " + Utils.getRandomNumber(1, 100) + "/" + Utils.getRandomNumber(1, 100));
            }

            if (message.equalsIgnoreCase("!cope")) {
                event.getChannel().sendMessage("Cope.");
            }

            if (message.equalsIgnoreCase("!penis")) {
                BoolStringPair pair = Utils.generatePenis();
                event.getChannel().sendMessage(pair.j);
            }

            if (message.equalsIgnoreCase("!amongus")) {
                event.getChannel().sendMessage("https://images-ext-1.discordapp.net/external/T0ud4M0NIAS4lhKig0WYa3oFt7DiynfFZyuMlFpzimM/https/media.discordapp.net/attachments/799672934255951934/851081611734024232/received_173201521360551.gif?width=720&height=51");
            }

            if (Utils.detectDadJoke(message) != null && event.getMessageAuthor().getId() != CLIDiscord.bot.getId()) {
                double randomNumber = Math.random();
                if (randomNumber < 0.5) {
                    event.getChannel().sendMessage("hi " + Utils.detectDadJoke(message) + "\n" + "dad's around here somewhere");
                } else {
                    event.getChannel().sendMessage("hello " + Utils.detectDadJoke(message) + "\n" + "dad's around here somewhere");
                }

            }

            if (message.equalsIgnoreCase("!fortniteBurger")) {
                event.getChannel().sendMessage("https://i.kym-cdn.com/photos/images/original/001/648/206/ea1.jpg");
            }

            if (message.equalsIgnoreCase("!clearConsole")) {
                event.getChannel().sendMessage("Wont fucking work because IntelliJ console is weird.");
                System.out.print("\033[H\033[2J");
            }
        }
    }

    public static class MemberJoinListener implements ServerMemberJoinListener {
        @Override
        public void onServerMemberJoin(ServerMemberJoinEvent event) {
            Optional<TextChannel> channel = event.getApi().getTextChannelById("904990167918600225");
            channel.ifPresent(textChannel -> textChannel.sendMessage("Welcome to the server, " + event.getUser().getMentionTag() + "!"));
        }
    }
}
