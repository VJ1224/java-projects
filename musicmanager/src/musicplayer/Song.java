/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayer;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Vansh Jain
 */
public class Song {
    private SimpleStringProperty title;
    private SimpleStringProperty artist;
    private SimpleStringProperty album;
    private SimpleIntegerProperty duration;

    public Song(String title, String artist, String album, int duration) {
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.album = new SimpleStringProperty(album);
        this.duration = new SimpleIntegerProperty(duration);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title = new SimpleStringProperty(title);
    }

    public String getArtist() {
        return artist.get();
    }

    public void setArtist(String artist) {
        this.artist = new SimpleStringProperty(artist);
    }

    public String getAlbum() {
        return album.get();
    }

    public void setAlbum(String album) {
        this.album = new SimpleStringProperty(album);
    }

    public int getDuration() {
        return duration.get();
    }

    public void setDuration(int duration) {
        this.duration = new SimpleIntegerProperty(duration);
    }

    @Override
    public String toString() {
        String print = "\nTitle: " + getTitle() + "\nArtist: " + getArtist() + 
                "\nAlbum: " + getAlbum() + "\nLength: " + getDuration() + "\n";
        return print;
    }
}
