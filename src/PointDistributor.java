import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeMap;

public class PointDistributor {

    static String playlistName;
    static int numberOfSongs;
    static int availablePoints;
    static int zeros;
    static int totalPoints = 0;
    static LinkedList<Song> songs = new LinkedList<Song>();
    static TreeMap<Integer, Rank> rankMap = new TreeMap<>();

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Name of Playlist:");
        playlistName = input.nextLine();
        while (true) {
            try {
                System.out.println("Number of Songs:");
                numberOfSongs = input.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("This is not a number!");
                input.nextLine();
            }
        }
        while (true) {
            try {
                System.out.println("Available Points:");
                availablePoints = input.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("This is not a number!");
                input.nextLine();
            }
        }
        int currentRank = 1;
        for (int i = 0; i < numberOfSongs; i++) {
            System.out.println("Rank: " + (currentRank) + "->");
            songs.add(new Song(i, currentRank, input.next()));
            if (i < numberOfSongs - 1) {
                while (true) {
                    try {
                        System.out.println("Do you want the next song to have the same rank? (y/n)");
                        String rankResponse = input.next();
                        if (rankResponse.equals("y")) {
                            break;
                        } else if (rankResponse.equals("n")) {
                            rankMap.put(currentRank, new Rank(i, currentRank > i ? 1 : i - currentRank + 2));
                            currentRank = currentRank > i ? currentRank + 1 : i + 2;
                            break;
                        } else {
                            System.out.println("Incorrect selection, please try again!");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Incorrect selection, please try again!");
                        input.nextLine();
                    }
                }
            }
        }
        while (true) {
            try {
                System.out.println("Number of songs with zero points:");
                zeros = input.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("This is not a number!");
                input.nextLine();
            }
        }
        input.nextLine();

        assignPoints(zeros);
        createFile();
        input.close();
    }

    private static void assignPoints(int zeros) {
        double totalScore = 0;
        for (int i = 0; i < songs.size(); i++) {
            songs.get(i).setScore(Math.pow(numberOfSongs - songs.get(i).getRank() + 1, 2));
            totalScore += songs.get(i).getScore();
        }
        double multiplier = availablePoints / totalScore;
        for (int i = 0; i < songs.size(); i++) {
            if (songs.size() - i <= zeros) {
                songs.get(i).setPoints(0);
                songs.get(i).setValue(1.0);
                songs.get(i).setRejected(true);
            } else {
                songs.get(i).setPoints((int) Math.floor(multiplier * songs.get(i).getScore()));
                songs.get(i).setValue(Math.abs(songs.get(i).getPoints() - (multiplier * songs.get(i).getScore()) + 1));
                songs.get(i).setRejected(false);
            }
            totalPoints += songs.get(i).getPoints();
        }
        while (totalPoints != availablePoints) {
            deficit(zeros, availablePoints - totalPoints);
        }
    }

    private static void deficit(int zeros, int deficit) {
        if (rankMap.get(1).getCount() == 1) {
            songs.get(0).setPoints((int) (songs.get(0).getPoints() + 1));
            totalPoints++;
            deficit--;
        }
        Comparator<Song> valueComparator = new Comparator<Song>() {
            @Override
            public int compare(Song s1, Song s2) {
                return s1.getValue().compareTo(s2.getValue());
            }
        };
        LinkedList<Song> newOrder = new LinkedList<>(songs);
        Collections.sort(newOrder, valueComparator);
        Integer counterDeficit = deficit;
        for (int i = 0; i < counterDeficit; i++) {
            Boolean lessPoints;
            if (newOrder.get(i).getRank() == 1) {
                lessPoints = true;
            } else {
                Integer higherElement = rankMap.lowerEntry(newOrder.get(i).getRank()).getValue().getId();
                lessPoints = newOrder.get(i).getPoints() < songs.get(higherElement).getPoints();
            }
            if (newOrder.get(i).getRejected() == false && rankMap.get(newOrder.get(i).getRank()).getCount() <= deficit
                    && lessPoints) {
                songs.get(newOrder.get(i).getId()).setPoints(newOrder.get(i).getPoints() + 1);
                deficit--;
                totalPoints++;
            }
        }
    }

    private static void createFile() {
        File fileList = new File(playlistName + ".txt");
        try {
            if (fileList.createNewFile()) {
                System.out.println("File created: " + fileList.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            BufferedWriter fileWrite = new BufferedWriter(new FileWriter(fileList.getAbsolutePath()));
            fileWrite.write("Music League Point Distribution");
            fileWrite.newLine();
            fileWrite.write("--------------------------------");
            fileWrite.newLine();
            fileWrite.write("File Details");
            fileWrite.newLine();
            fileWrite.write("--------------------------------");
            fileWrite.newLine();
            fileWrite.write("Playlist Name: " + playlistName);
            fileWrite.newLine();
            fileWrite.write("Number of Songs: " + numberOfSongs);
            fileWrite.newLine();
            fileWrite.write("Available Points: " + availablePoints);
            fileWrite.newLine();
            fileWrite.write("Number of zeros: " + zeros);
            fileWrite.newLine();
            fileWrite.write("--------------------------------");
            fileWrite.newLine();
            fileWrite.write("Song List");
            fileWrite.newLine();
            fileWrite.write("--------------------------------");
            fileWrite.newLine();
            for (int i = 0; i < songs.size(); i++) {
                fileWrite.write(
                        songs.get(i).getRank() + ". " + songs.get(i).getName() + ": " + songs.get(i).getPoints());
                fileWrite.newLine();
            }
            fileWrite.write("--------------------------------");
            fileWrite.close();
            System.out.println("Successfully wrote to file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
