package PROJECT;

import java.sql.*;

public class Main implements Utilities {
    static UserService userService;
    static PlaylistService playlistService;
    static Album album;
    
    public Main() throws SQLException {
        try {
            userService = new UserService();
            playlistService = new PlaylistService();
            album = new Album();
        } catch (SQLException e) {
            System.out.println(RED+"\t\t\tERROR: " + e.getMessage()+RESET);
        }
    }
    
    public static void main(String[] args) {
        try {
            Main app = new Main();
            app.start();
        } catch (SQLException e) {
            System.out.println(RED + "\t\t\tFAILED TO START APPLICATION DUE TO DATABASE ISSUES." + RESET);
            return;
        }
    }
    
    private void start() throws SQLException {
        boolean exit = false;
        while (!exit) {
            System.out.println(YELLOW + "CHOOSE AN OPTION:\t1. REGISTER \t2. LOGIN \t3. EXIT" + RESET);
            int choice = getIntegerInput();
        
            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    exit = true;
                    System.out.println(GREEN + "\t\t\tEXITING SONGS HUB..." + RESET);
                    break;
                default:
                    System.out.println(RED + "\t\t     INVALID CHOICE, PLEASE TRY AGAIN." + RESET);
                    System.out.println();
            }
        }
    }

    private static void register() throws SQLException {
        System.out.print(YELLOW + "\t\t\tENTER NAME: " + RESET);
        String name = sc.nextLine();

        if (name.length() <= 3) {
            System.out.println(RED + "\t\t  USERNAME MUST BE LONGER THAN 3 CHARACTERS." + RESET);
            System.out.println();
            return;
        }

        System.out.print(YELLOW + "\t\t\tENTER PASSWORD: " + RESET);
        String password = sc.nextLine();
    
        if (password.length() <= 5) {
            System.out.println(RED + "\t\t  PASSWORD MUST BE LONGER THAN 5 CHARACTERS." + RESET);
            System.out.println();
            return;
        }

        System.out.print(YELLOW + "\t\t\tCONFIRM PASSWORD: " + RESET);
        String confirmPassword = sc.nextLine();
    
        if (!password.equals(confirmPassword)) {
            System.out.println(RED + "\t\t  PASSWORD DOES NOT MATCH. TRY AGAIN." + RESET);
            System.out.println();
            return;
        }
        
        if (userService != null) {
            userService.register(name, password);
        } else {
            System.out.println(RED + "\t\t\tREGISTRATION FAILED." + RESET);
        }
    }
    

    private static void login() throws SQLException {
        System.out.print(YELLOW + "\t\t\tENTER NAME: " + RESET);
        String name = sc.nextLine();

        System.out.print(YELLOW + "\t\t\tENTER PASSWORD: " + RESET);
        String password = sc.nextLine();
        
        if (userService != null) {
            User user = userService.login(name, password);
            if (user != null) {
                userMenu(name);
            } else {
                System.out.println(RED + "\t\t INVALID CREDENTIALS. PLEASE TRY AGAIN" + RESET);
            }
        } else {
            System.out.println(RED + "\t\t\tLOGIN FAILED." + RESET);
        }
    }

    private static void userMenu(String userName) throws SQLException {
        boolean logout = false;
    
        while (!logout) {
            System.out.println(YELLOW + "CHOOSE AN OPTION: \n\t\t\t1. CREATE PLAYLIST \t\t7. DISPLAY SONGS IN PLAYLIST \n\t\t\t2. DISPLAY PLAYLIST \t\t8. DELETE SONG FROM PLAYLIST \n\t\t\t3. DISPLAY SONGS IN ALBUM \t9. UPDATE PASSWORD \n\t\t\t4. ADD SONGS TO PLAYLIST \t10. DISPLAY SONGS BY ARTIST \n\t\t\t5. PLAY PLAYLIST \t\t11. SEARCH ALBUM \n\t\t\t6. SHUFFLE PLAYLIST \t\t12. LOGOUT" + RESET);
            int choice = getIntegerInput();
    
            switch (choice) {
                case 1:
                    createPlaylist(userName);
                    break;
                case 2:
                    displayPlaylists(userName);
                    break;
                case 3:
                    displaySongsInAlbum();
                    break;
                case 4:
                    addSongsToPlaylist(userName);
                    break;
                case 5:
                    playPlaylist(userName);
                    break;
                case 6:
                    shufflePlaylist(userName);
                    break;
                case 7:
                    displaySongsInPlaylist(userName);
                    break;
                case 8:
                    deleteSongFromPlaylist(userName);
                    break;
                case 9:
                    updatePassword(userName);
                    break;
                case 10:
                    displaySongsByArtist();
                    break;
                case 11:
                    searchAlbum();
                    break;
                case 12:
                    logout = true;
                    System.out.println(GREEN + "\t\t\tLOGGING OUT OF YOUR ACCOUNT...." + RESET);
                    break;
                default:
                    System.out.println(RED + "\t\t\tINVALID CHOICE. PLEASE TRY AGAIN" + RESET);
            }
        }
    }
    
    private static void createPlaylist(String userName) throws SQLException {
        System.out.print(YELLOW + "\t\t\tENTER PLAYLIST NAME: " + RESET);
        String playlistName = sc.nextLine();
        if (playlistService != null) {
            playlistService.createPlaylist(playlistName, userName);
        } else {
            System.out.println(RED + "\t\t\tPLAYLIST CREATION FAILED" + RESET);
        }
    }

    private static void displayPlaylists(String userName) throws SQLException {
        if (playlistService != null) {
            playlistService.displayPlaylists(userName);
        } else {
            System.out.println(RED + "\t\t\t DISPLAYING PLAYLISTS FAILED" + RESET);
        }
    }
    
    private static void displaySongsInAlbum() throws SQLException {
        System.out.print(YELLOW + "\t\t\tENTER ALBUM NAME: " + RESET);
        String albumName = sc.nextLine();
        
        System.out.print(YELLOW + "\t\t\tENTER ALBUM LANGUAGE: " + RESET);
        String albumLanguage = sc.nextLine();
    
        System.out.println(YELLOW + "\t\t\tCHOOSE SORT OPTION: \n\t\t\t1. MOST PLAYED \n\t\t\t2. HIGHEST RATED" + RESET);
        int sortOption = getIntegerInput();
    
        String query = "";
        if (sortOption == 1) {
            query = "SELECT * FROM songs INNER JOIN albums ON songs.album_id=albums.id WHERE albums.name LIKE ? AND albums.language LIKE ? ORDER BY play_count DESC";
        } else if (sortOption == 2) {
            query = "SELECT * FROM songs INNER JOIN albums ON songs.album_id=albums.id WHERE albums.name LIKE ? AND albums.language LIKE ? ORDER BY rating DESC";
        } else {
            System.out.println(RED + "\t\t\tINVALID SORT OPTION." + RESET);
            return;
        }
        
        if (album != null) {
            album.displaySongsInAlbum(query, albumName, albumLanguage);
        } else {
            System.out.println(RED + "\t\t\tACTION FAILED. ALBUM NOT AVAILABLE" + RESET);
        }
    }
    
    
    private static void searchAlbum() throws SQLException {
        System.out.print(YELLOW + "\t\t\tENTER ALBUM NAME TO SEARCH: " + RESET);
        String albumName = sc.nextLine();
    
        System.out.print(YELLOW + "\t\t\tENTER ALBUM LANGUAGE: " + RESET);
        String albumLanguage = sc.nextLine();
        
        if (album != null) {
            album.searchAlbum(albumName, albumLanguage);
        } else {
            System.out.println(RED + "\\t\\t\\tACTION FAILED. ALBUM NOT AVAILABLE" + RESET);
        }
    }
    
    
    private static void displaySongsByArtist() throws SQLException {
        System.out.print(YELLOW + "\t\t\tENTER ARTIST NAME: " + RESET);
        String artistName = sc.nextLine();
        
        System.out.print(YELLOW + "\t\t\tENTER SONG LANGUAGE: " + RESET);
        String language = sc.nextLine();
        
        System.out.println(YELLOW + "\t\t\tCHOOSE SORT OPTION: \n\t\t\t1. MOST PLAYED \n\t\t\t2. HIGHEST RATED" + RESET);
        int sortOption = getIntegerInput();
        
        String query = "";
        if (sortOption == 1) {
            query = "SELECT s.name AS song_name, s.play_count, s.rating, a.name AS album_name FROM songs s JOIN albums a ON s.album_id = a.id WHERE s.artist LIKE ? AND a.language LIKE ? ORDER BY s.play_count DESC";
        } else if (sortOption == 2) {
            query = "SELECT s.name AS song_name, s.play_count, s.rating, a.name AS album_name FROM songs s JOIN albums a ON s.album_id = a.id WHERE s.artist LIKE ? AND a.language LIKE ? ORDER BY s.rating DESC";
        } else {
            System.out.println(RED + "\t\t\tINVALID SORT OPTION." + RESET);
            return;
        }
        
        if (album != null) {
            album.displaySongsByArtist(query, artistName, language);
        } else {
            System.out.println(RED + "\\t\\t\\tACTION FAILED. ALBUM NOT AVAILABLE" + RESET);
        }
    }
    
    
    
    private static void addSongsToPlaylist(String userName) throws SQLException {
        System.out.print(YELLOW + "\t\t\tENTER PLAYLIST NAME: " + RESET);
        String playlistName = sc.nextLine();
    
        System.out.print(YELLOW + "\t\t\tENTER SONG NAME: " + RESET);
        String songName = sc.nextLine();
    
        System.out.print(YELLOW + "\t\t\tENTER ALBUM NAME: " + RESET);
        String albumName = sc.nextLine();
    
        System.out.print(YELLOW + "\t\t\tENTER SONG LANGUAGE: " + RESET);
        String language = sc.nextLine();
    
        playlistService.addSongsToPlaylist(playlistName, userName, songName, albumName, language);
    }
    

        private static void playPlaylist(String userName) throws SQLException {
            System.out.print(YELLOW + "\t\t\tENTER PLAYLIST NAME:" + RESET);
            String playlistName = sc.nextLine();
            playlistService.playPlaylist(playlistName, userName);
        }

    private static void shufflePlaylist(String userName) throws SQLException {
        System.out.print(YELLOW + "\t\t\tENTER PLAYLIST NAME:" + RESET);
        String playlistName = sc.nextLine();
        playlistService.shufflePlaylist(userName,playlistName);
    }

    private static void displaySongsInPlaylist(String userName) throws SQLException {
        System.out.print(YELLOW + "\t\t\tENTER PLAYLIST NAME: " + RESET);
        String playlistName = sc.nextLine();
        playlistService.displaySongsInPlaylist(userName,playlistName);
    }

    private static void updatePassword(String userName) throws SQLException {
        System.out.print(YELLOW+"\t\t\tENTER CURRENT PASSWORD: "+RESET);
        String currentPassword = sc.nextLine();
        userService.updatePassword(userName,currentPassword);
    }

    private static void deleteSongFromPlaylist(String userName) throws SQLException {
        System.out.print(YELLOW + "\t\t\tENTER PLAYLIST NAME: " + RESET);
        String playlistName = sc.nextLine();
        playlistService.deleteSongFromPlaylist(userName,playlistName);
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
    public static String getAlphabetInput() {
        while (true) {
            String input = sc.nextLine();
            if (input.matches("[a-zA-Z ]+")) {
                return input;
            } else {
                System.out.println(RED + "\t\t\tINVALID INPUT. PLEASE ENTER ONLY ALPHABETS AND SPACES." + RESET);
            }
        }
}
}