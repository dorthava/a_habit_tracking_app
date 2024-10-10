package ru.ylab.habittracker.app;

import ru.ylab.habittracker.controllers.AuthenticationController;
import ru.ylab.habittracker.controllers.UsersController;
import ru.ylab.habittracker.dto.SignInRequest;
import ru.ylab.habittracker.dto.SignInResponse;
import ru.ylab.habittracker.dto.SignUpRequest;
import ru.ylab.habittracker.dto.SignUpResponse;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.repositories.UsersRepositoryImpl;
import ru.ylab.habittracker.services.AuthenticationService;
import ru.ylab.habittracker.services.UsersService;
import ru.ylab.habittracker.services.UsersServiceImpl;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private final AuthenticationController authenticationController;
    private final UsersController usersController;

    public Main() {
        HashMap<String, User> dataSource = new HashMap<>();
        UsersRepository usersRepository = new UsersRepositoryImpl(dataSource);
        UsersService usersService = new UsersServiceImpl(usersRepository);
        AuthenticationService authenticationService = new AuthenticationService(usersService, usersRepository);

        usersController = new UsersController(usersService);
        authenticationController = new AuthenticationController(authenticationService);
    }
    public static void main(String[] args) {
        Main main = new Main();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    System.out.println("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.println("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.println("Enter password: ");
                    String password = scanner.nextLine();

                    SignUpResponse signUpResponse = main.authenticationController.
                            signUp(new SignUpRequest(name, email, password));
                    System.out.println(signUpResponse.toString());

                    break;
                case 2:
                    System.out.println("Enter email: ");
                    String loginEmail = scanner.nextLine();
                    System.out.println("Enter password: ");
                    String loginPassword = scanner.nextLine();
                    SignInResponse signInResponse = main.authenticationController.
                            signIn(new SignInRequest(loginEmail, loginPassword));
                    System.out.println(signInResponse.toString());

                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
                    break;
            }
        }
    }
}
