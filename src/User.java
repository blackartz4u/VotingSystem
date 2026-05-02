public class User {
    private String username;
    private String password; //hashed password
    private boolean hasVoted;

    public User(String username, String password, boolean hasVoted) {
        this.username = username;
        this.password = password; //hashed password
        this.hasVoted = hasVoted;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean hasVoted() { return hasVoted; }
    public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }

    @Override
    public String toString() {
        return username + "," + password + "," + hasVoted;
    }
}