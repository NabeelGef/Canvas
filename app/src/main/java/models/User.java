package models;

public class User {
    String name , email , password , address , token , role , image , tokenMessage;
    int id;

    public int getId() {
        return id;
    }

    public String getTokenMessage() {
        return tokenMessage;
    }

    public void setTokenMessage(String tokenMessage) {
        this.tokenMessage = tokenMessage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User(String name, String email, String password, String address, String token, String role, String image, String tokenMessage, int id) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.token = token;
        this.role = role;
        this.image = image;
        this.tokenMessage = tokenMessage;
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
