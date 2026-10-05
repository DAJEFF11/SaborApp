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
        crearTablasCatalogo(db)
        crearTablasPedidos(db)
        insertarUsuarioInicial(db, "admin", "1234", "ADMIN")
        insertarUsuarioInicial(db, "mozo", "1234", "MOZO")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            crearTablasPedidos(db)
        }
    }

    private fun crearTablasCatalogo(db: SQLiteDatabase) {
        db.execSQL(CREATE_USUARIO)
        db.execSQL(CREATE_PLATO)
        db.execSQL(CREATE_MESA)
    }

    private fun crearTablasPedidos(db: SQLiteDatabase) {
        db.execSQL(CREATE_PEDIDO)
        db.execSQL(CREATE_DETALLE_PEDIDO)
        db.execSQL(CREATE_UNIQUE_PEDIDO_ABIERTO)
        db.execSQL(CREATE_INDEX_DETALLE_PEDIDO)
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
        const val DB_VERSION = 2

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

        private const val CREATE_PEDIDO = """
            CREATE TABLE IF NOT EXISTS pedido (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_mesa INTEGER NOT NULL,
                fecha TEXT NOT NULL,
                estado TEXT NOT NULL CHECK (estado IN ('ABIERTO', 'CERRADO')),
                total REAL NOT NULL DEFAULT 0 CHECK (total >= 0),
                FOREIGN KEY (id_mesa) REFERENCES mesa(id)
            )
        """

        private const val CREATE_DETALLE_PEDIDO = """
            CREATE TABLE IF NOT EXISTS detalle_pedido (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pedido INTEGER NOT NULL,
                id_plato INTEGER NOT NULL,
                cantidad INTEGER NOT NULL CHECK (cantidad > 0),
                precio_unit REAL NOT NULL CHECK (precio_unit > 0),
                subtotal REAL NOT NULL CHECK (subtotal > 0),
                FOREIGN KEY (id_pedido) REFERENCES pedido(id) ON DELETE CASCADE,
                FOREIGN KEY (id_plato) REFERENCES plato(id)
            )
        """

        private const val CREATE_UNIQUE_PEDIDO_ABIERTO = """
            CREATE UNIQUE INDEX IF NOT EXISTS idx_pedido_mesa_abierto
            ON pedido(id_mesa)
            WHERE estado = 'ABIERTO'
        """

        private const val CREATE_INDEX_DETALLE_PEDIDO = """
            CREATE INDEX IF NOT EXISTS idx_detalle_pedido_id_pedido
            ON detalle_pedido(id_pedido)
        """
    }
}
