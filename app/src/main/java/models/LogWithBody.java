package models;

public class LogWithBody {
    String email , password , tokenMessage;

    public LogWithBody(String email, String password, String tokenMessage) {
        this.email = email;
        this.password = password;
        this.tokenMessage = tokenMessage;
    }

    public String getTokenMessage() {
        return tokenMessage;
    }

    public void setTokenMessage(String tokenMessage) {
        this.tokenMessage = tokenMessage;
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
}
