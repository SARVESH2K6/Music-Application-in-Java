package PROJECT;

import java.sql.*;
import java.util.*;

public class PlaylistService extends Database implements Utilities {
    public PlaylistService() throws SQLException {
        super();
    }
    public void createPlaylist(String playlistName, String userName) throws SQLException {
        // Get user ID from the users table
        String getUserIdQuery = "SELECT id FROM users WHERE name = ?";
        PreparedStatement pstmt = conn.prepareStatement(getUserIdQuery);
        pstmt.setString(1, userName);
        ResultSet rs = pstmt.executeQuery();
    
        if (!rs.next()) {
            System.out.println(RED + "\t\t\tUSER NOT FOUND." + RESET);
            return;
        }
    
        int userId = rs.getInt("id");
    
        // Insert new playlist
        String insertPlaylistQuery = "INSERT INTO playlists (name, user_id) VALUES (?, ?)";
        pstmt = conn.prepareStatement(insertPlaylistQuery);
        pstmt.setString(1, playlistName);
        pstmt.setInt(2, userId);
        pstmt.executeUpdate();
        
        System.out.println(GREEN + "\t\t\tPLAYLIST CREATED SUCCESSFULLY." + RESET);
    }
    
    
    public void addSongsToPlaylist(String playlistName, String userName, String songName, String albumName, String albumLanguage) throws SQLException {
        // Call stored procedure to add song to playlist
        try 
        {
        CallableStatement cstmt = conn.prepareCall("{CALL add_song_to_playlist(?, ?, ?, ?, ?)}");
            cstmt.setString(1, playlistName);
            cstmt.setString(2, userName);
            cstmt.setString(3, songName);
            cstmt.setString(4, albumName);
            cstmt.setString(5, albumLanguage);
            
            try {
                cstmt.execute();
                System.out.println(GREEN + "\t\t\tSONG ADDED TO PLAYLIST." + RESET);
            } catch (SQLException e) {
                System.out.println(RED + "\t\t\tERROR: " + e.getMessage() + RESET);
            }
        }catch (SQLException e) {
            System.out.println(RED + "\t\t\tERROR: " + e.getMessage() + RESET);
    }
    }
    

    public void displayPlaylists(String userName) throws SQLException {
        // Get user ID from the users table
        String getUserIdQuery = "SELECT id FROM users WHERE name = ?";
        PreparedStatement pstmt = conn.prepareStatement(getUserIdQuery);
        pstmt.setString(1, userName);
        ResultSet rs = pstmt.executeQuery();
    
        if (!rs.next()) {
            System.out.println(RED + "\t\t\tUSER NOT FOUND." + RESET);
            return;
        }
    
        int userId = rs.getInt("id");
    
        // Display playlists for the user
        String getPlaylistsQuery = "SELECT * FROM playlists WHERE user_id = ?";
        pstmt = conn.prepareStatement(getPlaylistsQuery);
        pstmt.setInt(1, userId);
        ResultSet playlistsRs = pstmt.executeQuery();
    
        if (!playlistsRs.isBeforeFirst()) { // Check if the result set is empty
            System.out.println(RED + "\t\t\tNO PLAYLISTS CREATED." + RESET);
        } else {
            System.out.println(YELLOW + "\t\t\tYOUR PLAYLISTS:" + RESET);
            while (playlistsRs.next()) {
                System.out.println(GREEN + "\t\t\t\t" + playlistsRs.getString("name") + RESET);
            }
        }
    }
    

