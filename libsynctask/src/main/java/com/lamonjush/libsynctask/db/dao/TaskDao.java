package com.lamonjush.libsynctask.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.lamonjush.libsynctask.db.entity.TaskEntity;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM TaskEntity ORDER BY id ASC")
    List<TaskEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskEntity... entities);

    @Delete
    void delete(TaskEntity entity);
}
