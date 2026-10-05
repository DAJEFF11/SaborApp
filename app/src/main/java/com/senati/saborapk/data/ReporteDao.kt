package com.senati.saborapk.data

import android.content.Context
import com.senati.saborapk.data.model.ResumenVentas
import com.senati.saborapk.data.model.TopPlato
import com.senati.saborapk.data.model.VentaPorMesa

class ReporteDao(context: Context) {

    private val dbHelper = DBHelper(context.applicationContext)

    fun resumenDelDia(): ResumenVentas {
        val sql = """
            SELECT COALESCE(SUM(total), 0) AS venta,
                   COUNT(*) AS pedidos,
                   COALESCE(AVG(total), 0) AS ticket
            FROM pedido
            WHERE estado = 'CERRADO'
              AND date(fecha) = date('now', 'localtime')
        """.trimIndent()
        dbHelper.readableDatabase.rawQuery(sql, null).use { cursor ->
            cursor.moveToFirst()
            return ResumenVentas(
                ventaDelDia = cursor.getDouble(cursor.getColumnIndexOrThrow("venta")),
                cantidadPedidos = cursor.getInt(cursor.getColumnIndexOrThrow("pedidos")),
                ticketPromedio = cursor.getDouble(cursor.getColumnIndexOrThrow("ticket"))
            )
        }
    }

    fun topPlatos(): List<TopPlato> {
        val resultados = mutableListOf<TopPlato>()
        val sql = """
            SELECT pl.nombre, SUM(d.cantidad) AS cantidad
            FROM detalle_pedido d
            INNER JOIN pedido p ON p.id = d.id_pedido
            INNER JOIN plato pl ON pl.id = d.id_plato
            WHERE p.estado = 'CERRADO'
              AND date(p.fecha) = date('now', 'localtime')
            GROUP BY d.id_plato, pl.nombre
            ORDER BY cantidad DESC, pl.nombre COLLATE NOCASE
            LIMIT 5
        """.trimIndent()
        dbHelper.readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                resultados += TopPlato(
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
                )
            }
        }
        return resultados
    }

    fun ventaPorMesa(): List<VentaPorMesa> {
        val resultados = mutableListOf<VentaPorMesa>()
        val sql = """
            SELECT m.numero, SUM(p.total) AS total
            FROM pedido p
            INNER JOIN mesa m ON m.id = p.id_mesa
            WHERE p.estado = 'CERRADO'
              AND date(p.fecha) = date('now', 'localtime')
            GROUP BY p.id_mesa, m.numero
            ORDER BY total DESC, m.numero
        """.trimIndent()
        dbHelper.readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                resultados += VentaPorMesa(
                    numeroMesa = cursor.getInt(cursor.getColumnIndexOrThrow("numero")),
                    total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                )
            }
        }
        return resultados
    }
}
