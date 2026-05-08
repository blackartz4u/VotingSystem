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

    // getters
    public int    getId()        { return id; }
    public String getName()      { return name; }
    public String getParty()     { return party; }
    public int    getVoteCount() { return voteCount; }

    public void addVote() { voteCount++; }

    public String toDisplayString() {
        return String.format("  [%d]  %-22s %-35s Votes: %d",
                id, name, "(" + party + ")", voteCount);
    }
}