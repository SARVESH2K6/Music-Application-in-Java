package PROJECT;

import java.sql.*;

public class UserService extends Database implements Utilities {

    public UserService() throws SQLException {
        super();
    }

    public void register(String name, String password) throws SQLException {
    
        // Check if the username already exists
        PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE name = ?");
        checkStmt.setString(1, name);
        ResultSet rs = checkStmt.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        
        if (count > 0) {
            System.out.println(RED + "\t\t  USERNAME ALREADY EXISTS. PLEASE CHOOSE A DIFFERENT NAME." + RESET);
            System.out.println();
        } else {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (name, password) VALUES (?, ?)");
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            int rc = pstmt.executeUpdate();
            if (rc > 0) {
                System.out.println(GREEN + "\t\t\tREGISTRATION SUCCESSFUL. WELCOME TO SONGS HUB" + RESET);
                System.out.println();
            } else {
                System.out.println(RED + "\t\t  REGISTRATION FAILED. PLEASE TRY AGAIN." + RESET);
            }
        }
    }
    
    public User login(String name, String password) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE name = ? AND password = ?");
        pstmt.setString(1, name);
        pstmt.setString(2, password);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            System.out.println(GREEN+"\t\t\tLOGIN SUCCESSFUL.WELCOME TO SONGS HUB, "+name+RESET);
            return new User(rs.getString("name"), rs.getString("password"));
        } else {
            return null;
        }
    }

    public void updatePassword(String userName,String currentPassword) throws SQLException {

        PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM users WHERE BINARY name = ? AND BINARY password = ?");
        pstmt.setString(1, userName);
        pstmt.setString(2, currentPassword);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            System.out.print(YELLOW+"\t\t\tENTER NEW PASSWORD: "+RESET);
            String newPassword = sc.nextLine();

            System.out.print(YELLOW+"\t\t\tCONFIRM NEW PASSWORD: "+RESET);
            String confirmPassword = sc.nextLine();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println(RED+"\t\t\tPASSWORDS DO NOT MATCH. TRY AGAIN."+RESET);
                return;
            }

            if (newPassword.length() <= 5) {
                System.out.println(RED+"\t\t\tPASSWORD MUST BE LONGER THAN 5 CHARACTERS."+RESET);
                return;
            }

            pstmt = conn.prepareStatement("UPDATE users SET password = ? WHERE name = ?");
            pstmt.setString(1, newPassword);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
            System.out.println(GREEN+"\t\t\tPASSWORD UPDATED SUCCESSFULLY."+RESET);
        } else {
            System.out.println(RED+"\t\t\tINCORRECT CURRENT PASSWORD."+RESET);
        }
    }
}
