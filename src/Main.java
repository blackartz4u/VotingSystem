import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        VoteManager vm = new VoteManager();
        Scanner sc = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("       Online Voting System             ");
        System.out.println("========================================");

        while (true) {
            System.out.println("\n1. Register");
            System.out.println("2. Login & Vote");
            System.out.println("3. View Live Results");
            System.out.println("4. View Vote Counts Only");
            System.out.println("5. Exit");
            System.out.print("Choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter 1 - 5.");
                continue;
            }

            switch (choice) {

                // -------------------------------------------------------- //
                //  REGISTER                                                  //
                // -------------------------------------------------------- //
                case 1 -> {
                    System.out.print("Enter Username       : ");
                    String username = sc.nextLine().trim();

                    System.out.print("Set Password         : ");
                    String password = sc.nextLine();

                    // --- Age ---
                    int age = 0;
                    while (true) {
                        System.out.print("Enter Age            : ");
                        try {
                            age = Integer.parseInt(sc.nextLine().trim());
                            if (age <= 0 || age > 130) {
                                System.out.println("Please enter a valid age (1-130).");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Age must be a number.");
                        }
                    }

                    // --- Gender ---
                    String gender = "";
                    while (true) {
                        System.out.println("Select Gender:");
                        System.out.println("  1. Male");
                        System.out.println("  2. Female");
                        System.out.println("  3. Other");
                        System.out.print("Choice: ");
                        String gChoice = sc.nextLine().trim();
                        switch (gChoice) {
                            case "1" -> gender = "Male";
                            case "2" -> gender = "Female";
                            case "3" -> gender = "Other";
                            default  -> { System.out.println("Please choose 1, 2, or 3."); continue; }
                        }
                        break;
                    }

                    // --- Disability ---
                    boolean disabled = false;
                    while (true) {
                        System.out.print("Are you a person with a disability? (yes/no): ");
                        String dChoice = sc.nextLine().trim().toLowerCase();
                        if      (dChoice.equals("yes")) { disabled = true;  break; }
                        else if (dChoice.equals("no"))  { disabled = false; break; }
                        else { System.out.println("Please type 'yes' or 'no'."); }
                    }

                    vm.registerUser(username, password, age, gender, disabled);
                }

                // -------------------------------------------------------- //
                //  LOGIN & VOTE                                              //
                // -------------------------------------------------------- //
                case 2 -> {
                    System.out.print("Username : ");
                    String username = sc.nextLine().trim();
                    System.out.print("Password : ");
                    String password = sc.nextLine();

                    User currentUser = vm.login(username, password);

                    if (currentUser == null) {
                        System.out.println("Invalid credentials. Please try again.");
                        break;
                    }

                    System.out.println("\nWelcome, " + currentUser.getUsername() + "!");
                    System.out.println("Assigned booth : " + currentUser.getBoothAssignment());

                    if (currentUser.hasVoted()) {
                        System.out.println("Our records show you have already cast your vote.");
                        break;
                    }

                    // Show ballot with current vote counts
                    vm.displayCandidates();

                    // Candidate selection by number
                    int candidateId = 0;
                    while (true) {
                        System.out.print("\nEnter candidate number to vote for: ");
                        try {
                            candidateId = Integer.parseInt(sc.nextLine().trim());
                            if (vm.getCandidateById(candidateId).isEmpty()) {
                                System.out.println("No candidate with that number. Please try again.");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a valid number.");
                        }
                    }

                    // Confirm before submitting
                    String chosenName = vm.getCandidateById(candidateId).get().getName();
                    System.out.print("Confirm vote for \"" + chosenName + "\"? (yes/no): ");
                    String confirm = sc.nextLine().trim().toLowerCase();
                    if (!confirm.equals("yes")) {
                        System.out.println("Vote cancelled. You may log in again to vote.");
                        break;
                    }

                    vm.castVote(currentUser, candidateId);
                }

                // -------------------------------------------------------- //
                //  VIEW LIVE RESULTS                                         //
                // -------------------------------------------------------- //
                case 3 -> vm.displayResults();

                // -------------------------------------------------------- //
                //  VIEW VOTE COUNTS ONLY                                     //
                // -------------------------------------------------------- //
                case 4 -> {
                    System.out.println("\n  +================================+");
                    System.out.println("  |       VOTE COUNTS PER CANDIDATE |");
                    System.out.println("  +================================+");
                    for (Candidate c : vm.getCandidates()) {
                        System.out.printf("  %-22s : %d vote(s)%n",
                                c.getName(), c.getVoteCount());
                    }
                    System.out.println("  +================================+");
                }

                // -------------------------------------------------------- //
                //  EXIT                                                      //
                // -------------------------------------------------------- //
                case 5 -> {
                    System.out.println("Thank you for using the Online Voting System. Goodbye!");
                    sc.close();
                    return;
                }

                default -> System.out.println("Invalid choice. Please enter 1 - 5.");
            }
        }
    }
}