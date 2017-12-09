package dv606.sb223ce.assignment2;

public class Song {

    private final String artist;
    private final String album;
    private final String name;
    private final String path;
    private Song next = null;
    private Song previous = null;

    public Song(String artist, String album, String name, String path) {
        this.artist = artist;
        this.album = album;
        this.name = name;
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setNext(Song song) {
        next = song;
    }

    public Song getNext() {
        return next;
    }

    public void setPrevious(Song song) {
        previous = song;
    }

    public Song getPrevious() {
        return previous;
    }

    @Override
    public String toString() {
        String result = null;
        if (path != null)
            result = path;

        if (name != null)
            result = name;

        if (artist != null)
            result = artist + " - " + result;

        if (album != null)
            result = result + " (" + album + ")";

        return result;
    }
}