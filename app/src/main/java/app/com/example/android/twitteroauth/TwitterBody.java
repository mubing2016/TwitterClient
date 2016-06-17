package app.com.example.android.twitteroauth;

import java.io.Serializable;
import java.util.ArrayList;

public class TwitterBody implements Serializable{
    int statusesLenght = 0;
//    String[] texts = new String[statusesLenght];
//    String[] imgUrl = new String[statusesLenght];

    ArrayList<String> texts = new ArrayList<>();
    ArrayList<String> imgUrl = new ArrayList<>();

    public TwitterBody(int statusesLenght, ArrayList<String> texts, ArrayList<String> imgUrl) {
        this.statusesLenght = statusesLenght;
        this.texts = texts;
        this.imgUrl = imgUrl;
    }
    public int getStatusesLenght() {
        return statusesLenght;
    }

    public ArrayList<String> getTexts() {
        return texts;
    }

    public ArrayList<String> getImgUrl() {
        return imgUrl;
    }

}