    public void playPlaylist(String playlistName, String userName) throws SQLException {
        // Get user ID from the users table
        String getUserIdQuery = "SELECT id FROM users WHERE name = ?";
        PreparedStatement pstmt = conn.prepareStatement(getUserIdQuery);
        pstmt.setString(1, userName);
        ResultSet rs = pstmt.executeQuery();
        
        if (!rs.next()) {
            System.out.println(RED + "\t\t\tUSER NOT FOUND." + RESET);
            return;
        }
    
        int userId = rs.getInt("id");
    
        // Retrieve playlist ID
        String getPlaylistIdQuery = "SELECT id FROM playlists WHERE name = ? AND user_id = ?";
        pstmt = conn.prepareStatement(getPlaylistIdQuery);
        pstmt.setString(1, playlistName);
        pstmt.setInt(2, userId);
        rs = pstmt.executeQuery();
        
        if (!rs.next()) {
            System.out.println(RED + "\t\t\tPLAYLIST " + playlistName + " NOT FOUND" + RESET);
            return;
        }
        
        int playlistId = rs.getInt("id");
    
        // Retrieve songs from the playlist
        String getSongsQuery = "SELECT s.name AS song_name, s.id AS song_id FROM playlist_songs ps JOIN songs s ON ps.song_id = s.id WHERE ps.playlist_id = ?";
        pstmt = conn.prepareStatement(getSongsQuery);
        pstmt.setInt(1, playlistId);
        ResultSet songsRs = pstmt.executeQuery();
    
        LinkedList<String> songs = new LinkedList<>();
        LinkedList<Integer> songIds = new LinkedList<>();
        while (songsRs.next()) {
            songs.add(songsRs.getString("song_name"));
            songIds.add(songsRs.getInt("song_id"));
        }
    
        if (songs.isEmpty()) {
            System.out.println(RED + "\t\t\tNO SONGS IN PLAYLIST " + playlistName + RESET);
            return;
        }
    
        int currentSongIndex = 0;
        Stack<Integer> history = new Stack<>();
        boolean exit = false;
    
        while (!exit) {
            String currentSong = songs.get(currentSongIndex);
            int currentSongId = songIds.get(currentSongIndex);
    
            System.out.println(GREEN + "\t\t\tPLAYING: " + currentSong + RESET);
    
            // Update play count
            pstmt = conn.prepareStatement("UPDATE songs SET play_count = play_count + 1 WHERE id = ?");
            pstmt.setInt(1, currentSongId);
            pstmt.executeUpdate();
    
            System.out.println(YELLOW + "CHOOSE AN OPTION: \n\t\t1. PREVIOUS SONG \t\t2. NEXT SONG \n\t\t3. RATE SONG \t\t\t4. EXIT PLAYLIST" + RESET);
            int choice = getIntegerInput();
    
            switch (choice) {
                case 1:
                    if (!history.isEmpty()) {
                        currentSongIndex = history.pop();
                    } else {
                        System.out.println(RED + "\t\t\tNO PREVIOUS SONG IN HISTORY." + RESET);
                    }
                    break;
                case 2:
                    history.push(currentSongIndex);
                    currentSongIndex++;
                    if (currentSongIndex >= songs.size()) {
                        currentSongIndex = 0; // Loop back to start
                    }
                    break;
                case 3:
                    rateSong(currentSongId);
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println(RED + "INVALID CHOICE. PLEASE TRY AGAIN" + RESET);
            }
        }
    }
    
