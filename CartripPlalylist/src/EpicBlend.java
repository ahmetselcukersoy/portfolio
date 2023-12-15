import java.util.HashMap;
import java.util.Map;


class EpicBlend {
    int heartacheLimit;
    int roadtripLimit;
    int blissfulLimit;
    int playlistNum;
    Map<Integer, Song> hMap;
    Map<Integer, Song> rMap;
    Map<Integer, Song> bMap;
    Map<Integer, Song> finalMap = new HashMap<>();

    public EpicBlend(int heartacheLimit, int roadtripLimit, int blissfulLimit, int playlistNum) {
        this.heartacheLimit = heartacheLimit;
        this.roadtripLimit = roadtripLimit;
        this.blissfulLimit = blissfulLimit;
        this.playlistNum = playlistNum;
        hMap = new HashMap<>();
        rMap = new HashMap<>();
        bMap = new HashMap<>();
    }

}
