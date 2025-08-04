# 📱 **App de Contactos con Arquitectura MVVM**  

**✨ Una aplicación Android moderna para gestión de contactos** que implementa arquitectura **Model-View-ViewModel (MVVM)** con:  
- **Room** para persistencia local  
- **LiveData** para UI reactiva  
- **Coroutines** para operaciones asíncronas  

---

## 🚀 **Funcionalidades Principales**  

### 🔄 **CRUD Completo**  
- Crear, Leer, Actualizar y Eliminar contactos  

### 🔍 **Búsqueda Inteligente**  
- Filtrado en tiempo real mientras escribes  

### 🏷️ **Organización Avanzada**  
- Asignación de **categorías** (Familia, Trabajo, etc.)  
- Grupos de contactos ("Amigos", "Equipo", etc.)  

### ⚡ **Acciones Rápidas**  
- Llamar o enviar SMS directamente desde la app  

### 📂 **Backup & Exportación**  
- **Exportar/Importar** en formato JSON  
- Generar archivos **vCard (.vcf)** compatibles  

### 📲 **Integración con Dispositivo**  
- Importar contactos del teléfono  

### ✅ **Validación de Datos**  
- Verificación de campos al crear/editar contactos  

---

## 🛠️ **Arquitectura y Tecnologías**  

| Tecnología | Función |
|------------|---------|
| 🏗️ **MVVM** | Arquitectura limpia |
| 🗃️ **Room** | Base de datos local |
| 📊 **LiveData** | Actualización reactiva de UI |
| 🧵 **Coroutines** | Operaciones asíncronas |
| 🔗 **View Binding** | Manejo seguro de vistas |
| 🛠️ **KSP** | Procesamiento de anotaciones |
| 📦 **Gson** | Serialización JSON |


---

## ⚙️ **Dependencias Principales**  

```kotlin
dependencies {
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    
    // UI
    implementation("com.google.android.material:material:1.11.0")
    
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
}
---

##🤝 Cómo Contribuir
¡Tu ayuda es bienvenida! Puedes:

🐛 Reportar errores

💡 Sugerir mejoras

✨ Enviar pull requests

📌 Este proyecto es una base excelente para expandir funcionalidades de contactos.