    public void shufflePlaylist(String userName, String playlistName) throws SQLException {
        // Get user ID from the users table
        String getUserIdQuery = "SELECT id FROM users WHERE name = ?";
        PreparedStatement pstmt = conn.prepareStatement(getUserIdQuery);
        pstmt.setString(1, userName);
        ResultSet rs = pstmt.executeQuery();
    
        if (!rs.next()) {
            System.out.println(RED + "\t\t\tUSER NOT FOUND." + RESET);
            return;
        }
    
        int userId = rs.getInt("id");
    
        // Retrieve playlist ID
        String getPlaylistIdQuery = "SELECT id FROM playlists WHERE name = ? AND user_id = ?";
        pstmt = conn.prepareStatement(getPlaylistIdQuery);
        pstmt.setString(1, playlistName);
        pstmt.setInt(2, userId);
        rs = pstmt.executeQuery();
    
        if (!rs.next()) {
            System.out.println(RED + "\t\t\tPLAYLIST " + playlistName + " NOT FOUND" + RESET);
            return;
        }
    
        int playlistId = rs.getInt("id");
    
        // Retrieve songs from the playlist
        String getSongsQuery = "SELECT s.name AS song_name, s.id AS song_id FROM playlist_songs ps JOIN songs s ON ps.song_id = s.id WHERE ps.playlist_id = ?";
        pstmt = conn.prepareStatement(getSongsQuery);
        pstmt.setInt(1, playlistId);
        ResultSet songsRs = pstmt.executeQuery();
    
        LinkedList<String> songs = new LinkedList<>();
        LinkedList<Integer> songIds = new LinkedList<>();
        while (songsRs.next()) {
            songs.add(songsRs.getString("song_name"));
            songIds.add(songsRs.getInt("song_id"));
        }
    
        if (songs.isEmpty()) {
            System.out.println(RED + "\t\t\tNO SONGS IN PLAYLIST " + playlistName + RESET);
            return;
        }
    
        // Shuffle the song list and song IDs together
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
    
        LinkedList<String> shuffledSongs = new LinkedList<>();
        LinkedList<Integer> shuffledSongIds = new LinkedList<>();
        for (int index : indices) {
            shuffledSongs.add(songs.get(index));
            shuffledSongIds.add(songIds.get(index));
        }
    
        int currentSongIndex = 0;
        Stack<Integer> history = new Stack<>();
        boolean exit = false;
    
        while (!exit) {
            String currentSong = shuffledSongs.get(currentSongIndex);
            int currentSongId = shuffledSongIds.get(currentSongIndex);
    
            System.out.println(GREEN + "\t\t\tPLAYING: " + currentSong + RESET);
    
            // Update play count
            pstmt = conn.prepareStatement("UPDATE songs SET play_count = play_count + 1 WHERE id = ?");
            pstmt.setInt(1, currentSongId);
            pstmt.executeUpdate();
    
            System.out.println(YELLOW + "CHOOSE AN OPTION: \n\t\t1. PREVIOUS SONG \t\t2. NEXT SONG \n\t\t3. RATE SONG \t\t\t4. EXIT PLAYLIST" + RESET);
            int choice = getIntegerInput();
    
            switch (choice) {
                case 1:
                    if (!history.isEmpty()) {
                        currentSongIndex = history.pop();
                    } else {
                        System.out.println(RED + "\t\t\tNO PREVIOUS SONG IN HISTORY." + RESET);
                    }
                    break;
                case 2:
                    history.push(currentSongIndex);
                    currentSongIndex++;
                    if (currentSongIndex >= shuffledSongs.size()) {
                        currentSongIndex = 0; // Loop back to start
                    }
                    break;
                case 3:
                    rateSong(currentSongId);
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println(RED + "INVALID CHOICE. PLEASE TRY AGAIN" + RESET);
            }
        }
    }
    
    public void displaySongsInPlaylist(String playlistName, String userName) throws SQLException {
        // Get user ID from the users table
        String getUserIdQuery = "SELECT id FROM users WHERE name = ?";
        PreparedStatement pstmt = conn.prepareStatement(getUserIdQuery);
        pstmt.setString(1, userName);
        ResultSet rs = pstmt.executeQuery();
    
        if (!rs.next()) {
            System.out.println(RED + "\t\t\tUSER NOT FOUND." + RESET);
            return;
        }
    
        int userId = rs.getInt("id");
    
        // Check if the playlist exists for the given user
        String checkPlaylistQuery = "SELECT id FROM playlists WHERE name = ? AND user_id = ?";
        pstmt = conn.prepareStatement(checkPlaylistQuery);
        pstmt.setString(1, playlistName);
        pstmt.setInt(2, userId);
        rs = pstmt.executeQuery();
    
        if (!rs.next()) { // Check if playlist exists
            System.out.println(RED + "\t\t\tPLAYLIST " + playlistName + " IS NOT FOUND." + RESET);
        } else {
            int playlistId = rs.getInt("id");
    
            // Fetch songs from the playlist if it exists
            String getSongsQuery = "SELECT s.name AS song_name FROM playlist_songs ps JOIN songs s ON ps.song_id = s.id WHERE ps.playlist_id = ?";
            pstmt = conn.prepareStatement(getSongsQuery);
            pstmt.setInt(1, playlistId);
            ResultSet songsRs = pstmt.executeQuery();
    
            if (!songsRs.isBeforeFirst()) { // Check if the playlist is empty
                System.out.println(RED + "\t\t\tPLAYLIST IS EMPTY." + RESET);
            } else {
                System.out.println(YELLOW + "\t\t\tSONGS IN PLAYLIST " + playlistName + ":" + RESET);
                while (songsRs.next()) {
                    System.out.println(YELLOW + "\t\t\t" + songsRs.getString("song_name") + RESET);
                }
            }
        }
    }
    
