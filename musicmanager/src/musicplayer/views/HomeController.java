/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayer.views;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import musicplayer.Song;

/**
 * FXML Controller class
 *
 * @author Vansh Jain
 */
public class HomeController implements Initializable {
    @FXML private TextField songTitle;
    @FXML private TextField artistName;
    @FXML private TextField albumTitle;
    @FXML private TextField lengthSeconds;
    
    @FXML private TableView<Song> table;
    @FXML private TableColumn<Song,String> songCol;
    @FXML private TableColumn<Song,String> artistCol;
    @FXML private TableColumn<Song,String> albumCol;
    @FXML private TableColumn<Song,Integer> lengthCol;
    
    public static ObservableList<Song> songs = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        songCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        artistCol.setCellValueFactory(new PropertyValueFactory<>("Artist"));
        albumCol.setCellValueFactory(new PropertyValueFactory<>("Album"));
        lengthCol.setCellValueFactory(new PropertyValueFactory<>("Duration"));
        table.setItems(songs);
    }
    
    @FXML
    public void quit() {
        Platform.exit();
    }
    
    @FXML
    public void reset(){
        songTitle.setText("");
        artistName.setText("");
        albumTitle.setText("");
        lengthSeconds.setText("");
        table.setItems(songs);
    }
    
    @FXML
    public void filterSong() {
        String title = songTitle.getText().trim();
        String artist = artistName.getText().trim();
        String album = albumTitle.getText().trim();
        ObservableList<Song> filter = FXCollections.observableArrayList();
        for (Song song : songs) {
            if(!title.isEmpty())     
                if (song.getTitle().equals(title)) {
                    filter.add(song);
                    continue;
                }
            if(!artist.isEmpty())
                if (song.getArtist().equals(artist)) {
                    filter.add(song);
                    continue;
                }
            if(!album.isEmpty())    
                if (song.getAlbum().equals(album)) {
                    filter.add(song);
                }
        }
        table.setItems(filter);
    }
    
    @FXML
    public void addSong() {
        String title = songTitle.getText().trim();
        String artist = artistName.getText().trim();
        String album = albumTitle.getText().trim();
        String length = lengthSeconds.getText().trim();
            
        if(validateInput(title,artist,album,length)) {
            int l = Integer.parseInt(length);
            Song song = new Song(title,artist,album,l);
            songs.add(song);
        }
        reset();
    }
    
    @FXML
    public void removeSong() {
        String title = songTitle.getText().trim();
        int index = findIndex(title);
        songs.remove(index);
        reset();
    }
    
    @FXML
    public void updateSong() {
        String title = songTitle.getText().trim();
        String artist = artistName.getText().trim();
        String album = albumTitle.getText().trim();
        String length = lengthSeconds.getText().trim();
            
        if(validateInput(title,artist,album,length)) {
            int l = Integer.parseInt(length);
            int index = findIndex(title);
            System.out.println(index);
            Song song = songs.get(index);
            song.setArtist(artist);
            song.setAlbum(album);
            song.setDuration(l);
            songs.set(index, song);
        }
        reset();
    }
    
    public int findIndex(String name) {
        for (Song song : songs) {
            if (song.getTitle().equals(name)) {
                return songs.indexOf(song);
            }
        }
        return -1;
    }
    
    public boolean validateInput(String title, String name, String album, String l) {
        return (!title.isEmpty() && !name.isEmpty() && !album.isEmpty() && !l.isEmpty());
    }
}
