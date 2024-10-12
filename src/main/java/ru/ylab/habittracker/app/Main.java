package ru.ylab.habittracker.app;

import ru.ylab.habittracker.controllers.*;
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
    private final AdministrationController administrationController;

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
        administrationController = new AdministrationController(usersService);
    }

    public static void main(String[] args) {
        Main main = new Main();
        Scanner scanner = new Scanner(System.in);
        main.createAdmin(main);

        while (true) {
            System.out.print("1. Register\n2. Login\n3. Exit\nChoose an option:");
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
            System.out.println("1. Управление пользователями\n2. Управление привычками\n3. Заблокировать пользователя(Для администратора)\nЛюбая другая кнопка - выйти из аккаунта\nChoose an option:");
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
                case 3:
                    System.out.println("Введи email, который стоит заблокировать: ");
                    String emailBlock = scanner.nextLine();
                    System.out.println(main.administrationController.blockUser("admin@gmail.com", emailBlock));
                    break;
                default:
                    return;
            }
        }
    }

    private boolean userManagement(Main main, Scanner scanner, String email) {
        while (true) {
            System.out.println("1. Обновить пользовательские данные\n2. Обновить пароль\n3. Удалить аккаунт\nЛюбая другая кнопка - вернуться назад\nChoose an option:");
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
            System.out.println("1. Создание привычки\n2. Редактирование привычки\n3. Удаление привычки\n4. Просмотр всех привычек\n5. Просмотр всех привычек по дате создания\n6. Отслеживание выполнения привычки\n7. Статистика и аналитика\nЛюбая другая кнопка - вернуться назад\nChoose an option:");
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
            System.out.println("1. Отметить выполнение привычки\n2. История выполнения привычки\n3. Генерация статистики выполнения привычки за указанный период (день, неделя, месяц)\nЛюбая другая кнопка - вернуться назад\nChoose an option:");
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
            System.out.println("1. Подсчет текущих серий выполнения привычек (streak)\n2. Процент успешного выполнения привычек за определенный период\n3. Формирование отчета для пользователя по прогрессу выполнения\nЛюбая другая кнопка - вернуться назад\nChoose an option: ");
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

    private void createAdmin(Main main) {
        main.authenticationController.signUp(new SignUpRequest("admin", "admin@gmail.com", "admin"));
        main.administrationController.setAdminRole("admin@gmail.com");
    }
}
