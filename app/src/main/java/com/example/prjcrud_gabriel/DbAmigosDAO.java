package com.example.prjcrud_gabriel;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DbAmigosDAO {

    private final String TABLE_AMIGOS = "Amigos";
    private DbAmigosGateway gw;

    public DbAmigosDAO(Context ctx){
        gw = DbAmigosGateway.getInstance(ctx);
    }

    public boolean salvar(String nome, String celular, int status){
        return salvar(0, nome, celular, status);
    }
    public boolean salvar(int id, String nome, String celular, int status){
        ContentValues cv = new ContentValues();
        cv.put("Nome", nome);
        cv.put("Celular", celular);
        cv.put("Status", status);
        if (id > 0) {
            return gw.getDatabase().update(TABLE_AMIGOS, cv, "ID=?", new String[]{ id + "" }) > 0;

        } else {
            return gw.getDatabase().insert(TABLE_AMIGOS, null, cv) > 0;
        }
    }

    public boolean excluir(int id){
        ContentValues cv = new ContentValues();
        cv.put("Status", 30);
        return  gw.getDatabase().update(TABLE_AMIGOS, cv, "ID=?", new String[]{id + ""}) > 0;
    }
    public boolean excluirInDB(int id){
        return gw.getDatabase().delete(TABLE_AMIGOS, "ID=?", new String[]{ id + "" }) > 0;
    }

    public boolean excluirAll(){
        ContentValues cv = new ContentValues();
        cv.put("Status", 30);
        return  gw.getDatabase().update(TABLE_AMIGOS, cv, null, null) > 0;
    }
    public boolean excluirAllInDB(){
        return gw.getDatabase().delete(TABLE_AMIGOS,"status=?", new String[]{ "30" }) > 0;
    }

    public boolean restaurar(int id){
        ContentValues cv = new ContentValues();
        cv.put("Status", 20);
        return gw.getDatabase().update(TABLE_AMIGOS, cv, "ID=?", new String[]{ id + "" }) > 0;
    }

    public boolean restaurarAll(){
        ContentValues cv = new ContentValues();
        cv.put("Status", 20);
        return gw.getDatabase().update(TABLE_AMIGOS, cv, null, null) > 0;
    }

    public List<DbAmigo> listarAmigos(){
        return this.listarAmigos("status != 30");
    }
    public List<DbAmigo> listarAmigos(String whereClause){
        List<DbAmigo> amigos = new ArrayList<>();
        Cursor cursor = gw.getDatabase().rawQuery("SELECT * FROM Amigos WHERE "+whereClause, null);

        while (cursor.moveToNext())
        {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("ID"));
            @SuppressLint("Range") String nome = cursor.getString(cursor.getColumnIndex("Nome"));
            @SuppressLint("Range") String celular = cursor.getString(cursor.getColumnIndex("Celular"));
            @SuppressLint("Range") int situacao = cursor.getInt(cursor.getColumnIndex("Status"));
            amigos.add(new DbAmigo(id, nome, celular, situacao));
        }
        cursor.close();
        return amigos;
    }

    public DbAmigo ultimoAmigo(){
        Cursor cursor = gw.getDatabase().rawQuery("SELECT * FROM Amigos ORDER BY ID DESC", null);
        if(cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("ID"));
            @SuppressLint("Range") String nome = cursor.getString(cursor.getColumnIndex("Nome"));
            @SuppressLint("Range") String celular = cursor.getString(cursor.getColumnIndex("Celular"));
            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex("Status"));
            cursor.close();
            return new DbAmigo(id, nome, celular, status);
        }
        return null;
    }

}

