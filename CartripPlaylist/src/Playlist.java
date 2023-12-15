import java.util.Set;

class Playlist {
    int id;
    int playlistLimit;
    Set<Song> songs;
    MaxHeap hMaxHeap;
    MaxHeap rMaxHeap;
    MaxHeap bMaxHeap;

    public Playlist(int id, Set<Song> songs, int playlistLimit) {
        this.playlistLimit = playlistLimit;
        this.id = id;
        this.songs = songs;
        for (Song song : songs)
            song.playlistId = id;
    }
    public void addSong(Song song) {
        songs.add(song);
        song.playlistId = id;
        updateHeaps();
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
        song.playlistId = 0;
        updateHeaps();
    }

    public void updateHeaps() {
        int numSongs = this.songs.size();
        hMaxHeap = new MaxHeap(numSongs, "h");
        rMaxHeap = new MaxHeap(numSongs, "r");
        bMaxHeap = new MaxHeap(numSongs, "b");
        for (Song song : this.songs) {
            hMaxHeap.insert(song);
            rMaxHeap.insert(song);
            bMaxHeap.insert(song);
        }
    }


}
