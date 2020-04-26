package Model;

public class Blog {
    private String title;
    private String desc;
    private String image;
    private String userId;
    private String date;

    public Blog(String title, String desc, String image, String date ,String userId) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.date = date;
        this.userId = userId;
    }

    public Blog() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
