import java.util.Collections;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeMap;

public class PointGenerator {

    static int numberOfSongs;
    static int availablePoints;
    static int totalPoints = 0;
    static LinkedList<Song> songs = new LinkedList<Song>();
    static TreeMap<Integer, Rank> rankMap = new TreeMap<>();

    public static void main(String[] args) {
        System.out.println("Enter the number of songs");
        Scanner input = new Scanner(System.in);
        numberOfSongs = input.nextInt();
        System.out.println("Enter the number of available points");
        availablePoints = input.nextInt();
        int currentRank = 1;
        for (int i = 0; i < numberOfSongs; i++) {
            System.out.println("Rank: " + (currentRank) + "->");
            songs.add(new Song(i, currentRank, input.next()));
            if (i < numberOfSongs - 1) {
                boolean done = false;
                do {
                    try {
                        System.out.println("Do you want the next song to have the same rank? (y/n)");
                        System.out.flush();
                        String rankResponse = input.next();
                        if (rankResponse.equals("y")) {
                            done = true;
                        } else if (rankResponse.equals("n")) {
                            rankMap.put(currentRank, new Rank(i, currentRank > i ? 1 : i - currentRank + 2));
                            currentRank = currentRank > i ? currentRank + 1 : i + 2;
                            done = true;
                        } else
                            System.out.println("Incorrect selection, please try again!");
                    } catch (InputMismatchException e) {
                        System.out.println("This is not a letter");
                        input.nextLine();
                    }
                } while (!done);
            }
        }
        int zeros;
        System.out.println("Please enter the number of songs with zero points:");
        zeros = input.nextInt();
        assignPoints(zeros);
        input.close();

        System.out.println("Music League Point Distribution:");
        System.out.println("---------------------");
        for (int i = 0; i < songs.size(); i++) {
            System.out.println(songs.get(i).getName() + ": " + songs.get(i).getPoints());
        }
        System.out.println("---------------------");
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
}
