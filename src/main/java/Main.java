import processing.Loader;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input a query for which you want to download pictures:");
        String query = scanner.nextLine();
        System.out.println("Input a number of pictures you need:");
        int pagesNeeded = Integer.parseInt(scanner.nextLine());

        Loader loader = new Loader(query, pagesNeeded);
        loader.parseAndLoad();

        System.out.println("Images are downloaded!");
    }
}
