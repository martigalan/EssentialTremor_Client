package pojos;

import java.util.Scanner;

public class UserInput {
    private static Scanner scanner = new Scanner(System.in);

    public static String getString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim(); // Eliminar espacios en blanco
            if (input.isEmpty()) {
                System.out.println("---NOT A VALID INPUT, PLEASE TRY AGAIN...");
            }
        } while (input.isEmpty()); // Repetir hasta que se ingrese algo
        return input;
    }

    public static int getInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("---INVALID INPUT, PLEASE ENTER A NUMBER");
            scanner.next(); // Limpiar la entrada incorrecta
        }
        return scanner.nextInt();
    }

    public static double getDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.println("---INVALID INPUT, PLEASE ENTER A NUMBER");
            scanner.next(); // Limpiar la entrada incorrecta
        }
        return scanner.nextDouble();
    }

    public static void close() {
        scanner.close();
    }
    public static int getIntWithValidation(int min, int max) {
        int number = -1;
        while (number < min || number > max) {
            number = getInt("Please enter a number between " + min + " and " + max + ": ");
            if (number < min || number > max) {
                System.out.println("--- INVALID OPTION, PLEASE TRY AGAIN...");
            }
        }
        return number;
    }
}

