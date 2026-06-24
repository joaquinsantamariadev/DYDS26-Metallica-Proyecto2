package com.app.common.data.local

import com.app.inventory.data.local.CategoryTable
import com.app.inventory.data.local.ExchangeRateTable
import com.app.inventory.data.local.ProductTable
import com.app.pos.data.local.CashRegisterSessionsTable
import com.app.pos.data.local.SaleItemsTable
import com.app.pos.data.local.SalesTable
import com.app.settings.data.local.SettingsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.io.File

object DatabaseFactory {
    fun init() {
        val dbFile = File(System.getProperty("user.dir"), "stock_control.db")
        Database.connect(
            url = "jdbc:sqlite:${dbFile.absolutePath}",
            driver = "org.sqlite.JDBC"
        )
        
        transaction {
            SchemaUtils.create(
                CategoryTable, ProductTable, ExchangeRateTable, CashRegisterSessionsTable,
                SalesTable, SaleItemsTable, SettingsTable
            )
            seedInitialProducts()
        }
    }

    private fun seedInitialProducts() {
        val initialProducts = listOf(
            "7790770601899" to "Toalla Nosotras Clasicas C Calendula x 16u",
            "7790070318398" to "Fideos Dedalitos Lucchetti Bolsa x 500 Grs",
            "7622201808860" to "Galletitas Boca de Dama Terrabusi x 170 Grs",
            "7791293050089" to "Acondicionador Liso Efecto Botox Tresemme x 250 Cc",
            "7791905023210" to "Lavandina Original Odex x 2 Lt",
            "7798136041525" to "Perfume Aromatizador Freshline Aqua Spar Rombo",
            "7792798002726" to "Cerveza Origen Ipa Andes Lata x 473 Cc",
            "7791720034873" to "Jugo Manzana Carrefour Classic Tetra x 1 Lt",
            "7798108349161" to "Atun Al Natural Lomitos Carrefour Lata x 354 Grs",
            "7798031470635" to "Pochoclo Dulce Microondas Popcorn x 87 Grs",
            "7790040173200" to "Galletitas Kesitas Estuche x 125 Grs",
            "7791564136535" to "Queso Azul Bavaria Trozado x 125 Grs",
            "2510209000001" to "Costeleta Carre x 3 Huella Natural x Kg",
            "9786075281568" to "Manga Naruto 10",
            "7793253098773" to "Perfumante Tela Frag Sedosa Primavera Poett X250cc",
            "7790894900809" to "Celular Libre Motorola G84 8gb 256gb Ballad Blue",
            "7790639003444" to "Gaseosa Tonica Classic Cunnington x 2.25 Lt",
            "8445290624819" to "Cafe Tostado Molido Blonde Starbucks x 250 Grs",
            "7790528010256" to "Pano Texas Esponja Doble 44x48",
            "7791720039748" to "Galletitas Surtido Rellenas Bulnez x 350 Grs",
            "7791813420385" to "Gaseosa Pomelo Regular Paso de los Toros Pet X500c",
            "7791720045145" to "Yogur Colchon Dur Carrefour Extra Frasco x 200 Gr",
            "7500435209632" to "Shampoo Equilibrio Pantene x 200 Cc",
            "7791720030608" to "Enjuague Bucal Mentol Carrefour Soft Bot x 65 Cc",
            "7793015000440" to "Termo Lumilagro 1 L Terra",
            "7793147570927" to "Cerveza Blanca Warsteiner Lata x 473 Cc",
            "7790132009158" to "Destapacanerias Liquido Plomero x 1 L",
            "7792319972248" to "Vino Rosado Cosecha Tardia Norton x 750 Cc",
            "7790580132170" to "Mermelada Damasco Arcor x 454 Grs",
            "8445290256270" to "Cafe Instantaneo Caramel Dolca x 125 Grs",
            "4006000063102" to "Crema para Manos Intensive Lata Atrix x 150 Gr",
            "7793147570866" to "Cerveza Blanca Grolsch Lata x 473 Cc",
            "7791813402022" to "Agua Saborizada Limon S Gas H2oh x 500 Cc",
            "7790204450046" to "Alfajor S Tapa Ddl Angiola Dulce x 50 Grs"
        )

        initialProducts.forEach { (bar, nom) ->
            val exists = ProductTable.selectAll().where { ProductTable.barcode eq bar }.count() > 0
            if (!exists) {
                ProductTable.insert {
                    it[barcode] = bar
                    it[name] = nom
                    it[price] = 1.0
                    it[cost] = 0.5
                    it[stock] = 10
                }
            }
        }
    }
}