    public void rateSong(int songId) throws SQLException {
        System.out.print(YELLOW + "\t\t\tENTER YOUR RATING (1 TO 5): " + RESET);
        int rating = getIntegerInput();
    
        if (rating < 1 || rating > 5) {
            System.out.println(RED + "\t\t\tINVALID RATING. PLEASE ENTER BETWEEN 1 AND 5" + RESET);
            return;
        }
    
        // Fetch the current rating and number of ratings
        String fetchRatingQuery = "SELECT rating, rating_count FROM songs WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(fetchRatingQuery);
        pstmt.setInt(1, songId);
        ResultSet rs = pstmt.executeQuery();
    
        if (!rs.next()) {
            System.out.println(RED + "\t\t\tSONG NOT FOUND." + RESET);
            return;
        }
    
        double currentRating = rs.getDouble("rating");
        int ratingCount = rs.getInt("rating_count");
    
        // Calculate new average rating
        double newRating = (currentRating * ratingCount + rating) / (ratingCount + 1);
    
        // Update rating and rating count
        String updateRatingQuery = "UPDATE songs SET rating = ?, rating_count = rating_count + 1 WHERE id = ?";
        pstmt = conn.prepareStatement(updateRatingQuery);
        pstmt.setDouble(1, newRating);
        pstmt.setInt(2, songId);
        pstmt.executeUpdate();
    
        System.out.println(GREEN + "\t\t\tRATING SUBMITTED. THANK YOU" + RESET);
    }
    
    public void deleteSongFromPlaylist(String userName, String playlistName) throws SQLException {
        // Check if the playlist exists and get the user ID
        PreparedStatement checkPlaylistStmt = conn.prepareStatement(
            "SELECT p.id, u.id AS user_id FROM playlists p JOIN users u ON p.user_id = u.id WHERE p.name = ? AND u.name = ?");
        checkPlaylistStmt.setString(1, playlistName);
        checkPlaylistStmt.setString(2, userName);
        ResultSet checkPlaylistRs = checkPlaylistStmt.executeQuery();
    
        if (!checkPlaylistRs.isBeforeFirst()) { // Check if playlist exists
            System.out.println(RED + "\t\t\tPLAYLIST " + playlistName + " IS NOT FOUND." + RESET);
            return;
        }
        
        // Retrieve the playlist ID and user ID
        checkPlaylistRs.next();
        int playlistId = checkPlaylistRs.getInt("id");
        
        // Ask user for the song name to delete
        System.out.print(YELLOW + "\t\tENTER SONG NAME: " + RESET);
        String songName = sc.nextLine();
    
        // Check if the song exists in the playlist
        PreparedStatement checkSongStmt = conn.prepareStatement(
            "SELECT * FROM playlist_songs ps JOIN songs s ON ps.song_id = s.id WHERE ps.playlist_id = ? AND s.name = ?");
        checkSongStmt.setInt(1, playlistId);
        checkSongStmt.setString(2, songName);
        ResultSet checkSongRs = checkSongStmt.executeQuery();
    
        if (!checkSongRs.isBeforeFirst()) { // Check if the song exists in the playlist
            System.out.println(RED + "\t\t\tSONG NOT FOUND IN THE PLAYLIST." + RESET);
            return;
        }
    
        // Attempt to delete the song from the playlist
        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = (SELECT id FROM songs WHERE name = ?)");
        pstmt.setInt(1, playlistId);
        pstmt.setString(2, songName);
        int rowsAffected = pstmt.executeUpdate();
    
        if (rowsAffected > 0) {
            System.out.println(GREEN + "\t\t\tSONG DELETED FROM PLAYLIST." + RESET);
    
            // Display remaining songs in the playlist
            PreparedStatement remainingSongsStmt = conn.prepareStatement(
                "SELECT s.name FROM playlist_songs ps JOIN songs s ON ps.song_id = s.id WHERE ps.playlist_id = ?");
            remainingSongsStmt.setInt(1, playlistId);
            ResultSet remainingSongsRs = remainingSongsStmt.executeQuery();
    
            if (!remainingSongsRs.isBeforeFirst()) {
                System.out.println(RED + "\t\t\tPLAYLIST IS NOW EMPTY." + RESET);
            } else {
                System.out.println(YELLOW + "\t\t\tREMAINING SONGS IN PLAYLIST " + playlistName + ":" + RESET);
                while (remainingSongsRs.next()) {
                    System.out.println(GREEN + "\t\t\t\t" + remainingSongsRs.getString("name") + RESET);
                }
            }
        } else {
            System.out.println(RED + "\t\t\tFAILED TO DELETE SONG. PLEASE TRY AGAIN." + RESET);
        }
    }
    
    public static int getIntegerInput() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(RED + "\t\t\tINVALID INPUT. PLEASE ENTER A NUMBER." + RESET);
            }
        }
    }
}
