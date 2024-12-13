package PROJECT;

class User {
    private String name;
    private String password;

    public User(String username, String password) {
        this.name = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}

