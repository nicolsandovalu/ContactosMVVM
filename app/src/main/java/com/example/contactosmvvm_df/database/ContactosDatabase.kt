package com.example.contactosmvvm_df.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.contactosmvvm_df.model.Categoria
import com.example.contactosmvvm_df.model.Contacto
import com.example.contactosmvvm_df.model.ContactoGrupoCrossRef
import com.example.contactosmvvm_df.model.Grupo

@Database(
    entities = [
        Contacto::class,
        Categoria::class,
        Grupo::class,
        ContactoGrupoCrossRef::class
    ],
    version = 1,  // Incrementa este número cuando hagas cambios en el esquema
    exportSchema = false  // Cambia a true y provee un directorio si quieres guardar el esquema
)
abstract class ContactosDatabase : RoomDatabase() {
    abstract fun contactoDao(): ContactoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun grupoDao(): GrupoDao

    companion object {
        @Volatile
        private var INSTANCE: ContactosDatabase? = null

        // Migración de ejemplo (si necesitas cambiar la versión)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Aquí irían las operaciones SQL para migrar de versión 1 a 2
            }
        }

        fun getDatabase(context: Context): ContactosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactosDatabase::class.java,
                    "contactos_database"
                )
                    //.addMigrations(MIGRATION_1_2)  // Descomenta cuando tengas migraciones
                    .fallbackToDestructiveMigrationOnDowngrade()  // Solo para downgrades
                    .fallbackToDestructiveMigration()  // Para migraciones no manejadas (elimina datos)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}