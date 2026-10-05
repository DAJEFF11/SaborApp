package com.senati.saborapk.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.senati.saborapk.data.model.DetallePedido
import com.senati.saborapk.data.model.Pedido
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UseKtx")
class PedidoDao(context: Context) {

    private val dbHelper = DBHelper(context.applicationContext)

    fun obtenerPedidoAbiertoPorMesa(idMesa: Long): Pedido? {
        return consultarPedido(
            dbHelper.readableDatabase,
            "p.id_mesa = ? AND p.estado = 'ABIERTO'",
            arrayOf(idMesa.toString())
        )
    }

    fun obtenerPedido(idPedido: Long): Pedido? {
        return consultarPedido(
            dbHelper.readableDatabase,
            "p.id = ?",
            arrayOf(idPedido.toString())
        )
    }

    fun listarDetalle(idPedido: Long): List<DetallePedido> {
        val detalles = mutableListOf<DetallePedido>()
        val sql = """
            SELECT d.id, d.id_plato, p.nombre, d.cantidad, d.precio_unit, d.subtotal
            FROM detalle_pedido d
            INNER JOIN plato p ON p.id = d.id_plato
            WHERE d.id_pedido = ?
            ORDER BY d.id
        """.trimIndent()
        dbHelper.readableDatabase.rawQuery(sql, arrayOf(idPedido.toString())).use { cursor ->
            while (cursor.moveToNext()) {
                detalles += DetallePedido(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    idPlato = cursor.getLong(cursor.getColumnIndexOrThrow("id_plato")),
                    nombrePlato = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                    precioUnitario = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_unit")),
                    subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal"))
                )
            }
        }
        return detalles
    }

    fun agregarPlato(idMesa: Long, idPlato: Long, cantidad: Int): Long {
        require(cantidad > 0) { "La cantidad debe ser mayor que cero" }
        val db = dbHelper.writableDatabase
        var idPedido = 0L

        db.beginTransaction()
        try {
            val precio = obtenerPrecioDisponible(db, idPlato)
                ?: throw IllegalStateException("El plato ya no está disponible")

            idPedido = obtenerIdPedidoAbierto(db, idMesa) ?: crearPedido(db, idMesa)
            val subtotal = precio * cantidad
            val detalleValues = ContentValues().apply {
                put("id_pedido", idPedido)
                put("id_plato", idPlato)
                put("cantidad", cantidad)
                put("precio_unit", precio)
                put("subtotal", subtotal)
            }
            db.insertOrThrow("detalle_pedido", null, detalleValues)
            db.execSQL(
                """
                UPDATE pedido
                SET total = (
                    SELECT COALESCE(SUM(subtotal), 0)
                    FROM detalle_pedido
                    WHERE id_pedido = ?
                )
                WHERE id = ?
                """.trimIndent(),
                arrayOf(idPedido, idPedido)
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return idPedido
    }

    fun cerrarCuenta(idPedido: Long): Boolean {
        val db = dbHelper.writableDatabase
        var cerrada = false

        db.beginTransaction()
        try {
            val pedido = consultarPedido(
                db,
                "p.id = ? AND p.estado = 'ABIERTO'",
                arrayOf(idPedido.toString())
            ) ?: return false

            val total = db.rawQuery(
                "SELECT COALESCE(SUM(subtotal), 0) FROM detalle_pedido WHERE id_pedido = ?",
                arrayOf(idPedido.toString())
            ).use { cursor ->
                cursor.moveToFirst()
                cursor.getDouble(0)
            }
            if (total <= 0.0) return false

            val pedidoValues = ContentValues().apply {
                put("estado", ESTADO_CERRADO)
                put("total", total)
            }
            val pedidosActualizados = db.update(
                "pedido",
                pedidoValues,
                "id = ? AND estado = 'ABIERTO'",
                arrayOf(idPedido.toString())
            )
            val mesaValues = ContentValues().apply { put("estado", MesaDao.ESTADO_LIBRE) }
            val mesasActualizadas = db.update(
                "mesa",
                mesaValues,
                "id = ?",
                arrayOf(pedido.idMesa.toString())
            )
            cerrada = pedidosActualizados == 1 && mesasActualizadas == 1
            if (cerrada) db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return cerrada
    }

    private fun crearPedido(db: SQLiteDatabase, idMesa: Long): Long {
        val mesaActualizada = db.update(
            "mesa",
            ContentValues().apply { put("estado", MesaDao.ESTADO_OCUPADA) },
            "id = ? AND estado = ?",
            arrayOf(idMesa.toString(), MesaDao.ESTADO_LIBRE)
        )
        if (mesaActualizada != 1) {
            throw IllegalStateException("La mesa no está disponible")
        }

        val values = ContentValues().apply {
            put("id_mesa", idMesa)
            put("fecha", FORMATO_FECHA.format(Date()))
            put("estado", ESTADO_ABIERTO)
            put("total", 0.0)
        }
        return db.insertOrThrow("pedido", null, values)
    }

    private fun obtenerPrecioDisponible(db: SQLiteDatabase, idPlato: Long): Double? {
        return db.rawQuery(
            "SELECT precio FROM plato WHERE id = ? AND disponible = 1",
            arrayOf(idPlato.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getDouble(0) else null
        }
    }

    private fun obtenerIdPedidoAbierto(db: SQLiteDatabase, idMesa: Long): Long? {
        return db.rawQuery(
            "SELECT id FROM pedido WHERE id_mesa = ? AND estado = 'ABIERTO' LIMIT 1",
            arrayOf(idMesa.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getLong(0) else null
        }
    }

    private fun consultarPedido(
        db: SQLiteDatabase,
        where: String,
        args: Array<String>
    ): Pedido? {
        val sql = """
            SELECT p.id, p.id_mesa, m.numero, p.fecha, p.estado, p.total
            FROM pedido p
            INNER JOIN mesa m ON m.id = p.id_mesa
            WHERE $where
            LIMIT 1
        """.trimIndent()
        db.rawQuery(sql, args).use { cursor ->
            if (!cursor.moveToFirst()) return null
            return cursorAPedido(cursor)
        }
    }

    private fun cursorAPedido(cursor: Cursor) = Pedido(
        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
        idMesa = cursor.getLong(cursor.getColumnIndexOrThrow("id_mesa")),
        numeroMesa = cursor.getInt(cursor.getColumnIndexOrThrow("numero")),
        fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
        estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
        total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
    )

    companion object {
        const val ESTADO_ABIERTO = "ABIERTO"
        const val ESTADO_CERRADO = "CERRADO"
        private val FORMATO_FECHA = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    }
}
