package models;

public class Log_in {
    String token , name , address , image , tokenMessage , url , email , role;
    int id;

    public Log_in(String token, String name, String address, String image, String tokenMessage, String url, String email, String role, int id) {
        this.token = token;
        this.name = name;
        this.address = address;
        this.image = image;
        this.tokenMessage = tokenMessage;
        this.url = url;
        this.email = email;
        this.role = role;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTokenMessage() {
        return tokenMessage;
    }

    public void setTokenMessage(String tokenMessage) {
        this.tokenMessage = tokenMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
