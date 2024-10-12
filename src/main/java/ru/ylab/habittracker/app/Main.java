package ru.ylab.habittracker.app;

import ru.ylab.habittracker.controllers.AuthenticationController;
import ru.ylab.habittracker.controllers.HabitCompletionController;
import ru.ylab.habittracker.controllers.HabitsController;
import ru.ylab.habittracker.controllers.UsersController;
import ru.ylab.habittracker.dto.*;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.*;
import ru.ylab.habittracker.services.*;
import ru.ylab.habittracker.utils.Frequency;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private final AuthenticationController authenticationController;
    private final UsersController usersController;
    private final HabitsController habitsController;
    private final HabitCompletionController habitCompletionController;

    public Main() {
        UsersRepository usersRepository = new UsersRepositoryImpl();
        HabitsRepository habitsRepository = new HabitsRepositoryImpl();
        HabitCompletionRepository habitCompletionRepository = new HabitCompletionRepositoryImpl();

        UsersService usersService = new UsersServiceImpl(usersRepository, habitsRepository);
        AuthenticationService authenticationService = new AuthenticationService(usersService, usersRepository);
        HabitsService habitsService = new HabitsServiceImpl(usersRepository, habitsRepository);
        HabitCompletionService habitCompletionService = new HabitCompletionServiceImpl(habitsRepository, habitCompletionRepository);

        usersController = new UsersController(usersService);
        authenticationController = new AuthenticationController(authenticationService);
        habitsController = new HabitsController(habitsService);
        habitCompletionController = new HabitCompletionController(habitCompletionService);
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
                    if (signInResponse.isSuccess()) {
                        main.workingInsideAnAccount(main, scanner, loginEmail);
                    }
                    break;
                case 3:
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
                    break;
            }
        }
    }

    public AuthenticationController getAuthenticationController() {
        return authenticationController;
    }

    public UsersController getUsersController() {
        return usersController;
    }

    public HabitsController getHabitsController() {
        return habitsController;
    }

    public HabitCompletionController getHabitCompletionController() {
        return habitCompletionController;
    }

    private void workingInsideAnAccount(Main main, Scanner scanner, String email) {
        while (true) {
            System.out.println();
            System.out.println("1. Управление пользователями");
            System.out.println("2. Управление привычками");
            System.out.println("Любая другая кнопка - выйти из аккаунта");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    if (!userManagement(main, scanner, email)) {
                        return;
                    }
                    break;
                case 2:
                    managingHabits(main, scanner, email);
                    break;
                default:
                    return;
            }
        }
    }

    private boolean userManagement(Main main, Scanner scanner, String email) {
        while (true) {
            System.out.println();
            System.out.println("1. Обновить пользовательские данные");
            System.out.println("2. Обновить пароль");
            System.out.println("3. Удалить аккаунт");
            System.out.println("Любая другая кнопка - вернуться назад");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    System.out.println("Enter name: ");
                    String name = scanner.nextLine();

                    System.out.println("Enter password: ");
                    String password = scanner.nextLine();

                    System.out.println(usersController.updatingTheUserProfile(new User(null, name, email, password)));
                    break;
                case 2:
                    System.out.println("Enter password: ");
                    String forgotPassword = scanner.nextLine();

                    System.out.println(usersController.forgotPassword(email, forgotPassword));
                    break;
                case 3:
                    usersController.deleteUserByEmail(email);
                    System.out.println("User deleted!");
                    return false;
                default:
                    return true;
            }
        }
    }

    private void managingHabits(Main main, Scanner scanner, String email) {
        while (true) {
            System.out.println();
            System.out.println("1. Создание привычки");
            System.out.println("2. Редактирование привычки");
            System.out.println("3. Удаление привычки");
            System.out.println("4. Просмотр всех привычек");
            System.out.println("5. Просмотр всех привычек по дате создания");
            System.out.println("6. Отслеживание выполнения привычки");
            System.out.println("7. Статистика и аналитика");
            System.out.println("Любая другая кнопка - вернуться назад");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    System.out.println("Enter habit name: ");
                    String name = scanner.nextLine();

                    System.out.println("Enter habit description: ");
                    String description = scanner.nextLine();

                    System.out.println("Enter habit frequency (daily/weekly):");
                    Frequency frequency;
                    try {
                        frequency = Frequency.fromString(scanner.nextLine());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid frequency");
                        continue;
                    }
                    System.out.println(main.habitsController.create(new Habit(null, name, description, frequency, email)));
                    break;
                case 2:
                    System.out.println("Enter habit id: ");
                    Long updateId = scanner.nextLong();
                    scanner.nextLine();
                    System.out.println("Enter habit name: ");
                    String updateName = scanner.nextLine();

                    System.out.println("Enter habit description: ");
                    String updateDescription = scanner.nextLine();

                    System.out.println(main.habitsController.update(new Habit(updateId, updateName, updateDescription, null, email)));
                    break;
                case 3:
                    System.out.println("Enter habit id: ");
                    Long id = scanner.nextLong();
                    habitsController.delete(id);
                    break;
                case 4:
                    System.out.println(habitsController.findAllUserHabitsByEmail(email));
                    break;
                case 5:
                    System.out.println("Введи данные в формате: \"yyyy-MM-dd\"");
                    try {
                        LocalDate localDate = LocalDate.parse(scanner.nextLine());
                        System.out.println(habitsController.findAllUserHabitsByEmailAndDate(email, localDate));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid date format");
                    }
                    break;
                case 6:
                    trackingTheFulfillmentOfHabits(main, scanner);
                    break;
                case 7:
                    statisticsAndAnalyticsSearch(main, scanner);
                    break;
                default:
                    return;
            }
        }
    }

    private void trackingTheFulfillmentOfHabits(Main main, Scanner scanner) {
        while (true) {
            System.out.println();
            System.out.println("1. Отметить выполнение привычки");
            System.out.println("2. История выполнения привычки");
            System.out.println("3. Генерация статистики выполнения привычки за указанный период(день, неделя, месяц)");
            System.out.println("Любая другая кнопка - вернуться назад");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    System.out.println("Enter habit id: ");
                    main.habitCompletionController.markCompletion(scanner.nextLong());
                    scanner.nextLine();
                    break;
                case 2:
                    System.out.println("Enter habit id: ");
                    System.out.println(main.habitCompletionController.showTheHistory(scanner.nextLong()));
                    scanner.nextLine();

                    break;
                case 3:
                    System.out.println("Enter habit id: ");
                    Long id = scanner.nextLong();
                    scanner.nextLine();
                    System.out.println("Enter the period for generating habit statistics (choose one: day, week, or month):");
                    System.out.println(main.habitCompletionController.getStatistics(id, scanner.nextLine()));
                    break;
                default:
                    return;
            }
        }
    }

    private void statisticsAndAnalyticsSearch(Main main, Scanner scanner) {
        while (true) {
            System.out.println();
            System.out.println("1. Подсчет текущих серий выполнения привычек (streak)");
            System.out.println("2. Процент успешного выполнения привычек за определенный период");
            System.out.println("3. Формирование отчета для пользователя по прогрессу выполнения");
            System.out.println("Любая другая кнопка - вернуться назад");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    System.out.println("Enter habit id: ");
                    System.out.println(main.habitCompletionController.calculateCurrentStreak(scanner.nextLong(), LocalDate.now()));
                    scanner.nextLine();
                    break;
                case 2:
                    System.out.println("Enter habit id: ");
                    Long id = scanner.nextLong();
                    scanner.nextLine();
                    try {
                        System.out.println("Введи дату начала в формате: \"yyyy-MM-dd\"");
                        LocalDate localDateStart = LocalDate.parse(scanner.nextLine());
                        System.out.println("Введи дату конца в формате: \"yyyy-MM-dd\"");
                        LocalDate localDateEnd = LocalDate.parse(scanner.nextLine());
                        System.out.println(main.habitCompletionController.calculateCompletionPercentage(id, localDateStart, localDateEnd));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid date format");
                    }
                    break;
                case 3:
                    try {
                        System.out.println("Enter habit id: ");
                        Long generateReportId = scanner.nextLong();
                        scanner.nextLine();
                        System.out.println("Введи дату начала в формате: \"yyyy-MM-dd\"");
                        LocalDate localDateStart = LocalDate.parse(scanner.nextLine());
                        LocalDate localDateEnd = LocalDate.parse(scanner.nextLine());
                        System.out.println(main.habitCompletionController.calculateCompletionPercentage(generateReportId, localDateStart, localDateEnd));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid date format");
                    }
                    break;
                default:
                    return;
            }
        }
    }
}
