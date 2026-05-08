public class User {
    private String username;
    private String password;
    private boolean hasVoted;
    private int age;
    private String gender;    // "Male", "Female", "Other"
    private boolean disabled;

    public User(String username, String password, boolean hasVoted,
                int age, String gender, boolean disabled) {
        this.username = username;
        this.password = password;
        this.hasVoted = hasVoted;
        this.age = age;
        this.gender = gender;
        this.disabled = disabled;
    }

    // Getters
    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public boolean hasVoted()    { return hasVoted; }
    public int getAge()          { return age; }
    public String getGender()    { return gender; }
    public boolean isDisabled()  { return disabled; }

    // Setters
    public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }

    /**
     * Determines which of the 9 booths this voter is assigned to.
     *
     * Priority:
     *   1. Elderly (age >= 70)  → "Elderly – <Gender> Booth"
     *   2. Disabled             → "Disabled – <Gender> Booth"
     *   3. General              → "General – <Gender> Booth"
     */
    public String getBoothAssignment() {
        String category;
        if (age >= 70) {
            category = "Elderly";
        } else if (disabled) {
            category = "Disabled";
        } else {
            category = "General";
        }
        return category + " – " + gender + " Booth";
    }

    @Override
    public String toString() {
        // CSV format stored in users.txt:
        // username,hashedPassword,hasVoted,age,gender,disabled
        return username + "," + password + "," + hasVoted + ","
                + age + "," + gender + "," + disabled;
    }
}