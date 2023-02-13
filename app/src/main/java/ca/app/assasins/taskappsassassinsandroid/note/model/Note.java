package ca.app.assasins.taskappsassassinsandroid.note.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

import ca.app.assasins.taskappsassassinsandroid.common.helper.Converters;

@TypeConverters({Converters.class})
@Entity(tableName = "NOTE_TBL")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private Long id;

    private Long categoryId;
    private String title;
    private String description;

    private Date createdDate;

    public Note() {
    }

    public Note(String title,
                String description,
                Date createdDate,
                Long categoryId) {
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.categoryId = categoryId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long id) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
