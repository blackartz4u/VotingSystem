public class User {
    private String username;
    private String password;
    private boolean hasVoted;
    private int age;
    private String gender;
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
    // getters
    public String getUsername() { return username; }
    public String getPassword()  { return password; }
    public boolean hasVoted()    { return hasVoted; }
    public int getAge()          { return age; }
    public String getGender()    { return gender; }
    public boolean isDisabled()  { return disabled; }

    // setters
    public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }

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
        return username + "," + password + "," + hasVoted + ","
                + age + "," + gender + "," + disabled;
    }
}