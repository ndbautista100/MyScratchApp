package classes;

public class Profile {
    private String pname, pbio, pfavoritefood;
    String pimageURL;

    public Profile() {
        // empty constructor
        // required for Firebase.
    }

    // Constructor for all variables.
    public Profile(String name, String bio, String favoritefood) {
        pname = name;
        pbio = bio;
        pfavoritefood = favoritefood;
    }

    // getter methods for all variables.
    public String getpname() {
        return pname;
    }

    public void setname(String name) {
        pname = name;
    }

    public String getbio() {
        return pbio;
    }

    // setter method for all variables.
    public void setbio(String bio) {
        pbio = bio;
    }

    public String getfavoritefood() {
        return pfavoritefood;
    }

    public void setfavoritefood(String favoritefood) {
        pfavoritefood = favoritefood;
    }

    public String getImageURL(){return pimageURL;}

    public void setImageURL(String imageURL){this.pimageURL = imageURL;}
}