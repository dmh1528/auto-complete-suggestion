package InterestGroupCassandra;

public class InterestGroupItem implements Comparable<InterestGroupItem> {

    private String id;
    private Double score;

    public InterestGroupItem() {

    }

    public InterestGroupItem(String id, Double score) {
        this.id = id;
        this.score = score;
    }

    @Override
    public int compareTo(InterestGroupItem o) {

        return o.score.compareTo(this.score);
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public Double getScore() {

        return score;
    }

    public void setScore(Double score) {

        this.score = score;
    }

}