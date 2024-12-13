package PROJECT;

import java.sql.*;
import java.util.*;

public class Album extends Database implements Utilities {
    Scanner sc=new Scanner(System.in);
    public Album() throws SQLException {
        super();
    }
    public void displaySongsInAlbum(String query, String albumName, String albumLanguage) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
    
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, "%"+albumName+"%");
            pstmt.setString(2, "%"+albumLanguage+"%");
            rs = pstmt.executeQuery();
            
            if (!rs.isBeforeFirst()) { // Checks if the result set is empty
                System.out.println(RED + "\t\t\tNO SUCH ALBUM IS FOUND." + RESET);
            } else {
                System.out.println(YELLOW + "\t\t\tSONGS IN " + albumName + " :" + RESET);
                while (rs.next()) {
                    System.out.println(GREEN + "\t\t\t\t" + rs.getString("songs.name") +"|| ALBUM NAME: " + rs.getString("albums.name") + "|| PLAY COUNT: " + rs.getInt("play_count") + "|| RATING: " + rs.getDouble("rating") + RESET);
                }
            }
        } catch (SQLException e) {
            System.err.println(RED + "\t\t\tSQL ERROR: " + e.getMessage() + RESET);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println(RED + "\t\t\tERROR: " + e.getMessage() + RESET);
            }
        }
    }
    
    public void searchAlbum(String albumName, String albumLanguage) throws SQLException {
        // search for albums based on name and language
        PreparedStatement pstmt = conn.prepareStatement( "SELECT name FROM albums WHERE name LIKE ? AND language LIKE ?");
        pstmt.setString(1, "%" + albumName + "%");
        pstmt.setString(2, "%"+albumLanguage+"%");
        ResultSet rs = pstmt.executeQuery();
        
        if (!rs.isBeforeFirst()) { // Check if the result set is empty
            System.out.println(RED + "\t\t\tNO RESULTS FOUND FOR ALBUM " + albumName + RESET);
        } else {
            System.out.println(YELLOW + "\t\t\tSEARCH RESULTS: " + RESET);
            while (rs.next()) {
                System.out.println(GREEN + "\t\t\t\t" + rs.getString("name") + RESET);
            }
        }
    }
    
    public void displaySongsByArtist(String query,String artistName, String albumLanguage) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, "%" + artistName + "%"); // Using LIKE to match artist names
        pstmt.setString(2, "%"+albumLanguage+"%");
        ResultSet rs = pstmt.executeQuery();
        
        if (!rs.isBeforeFirst()) { // Check if the result set is empty
            System.out.println(RED + "\t\t\tNO SONGS FOUND FOR ARTIST " + artistName + "." + RESET);
        } else {
            System.out.println(YELLOW + "\t\t\tSONGS BY " + artistName + ":" + RESET);
            while (rs.next()) {
                System.out.println(GREEN + "\t\t\t\t" + rs.getString("song_name") + "|| PLAY COUNT: " + rs.getInt("play_count") +"|| RATING: " + rs.getDouble("rating") +"|| ALBUM: " + rs.getString("album_name"));
            }
        }
    }
    
}