package com.syncadapters.czar.exchange.roomdatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface HomeDao {

    @Insert
    void insert(Home home);

    @Update
    void update(Home home);

    @Query("SELECT * FROM home WHERE id = 1")
    List<Home> query_users();


}
