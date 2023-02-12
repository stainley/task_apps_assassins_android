package ca.app.assasins.taskappsassassinsandroid.common.model;

import androidx.room.Entity;

@Entity(tableName = "AUDIO_TBL")
public class Audio {
    private Long id;
    private String path;
    private Long creationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }
}
