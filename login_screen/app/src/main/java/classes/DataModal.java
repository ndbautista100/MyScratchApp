package classes;


public class DataModal {
    private String name;
    private String description;
    private String imgUrl;

    public DataModal() {
        // empty constructor required for Firebase
    }

    // constructor for our object class
    public DataModal(String name, String description, String imgUrl) {
        this.name = name;
        this.description = description;
        this.imgUrl = imgUrl;
    }

    // getter and setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}