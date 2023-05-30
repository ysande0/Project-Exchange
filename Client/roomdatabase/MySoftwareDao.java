package com.syncadapters.czar.exchange.roomdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class MySoftwareDao {

    @Insert
    public abstract long insert(MySoftware my_software);

    @Insert
    public abstract void insert_all_software(List<MySoftware> my_software);

    @Update
    public abstract void update(MySoftware my_software);

    @Delete
    public abstract void delete(MySoftware my_software);

    @Query("DELETE FROM mysoftware")
    public abstract void delete_all_software();

    @Query("SELECT * FROM mysoftware ")
    public abstract List<MySoftware> query_all_software();

    @Transaction
    public  void update_all_software(List<MySoftware> my_softwares){

        delete_all_software();
        insert_all_software(my_softwares);

    }

}
