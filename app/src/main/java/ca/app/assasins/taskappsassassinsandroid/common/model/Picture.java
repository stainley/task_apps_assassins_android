package ca.app.assasins.taskappsassassinsandroid.common.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "PICTURE_TBL")
public class Picture {
    @PrimaryKey
    @ColumnInfo(name = "PICTURE_ID")
    private Long id;
    @ColumnInfo(name = "PICTURE_PATH")
    private String path;
    @ColumnInfo(name = "CREATION_DATE")
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
