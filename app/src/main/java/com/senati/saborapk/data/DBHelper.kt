package com.senati.saborapk.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_USUARIO)
        db.execSQL(CREATE_PLATO)
        db.execSQL(CREATE_MESA)
        insertarUsuarioInicial(db, "admin", "1234", "ADMIN")
        insertarUsuarioInicial(db, "mozo", "1234", "MOZO")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // La primera migración se implementará en el Sprint 3 (DB_VERSION = 2).
    }

    private fun insertarUsuarioInicial(
        db: SQLiteDatabase,
        usuario: String,
        clave: String,
        rol: String
    ) {
        val values = ContentValues().apply {
            put("usuario", usuario)
            put("clave", clave)
            put("rol", rol)
        }
        db.insertOrThrow("usuario", null, values)
    }

    companion object {
        const val DB_NAME = "saborapp.db"
        const val DB_VERSION = 1

        private const val CREATE_USUARIO = """
            CREATE TABLE usuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT NOT NULL UNIQUE COLLATE NOCASE,
                clave TEXT NOT NULL,
                rol TEXT NOT NULL CHECK (rol IN ('ADMIN', 'MOZO'))
            )
        """

        private const val CREATE_PLATO = """
            CREATE TABLE plato (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                categoria TEXT NOT NULL,
                precio REAL NOT NULL CHECK (precio > 0),
                disponible INTEGER NOT NULL DEFAULT 1 CHECK (disponible IN (0, 1))
            )
        """

        private const val CREATE_MESA = """
            CREATE TABLE mesa (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero INTEGER NOT NULL UNIQUE,
                capacidad INTEGER NOT NULL CHECK (capacidad BETWEEN 1 AND 12),
                estado TEXT NOT NULL DEFAULT 'LIBRE' CHECK (estado IN ('LIBRE', 'OCUPADA'))
            )
        """
    }
}
