import java.io.*;
import java.util.*;

public class Project3 {

    Map<Integer, Playlist> playlists;
    Map<Integer, Song> songs;

    // Constructor
    public Project3() {
        playlists = new HashMap<>();
        songs = new HashMap<>();
    }

    // Method to read the song file and populate the songs map
    private void readSongFile(String songFileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(songFileName))) {
            String line = br.readLine();
            Song.numSongs = Integer.parseInt(line);

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                int songId = Integer.parseInt(parts[0]);
                String songName = parts[1];
                int playCount = Integer.parseInt(parts[2]);
                int heartacheScore = Integer.parseInt(parts[3]);
                int roadtripScore = Integer.parseInt(parts[4]);
                int blissfulScore = Integer.parseInt(parts[5]);

                Song song = new Song(songId, songName, playCount, heartacheScore, roadtripScore, blissfulScore);
                songs.put(songId, song);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Method to read the test case file and perform the corresponding actions
    private void readTestCaseFile(String testCaseFileName, StringBuilder result) {
        try (BufferedReader br = new BufferedReader(new FileReader(testCaseFileName))) {
            String line;
            int heartacheLimit, roadtripLimit, blissfulLimit;
            int playlistLimit, playlistNum;

            // Read the limits for categories
            line = br.readLine();
            String[] categoryLimits = line.split(" ");
            playlistLimit = Integer.parseInt(categoryLimits[0]);
            heartacheLimit = Integer.parseInt(categoryLimits[1]);
            roadtripLimit = Integer.parseInt(categoryLimits[2]);
            blissfulLimit = Integer.parseInt(categoryLimits[3]);


            // Read the number of playlists
            line = br.readLine();
            playlistNum = Integer.parseInt(line);
            EpicBlend epicBlend = new EpicBlend(heartacheLimit, roadtripLimit, blissfulLimit, playlistNum);

            // Read each playlist and populate the playlists map
            for (int i = 0; i < playlistNum; i++) {
                line = br.readLine();
                String[] playlistInfo = line.split(" ");
                int playlistId = Integer.parseInt(playlistInfo[0]);

                // Read the song IDs in the playlist
                Set<Song> songIdList = new HashSet<>();
                line = br.readLine();
                if (!Objects.equals(line, "")) {
                    String[] songIds = line.split(" ");
                    for (String id : songIds) {
                        int songId = Integer.parseInt(id);
                        Song tempSong = songs.get(songId);
                        songIdList.add(tempSong);
                    }
                }
                // Create a playlist and add it to the playlists map
                Playlist playlist = new Playlist(playlistId, songIdList, playlistLimit);
                playlists.put(playlistId, playlist);
            }

            // Read the number of events
            line = br.readLine();
            int numEvents = Integer.parseInt(line);

            // Updating blend
            updateBlend(epicBlend);
            // Process each event
            for (int i = 0; i < numEvents; i++) {
                line = br.readLine();
                String[] eventInfo = line.split(" ");

                // Identify the event type and perform the corresponding action
                switch (eventInfo[0]) {
                    case "ADD" -> {
                        int addedSongId = Integer.parseInt(eventInfo[1]);
                        int addedPlaylistId = Integer.parseInt(eventInfo[2]);
                        Song song = songs.get(addedSongId);
                        Playlist pl = playlists.get(addedPlaylistId);
                        pl.addSong(song);
                        updateAndPrintBlend(epicBlend, result);
                    }
                    case "REM" -> {
                        int removedSongId = Integer.parseInt(eventInfo[1]);
                        int removedPlaylistId = Integer.parseInt(eventInfo[2]);
                        Song song = songs.get(removedSongId);
                        Playlist temp_pl = playlists.get(removedPlaylistId);
                        temp_pl.removeSong(song);
                        updateAndPrintBlend(epicBlend, result);
                    }
                    case "ASK" ->
                        printEpicBlend(epicBlend, result);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update the EpicBlend
    public void updateBlend(EpicBlend epicBlend) {

        MaxHeap hFinalHeap = new MaxHeap(Song.numSongs, "h");
        MaxHeap rFinalHeap = new MaxHeap(Song.numSongs, "r");
        MaxHeap bFinalHeap = new MaxHeap(Song.numSongs, "b");
        HashMap<Integer, Song> finalMap = new HashMap<>();
        // clearing maps in every update
        epicBlend.hMap.clear();
        epicBlend.rMap.clear();
        epicBlend.bMap.clear();
        // unique song check to avoid redundant addition
        HashMap<Integer, Boolean> uniqueSongIds = new HashMap<>();

        // choose songs according to playlist limit
        for (int i = 1; i < epicBlend.playlistNum + 1; i++) {
            Playlist playlist = playlists.get(i);
            playlist.updateHeaps();
            for (int j = 0; j < playlist.playlistLimit; j++) {
                Song song = playlist.hMaxHeap.extractMax();
                if (song != null) {
                    hFinalHeap.insert(song);
                }
                song = playlist.rMaxHeap.extractMax();
                if (song != null)
                    rFinalHeap.insert(song);
                song = playlist.bMaxHeap.extractMax();
                if (song != null)
                    bFinalHeap.insert(song);
            }
        }

        // choose songs according to category limit
        for (int i = 0; i < epicBlend.heartacheLimit; i++) {
            Song song = hFinalHeap.extractMax();
            if (song != null) {
                if (!finalMap.containsKey(song.id) && uniqueSongIds.putIfAbsent(song.id, true) == null)
                    finalMap.put(song.id, song);
                if (!epicBlend.hMap.containsKey(song.id))
                    epicBlend.hMap.put(song.id, song);
            }
        }
        for (int i = 0; i < epicBlend.roadtripLimit; i++) {
            Song song = rFinalHeap.extractMax();
            if (song != null) {
                if (!finalMap.containsKey(song.id) && uniqueSongIds.putIfAbsent(song.id, true) == null)
                    finalMap.put(song.id, song);
                if (!epicBlend.rMap.containsKey(song.id))
                    epicBlend.rMap.put(song.id, song);
            }
        }
        for (int i = 0; i < epicBlend.blissfulLimit; i++) {
            Song song = bFinalHeap.extractMax();
            if (song != null) {
                if (!finalMap.containsKey(song.id) && uniqueSongIds.putIfAbsent(song.id, true) == null)
                    finalMap.put(song.id, song);
                if (!epicBlend.bMap.containsKey(song.id))
                    epicBlend.bMap.put(song.id, song);
            }
        }

        // update epicBlend.finalMap
        epicBlend.finalMap = new HashMap<>(finalMap);
    }



    public void updateAndPrintBlend(EpicBlend epicBlend, StringBuilder result) throws IOException {
        Map<Integer, Song> oldhListMap = new HashMap<>(epicBlend.hMap);
        Map<Integer, Song> oldrListMap = new HashMap<>(epicBlend.rMap);
        Map<Integer, Song> oldbListMap = new HashMap<>(epicBlend.bMap);
        // Update the EpicBlend
        updateBlend(epicBlend);
        // get the differences
        int h1 = findNewSong(epicBlend.hMap, oldhListMap);
        int h2 = findNewSong(oldhListMap, epicBlend.hMap);
        int r1 = findNewSong(epicBlend.rMap, oldrListMap);
        int r2 = findNewSong(oldrListMap, epicBlend.rMap);
        int b1 = findNewSong(epicBlend.bMap, oldbListMap);
        int b2 = findNewSong(oldbListMap, epicBlend.bMap);

        // append to result
        result.append(h1).append(" ").append(r1).append(" ").append(b1).
                append(System.lineSeparator()).
                append(h2).append(" ").append(r2).append(" ").append(b2).
                append(System.lineSeparator());
    }


    // find different song
    private int findNewSong(Map<Integer, Song> newList, Map<Integer, Song> oldListMap) {
        for (Map.Entry<Integer, Song> entry : newList.entrySet()) {
            int id = entry.getKey();
            if (!oldListMap.containsKey(id))
                return id; // Found a new song
        }
        return 0; // No new songs found
    }

    // printing the blend
    public void printEpicBlend(EpicBlend epicBlend, StringBuilder result) throws IOException {
        updateBlend(epicBlend);
        MaxHeap descendingHeap = new MaxHeap(epicBlend.finalMap.size(), "c");
        for (Song song : epicBlend.finalMap.values())
            descendingHeap.insert(song);
        while (descendingHeap.size > 0) {
            String id = String.valueOf(descendingHeap.extractMax().id);
            if (descendingHeap.size == 0) {
                result.append(id);
            } else {
                id = id + " ";
                result.append(id);
            }
        }
        result.append("\n");
    }


    // Main method to run the project
    public static void main(String[] args) throws IOException {
        Project3 project3 = new Project3();
        String songFile = args[0];
        String testcaseFile = args[1];
        String outputFile = args[2];
        StringBuilder result = new StringBuilder();

        // Read the song file and test case file
        project3.readSongFile(songFile);
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        project3.readTestCaseFile(testcaseFile, result);
        // use bw only once so code runs faster
        bw.write(String.valueOf(result));
        bw.close();
    }
}
