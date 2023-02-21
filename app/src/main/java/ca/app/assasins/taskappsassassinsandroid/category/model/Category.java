package ca.app.assasins.taskappsassassinsandroid.category.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "CATEGORY_TBL")
public class Category implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "CATEGORY_ID")
    private Long id;
    private String name;

    public Category() {}

    @Ignore
    public Category(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
