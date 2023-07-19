package main;

import javafx.scene.image.ImageView;

public class TweetStats {

    private int tweetNumber;
    private int likes;
    private int retweet;
    private int followers;
    private String user;
    private ImageView image;

    public TweetStats(int tweetNumber, String user, int likes, int retweet, int followers, ImageView image){
        this.tweetNumber = tweetNumber;
        this.user = user;
        this.likes = likes;
        this.retweet = retweet;
        this.followers = followers;
        this.image = image;
    }

    public int getTweetNumber() {
        return tweetNumber;
    }

    public void setTweetNumber(int tweetNumber) {
        this.tweetNumber = tweetNumber;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getRetweet() {
        return retweet;
    }

    public void setRetweet(int retweet) {
        this.retweet = retweet;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }



}