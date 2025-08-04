package com.example.contactosmvvm_df.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.contactosmvvm_df.model.Categoria
import com.example.contactosmvvm_df.model.Contacto
import com.example.contactosmvvm_df.model.ContactoGrupoCrossRef
import com.example.contactosmvvm_df.model.Grupo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

    private class ContactosDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.categoriaDao())
                    populateDbGroups(database.grupoDao())
                }
            }
        }

        suspend fun populateDatabase(categoriaDao: CategoriaDao) {
            categoriaDao.insertar(Categoria(nombre = "Familia"))
            categoriaDao.insertar(Categoria(nombre = "Trabajo"))
            categoriaDao.insertar(Categoria(nombre = "Amigos"))
            categoriaDao.insertar(Categoria(nombre = "General"))
        }

        suspend fun populateDbGroups(grupoDao: GrupoDao) {
            grupoDao.insertarGrupo(Grupo(nombre = "Grupo 1"))
            grupoDao.insertarGrupo(Grupo(nombre = "Grupo 2"))
            grupoDao.insertarGrupo(Grupo(nombre = "Grupo 3"))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ContactosDatabase? = null

        /**
         * Obtiene la instancia única de la base de datos (Singleton).
         */
        fun getDatabase(context: Context, coroutineScope: CoroutineScope): ContactosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactosDatabase::class.java,
                    "contactos_database"
                )
                    .addCallback(ContactosDatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}