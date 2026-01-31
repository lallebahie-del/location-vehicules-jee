package model;

public class Admin extends User {

    public Admin() {
        super();
        this.setRole("ADMIN");
    }

    public Admin(Long id, String username, String password) {
        super(id, username, password, "ADMIN");
    }
}