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
    version = 2, // Se incrementa la versión de la base de datos [cite: anbs12/deflatam_contactapp/DefLatam_ContactApp-5b5d6d9c8ae4026dc08fe88f4eb93affa0640ef2/app/src/main/java/com/example/deflatam_contactapp/database/ContactosDatabase.kt]
    exportSchema = false
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

        fun getDatabase(context: Context, coroutineScope: CoroutineScope): ContactosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactosDatabase::class.java,
                    "contactos_database"
                )
                    .fallbackToDestructiveMigration() // Agregue esta línea [cite: anbs12/deflatam_contactapp/DefLatam_ContactApp-5b5d6d9c8ae4026dc08fe88f4eb93affa0640ef2/app/src/main/java/com/example/deflatam_contactapp/database/ContactosDatabase.kt]
                    .addCallback(ContactosDatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
