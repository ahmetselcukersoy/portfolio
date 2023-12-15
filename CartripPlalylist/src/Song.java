import java.util.Objects;

class Song {
    int id;
    String name;
    int playCount;
    int heartacheScore;
    int roadtripScore;
    int blissfulScore;
    int playlistId = 0;
    public static int numSongs;


    public Song(int id, String name, int playCount, int heartacheScore, int roadtripScore, int blissfulScore) {
        this.id = id;
        this.name = name;
        this.playCount = playCount;
        this.heartacheScore = heartacheScore;
        this.roadtripScore = roadtripScore;
        this.blissfulScore = blissfulScore;
    }

    // getting scores according to given category
    public int getScore(String category) {
        if (Objects.equals(category, "h"))
            return this.heartacheScore;
        if (Objects.equals(category, "r"))
            return this.roadtripScore;
        if (Objects.equals(category, "b"))
            return this.blissfulScore;
        if (Objects.equals(category, "c"))
            return this.playCount;
        return 0;
    }
}
