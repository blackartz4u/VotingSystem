import java.io.*;
import java.util.*;
import java.security.MessageDigest; // For hashing passwords
import java.security.NoSuchAlgorithmException;

public class VoteManager {
    private final String USER_FILE = "users.txt";
    private final String VOTE_FILE = "results.txt";

    //hashing function
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

    public void registerUser(String user, String pass) {
        String hashedPass = hashPassword(pass); //hashes the password
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            bw.write(user + "," + hashedPass + ",false");
            bw.newLine();
            System.out.println("User registered!");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public User login(String username, String password) {
        String hashedInput = hashPassword(password); //hashes the password
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // Compare hashed input with stored hash
                if (parts[0].equals(username) && parts[1].equals(hashedInput)) {
                    return new User(parts[0], parts[1], Boolean.parseBoolean(parts[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("File Error: " + e.getMessage());
        }
        return null;
    }

    public void castVote(User user, String candidate) {
        if (user.hasVoted()) {
            System.out.println("Security Alert: You have already voted!");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(VOTE_FILE, true))) {
            bw.write(candidate);
            bw.newLine();
            updateUserStatus(user.getUsername());
            System.out.println("Vote cast for " + candidate + "! Thank you.");
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
                    lines.add(parts[0] + "," + parts[1] + ",true"); // Flip to true
                } else {
                    lines.add(line);
                }
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(USER_FILE))) {
                for (String l : lines) pw.println(l);
            }
        } catch (IOException e) {
            System.out.println("Error updating status: " + e.getMessage());
        }
    }
}