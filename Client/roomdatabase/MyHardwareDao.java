package com.syncadapters.czar.exchange.roomdatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class MyHardwareDao {

    @Insert
   public abstract void insert(MyHardware my_hardware);

    @Insert
    public abstract void insert_all_hardware(List<MyHardware> my_hardwares);

    @Update
    public abstract void update(MyHardware my_hardware);

    @Query("DELETE FROM myhardware WHERE platform = :platform")
    public abstract void delete(String platform);

    @Query("DELETE FROM myhardware")
    public abstract void delete_all_hardware();

    @Query("SELECT * FROM myhardware")
    public abstract List<MyHardware> query_all_hardware();


    @Transaction
    public void update_all_hardware(List<MyHardware> my_hardwares){

        delete_all_hardware();
        insert_all_hardware(my_hardwares);
    }
}
