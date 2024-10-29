package ru.ylab.habittracker.app;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        App app = new App();
        app.start(scanner);
    }
}
