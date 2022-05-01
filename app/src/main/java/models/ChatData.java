package models;

public class ChatData {
    int idme , idother ;
    String message;

    public ChatData(int idme, int idother, String message) {
        this.idme = idme;
        this.idother = idother;
        this.message = message;
    }

    public int getIdme() {
        return idme;
    }

    public int getIdother() {
        return idother;
    }

    public String getMessage() {
        return message;
    }
}
