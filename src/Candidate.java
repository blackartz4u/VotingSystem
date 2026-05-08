public class Candidate {
    private final int    id;
    private final String name;
    private final String party;
    private int          voteCount;

    public Candidate(int id, String name, String party) {
        this.id        = id;
        this.name      = name;
        this.party     = party;
        this.voteCount = 0;
    }

    // Getters
    public int    getId()        { return id; }
    public String getName()      { return name; }
    public String getParty()     { return party; }
    public int    getVoteCount() { return voteCount; }

    /** Increments this candidate's vote counter by one. */
    public void addVote() { voteCount++; }

    /**
     * Pretty one-line summary used in the candidate list display.
     * Example:  [1]  Alice Johnson        (National Progress Party)   Votes: 4
     */
    public String toDisplayString() {
        return String.format("  [%d]  %-22s %-35s Votes: %d",
                id, name, "(" + party + ")", voteCount);
    }
}