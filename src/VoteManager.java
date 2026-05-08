import java.io.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VoteManager {
    private final String USER_FILE = "users.txt";
    private final String VOTE_FILE = "results.txt";

    // ------------------------------------------------------------------ //
    //  Pre-loaded Candidates                                               //
    //  To add or remove candidates, edit this list only.                  //
    // ------------------------------------------------------------------ //

    private final List<Candidate> candidates = new ArrayList<>(Arrays.asList(
            new Candidate(1, "Alice Johnson",   "National Progress Party"),
            new Candidate(2, "Bob Martinez",    "United Citizens Front"),
            new Candidate(3, "Clara Singh",     "Green Future Alliance"),
            new Candidate(4, "David Chen",      "Liberty First Movement"),
            new Candidate(5, "Eva Okafor",      "People's Democratic Union")
    ));

    // ------------------------------------------------------------------ //
    //  Constructor — re-hydrate vote counts from results.txt on startup   //
    // ------------------------------------------------------------------ //

    public VoteManager() {
        loadVoteCounts();
    }

    /**
     * Reads results.txt and increments each candidate's in-memory counter
     * so vote tallies survive a program restart.
     *
     * Line format in results.txt:  candidateName,boothAssignment
     */
    private void loadVoteCounts() {
        File file = new File(VOTE_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", 2);
                String name = parts[0].trim();
                getCandidateByName(name).ifPresent(Candidate::addVote);
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load existing vote counts: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ //
    //  Candidate helpers                                                   //
    // ------------------------------------------------------------------ //

    /** Returns an unmodifiable view of the candidate list. */
    public List<Candidate> getCandidates() {
        return Collections.unmodifiableList(candidates);
    }

    /** Looks up a candidate by their menu number (1-based). */
    public Optional<Candidate> getCandidateById(int id) {
        return candidates.stream().filter(c -> c.getId() == id).findFirst();
    }

    /** Looks up a candidate by name (case-insensitive). */
    public Optional<Candidate> getCandidateByName(String name) {
        return candidates.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /** Prints the full candidate roster with live vote counts. */
    public void displayCandidates() {
        System.out.println("\n  +================================================================+");
        System.out.println("  |                    CANDIDATES ON BALLOT                       |");
        System.out.println("  +================================================================+");
        for (Candidate c : candidates) {
            System.out.println(c.toDisplayString());
        }
        System.out.println("  +================================================================+");
    }

    /** Prints a full vote-count summary, sorted by votes descending. */
    public void displayResults() {
        List<Candidate> sorted = new ArrayList<>(candidates);
        sorted.sort((a, b) -> b.getVoteCount() - a.getVoteCount());

        int totalVotes = sorted.stream().mapToInt(Candidate::getVoteCount).sum();

        System.out.println("\n  +================================================================+");
        System.out.println("  |                      LIVE VOTE TALLY                          |");
        System.out.println("  +================================================================+");
        for (Candidate c : sorted) {
            double pct = (totalVotes == 0) ? 0.0
                    : (c.getVoteCount() * 100.0 / totalVotes);
            System.out.printf("  %-22s | %3d votes | %5.1f%%%n",
                    c.getName(), c.getVoteCount(), pct);
        }
        System.out.println("  ----------------------------------------------------------------");
        System.out.printf("  Total votes cast: %d%n", totalVotes);
        System.out.println("  +================================================================+");
    }

    // ------------------------------------------------------------------ //
    //  Password hashing                                                    //
    // ------------------------------------------------------------------ //

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found!", e);
        }
    }

    // ------------------------------------------------------------------ //
    //  Registration                                                        //
    // ------------------------------------------------------------------ //

    public void registerUser(String username, String password,
                             int age, String gender, boolean disabled) {

        if (age < 18) {
            System.out.println("Registration denied: You must be at least 18 years old to vote.");
            return;
        }

        if (userExists(username)) {
            System.out.println("Registration failed: Username \"" + username + "\" is already taken.");
            return;
        }

        String hashedPass = hashPassword(password);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            bw.write(username + "," + hashedPass + ",false,"
                    + age + "," + gender + "," + disabled);
            bw.newLine();
            System.out.println("Registration successful! Welcome, " + username + ".");
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    private boolean userExists(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(username)) return true;
            }
        } catch (IOException ignored) {}
        return false;
    }

    // ------------------------------------------------------------------ //
    //  Login                                                               //
    // ------------------------------------------------------------------ //

    public User login(String username, String password) {
        String hashedInput = hashPassword(password);

        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 6) continue;

                if (parts[0].equals(username) && parts[1].equals(hashedInput)) {
                    return new User(parts[0], parts[1],
                            Boolean.parseBoolean(parts[2]),
                            Integer.parseInt(parts[3]),
                            parts[4],
                            Boolean.parseBoolean(parts[5]));
                }
            }
        } catch (IOException e) {
            System.out.println("File Error: " + e.getMessage());
        }
        return null;
    }

    // ------------------------------------------------------------------ //
    //  Voting                                                              //
    // ------------------------------------------------------------------ //

    /**
     * Casts a vote for the candidate selected by ID number.
     * Updates the in-memory counter immediately and persists to results.txt.
     */
    public void castVote(User user, int candidateId) {
        if (user.hasVoted()) {
            System.out.println("Security Alert: You have already voted!");
            return;
        }

        Optional<Candidate> opt = getCandidateById(candidateId);
        if (opt.isEmpty()) {
            System.out.println("Invalid candidate selection.");
            return;
        }

        Candidate chosen = opt.get();
        String booth = user.getBoothAssignment();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(VOTE_FILE, true))) {
            bw.write(chosen.getName() + "," + booth);
            bw.newLine();

            chosen.addVote();   // update in-memory counter immediately
            updateUserStatus(user.getUsername());

            System.out.println("\nVote cast for \"" + chosen.getName()
                    + "\" (" + chosen.getParty() + ")! Thank you.");
            System.out.println("Recorded at booth   : " + booth);
            System.out.println("Running total       : "
                    + chosen.getVoteCount() + " vote(s) for " + chosen.getName());
        } catch (IOException e) {
            System.out.println("Voting failed: " + e.getMessage());
        }
    }

    private void updateUserStatus(String username) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(username + ",")) {
                    String[] parts = line.split(",");
                    lines.add(parts[0] + "," + parts[1] + ",true,"
                            + parts[3] + "," + parts[4] + "," + parts[5]);
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user file: " + e.getMessage());
            return;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(USER_FILE))) {
            for (String l : lines) pw.println(l);
        } catch (IOException e) {
            System.out.println("Error updating user status: " + e.getMessage());
        }
    }
}