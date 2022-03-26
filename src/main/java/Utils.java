import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
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
        writer.flush();
        writer.close();
    }

    public static boolean checkNumberInputs(String message, int targetNumInputs, TextChannel channel) {
        String[] splitMsg = message.split(" ");
        if (splitMsg.length != targetNumInputs+1) {
            channel.sendMessage("Heyo bucko, " + targetNumInputs + " inputs please.");
            return false;
        } else {
            for (int i = 1; i <= targetNumInputs; i++) {
                if (!Utils.isNumeric(splitMsg[i])) {
                    channel.sendMessage("Imagine inputting something which isn't even a number lmfao");
                    return false;
                }
            }
        }
        return true;
    }

    public static String generateL(int n) {
        StringBuilder sb = new StringBuilder();
        sb.append("```");
        for (int i = 0; i < n; i++) {
            if (i == n-1) {
                sb.append('|');
                continue;
            }
            sb.append("|\n");
        }
        for (int i = 0; i < n; i++) {
            sb.append('_');
        }
        sb.append("```");

        return sb.toString();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void deleteDirectory(File directory) {
        if(directory.isDirectory()) {
            File[] files = directory.listFiles();
            if(files != null) {
                for(File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
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

    // TODO: add penis database tracker
    public static BoolStringPair generatePenis() {
        StringBuilder stringBuilder = new StringBuilder().append(8);
        int j = Utils.getRandomNumber(1, 10);

        for (int i = 0; i < j; i++) {
            stringBuilder.append('=');
        }
        stringBuilder
                .append(')')
                .append("\n\n")
                .append("Congradulations, your penis is ")
                .append(j)
                .append('/')
                .append("10");

        BoolStringPair pair = new BoolStringPair(false, stringBuilder.toString());

        if (j == 10) {
            pair.i = true;
        }

        return pair;
    }

    public static String detectDadJoke(String message) {
        String[] splitList = new String[]{"I'm", "Im", "i'm", "im"};
        for (int i = 0; i < 2; i++) {
            if (i == 0) { // I'm ____________.
                for (String s : splitList) {
                    if (message.startsWith(s + " ")) {
                        String[] split = message.split(" ");
                        if (split.length - 1 <= 3) {
                            return Utils.getAndProcessSubArray(1, split.length, split);
                        }
                    }
                }
            } else { // Hello, I'm _________.
                for (String s : splitList) {
                    String temp = " " + s + " ";
                    if (message.contains(temp)) {
                        String[] split = message.split(temp);
                        if (split.length > 1) {
                            String[] split2 = split[1].split(" ");
                            if (split2.length <= 3) {
                                return Utils.getAndProcessSubArray(0, split2.length, split2);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
