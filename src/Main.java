import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        VoteManager vm = new VoteManager();
        Scanner sc = new Scanner(System.in);

        System.out.println("--- Online Voting System ---");

        while (true) {
            System.out.println("\n1. Register\n2. Login\n3. Exit");
            System.out.print("Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                System.out.print("Enter Username: ");
                String u = sc.nextLine();
                System.out.print("Set Password: ");
                String p = sc.nextLine();
                vm.registerUser(u, p);
            } else if (choice == 2) {
                System.out.print("Username: ");
                String u = sc.nextLine();
                System.out.print("Password: ");
                String p = sc.nextLine();
                User currentUser = vm.login(u, p);

                if (currentUser != null) {
                    System.out.println("Welcome, " + currentUser.getUsername());
                    System.out.print("Enter Candidate Name to Vote: ");
                    String candidate = sc.nextLine();
                    vm.castVote(currentUser, candidate);
                } else {
                    System.out.println("Invalid Credentials!");
                }
            } else {
                System.out.println("Goodbye!");
                break;
            }
        }
    }
}