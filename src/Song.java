class Song {
    private Integer id;
    private Integer rank;
    private String name;
    private Double score;
    private Integer points;
    private Double value;
    private Boolean rejected;

    public Song(Integer id, Integer rank, String name) {
        this.id = id;
        this.rank = rank;
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Integer getRank() {
        return this.rank;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getScore() {
        return this.score;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getPoints() {
        return this.points;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return this.value;
    }

    public void setRejected(Boolean rejected) {
        this.rejected = rejected;
    }

    public Boolean getRejected() {
        return this.rejected;
    }
}