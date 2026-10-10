import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Path
import java.io.File
import java.io.RandomAccessFile
import java.nio.file.Files
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.serialization.*
import kotlinx.serialization.json.*

// Data class CSV
data class Habitat(
    val id_habitat: Int,
    val nombre: String,
    val clima: String,
    val altitud_media: Int,
    val temperatura_media: Double
)

data class habitatXML(
    @JacksonXmlProperty(localName = "id_habitat")
    val idHabitat: Int,
    @JacksonXmlProperty(localName = "nombre")
    val nombre: String,
    @JacksonXmlProperty(localName = "clima")
    val clima: String,
    @JacksonXmlProperty(localName = "altitud_media")
    val altitud_media: Int,
    @JacksonXmlProperty(localName = "temperatura_media")
    val temperatura_media: Double
)

@JacksonXmlRootElement(localName = "habitats")
data class HabitatsWrapper(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "habitat")
    val listaHabitats: List<habitatXML> = emptyList()
)

@Serializable
data class HabitatJSON(
    @SerialName("id_habitat") val idHabitat: Int,
    @SerialName("nombre") val nombre: String,
    @SerialName("clima") val clima: String,
    @SerialName("altitud_media") val altitud_media: Int,
    @SerialName("temperatura") val temperatura: Double
)

fun main() {
    var mostrarMenu = true
    while (mostrarMenu) {
        println(
            "--------------------------------------" +
                    "\n----------- MENÚ PRINCIPAL -----------" +
                    "\n--------------------------------------" +
                    "\n1. Gestión CSV" +
                    "\n2. Gestión XML" +
                    "\n3. Gestión JSON" +
                    "\n10. Gestión fichero BIN" +
                    "\n0. Salir"
        )

        val eleccion: Int = readln().toIntOrNull() ?: -1
        when (eleccion) {
            0 -> mostrarMenu = false
            1 -> menuCSV()
            2 -> menuXML()
            3 -> menuJSON()
            10 -> menuBIN()
            else -> println("Opción inválida. Intenta con un número del menú.")
        }
    }
}

// MENUS
fun menuCSV() {
    val entradaCSV = Path.of("datos", "habitat.csv")
    var mostrarMenuCSV = true
    while (mostrarMenuCSV) {
        println(
            "--------------------------------------" +
                    "\n-------------- CRUD CSV --------------" +
                    "\n--------------------------------------" +
                    "\n1. Leer datos desde CSV" +
                    "\n2. Añadir un registro nuevo al final del fichero" +
                    "\n3. Modificar un registro existente (por ID)" +
                    "\n4. Eliminar un registro existente (por ID)" +
                    "\n0. Volver al menú principal"
        )

        val eleccion: Int = readln().toIntOrNull() ?: -1
        when (eleccion) {
            0 -> mostrarMenuCSV = false
            1 -> println(leerDatosCSV(entradaCSV))
            2 -> escribirCSV(entradaCSV)
            3 -> modificarCSV(entradaCSV)
            4 -> eliminarCSV(entradaCSV)
            else -> println("Opción inválida. Intenta con un número del menú.")
        }
    }
}

fun menuXML() {
    val entradaXML = Path.of("datos", "habitat.xml")
    var mostrarMenuXML = true
    while (mostrarMenuXML) {
        println(
            "--------------------------------------" +
                    "\n-------------- CRUD XML --------------" +
                    "\n--------------------------------------" +
                    "\n1. Leer datos desde XML" +
                    "\n0. Volver al menú principal"
        )

        val eleccion: Int = readln().toIntOrNull() ?: -1
        when (eleccion) {
            0 -> mostrarMenuXML = false
            1 -> println(leerDatosXML(entradaXML))
            else -> println("Opción inválida. Intenta con un número del menú.")
        }
    }
}

fun menuJSON() {
    val entradaJSON = Path.of("datos", "habitat.json")
    var mostrarMenuJSON = true
    while (mostrarMenuJSON) {
        println(
            "--------------------------------------" +
                    "\n-------------- CRUD JSON --------------" +
                    "\n--------------------------------------" +
                    "\n1. Leer datos desde JSON" +
                    "\n0. Volver al menú principal"
        )

        val eleccion: Int = readln().toIntOrNull() ?: -1
        when (eleccion) {
            0 -> mostrarMenuJSON = false
            1 -> println(leerJSON(entradaJSON))
            else -> println("Opción inválida. Intenta con un número del menú.")
        }
    }
}

fun menuBIN() {
    val rutaBin = Path.of("datos", "habitat.bin")
    val fuenteCSV = Path.of("datos", "habitat.csv")
    var mostrarMenuBIN = true

    while (mostrarMenuBIN) {
        println(
            "--------------------------------------" +
                    "\n---------- CRUD fichero BIN ----------" +
                    "\n--------------------------------------" +
                    "\n1. Importar datos desde fichero plano (CSV)" +
                    "\n2. Leer información del fichero binario" +
                    "\n3. Añadir un registro nuevo" +
                    "\n4. Modificar un registro existente (por ID)" +
                    "\n5. Eliminar un registro existente (por ID)" +
                    "\n0. Volver al menú principal"
        )

        val eleccion = readln().toIntOrNull() ?: -1
        when (eleccion) {
            0 -> mostrarMenuBIN = false
            1 -> importarBinario(fuenteCSV, rutaBin)
            2 -> leerBinario(rutaBin)
            3 -> agregarBinario(rutaBin)
            4 -> modificarBinario(rutaBin)
            5 -> eliminarBinario(rutaBin)
            else -> println("Opción inválida. Intenta con un número del menú.")
        }
    }
}

// FUNCIONES CSV
fun leerDatosCSV(ruta: Path): List<Habitat> {
    var habitats: List<Habitat> = emptyList()

    if (!Files.isReadable(ruta)) {
        println("Error: No se puede leer el fichero en la ruta: $ruta")
    } else {
        val reader = csvReader { delimiter = ';' }
        val filas: List<List<String>> = reader.readAll(ruta.toFile())

        habitats = filas.mapNotNull { columnas ->
            if (columnas.size >= 5) {
                try {
                    Habitat(
                        id_habitat = columnas[0].toInt(),
                        nombre = columnas[1],
                        clima = columnas[2],
                        altitud_media = columnas[3].toInt(),
                        temperatura_media = columnas[4].toDouble()
                    )
                } catch (e: Exception) {
                    null
                }
            } else null
        }
    }
    return habitats
}

fun escribirCSV(ruta: Path) {
    val habitats = leerDatosCSV(ruta).toMutableList()
    val fichero: File = ruta.toFile()
    var bandera = true

    while (bandera) {
        try {
            println("Asigna un ID válido:")
            val id = readln().toInt()

            if (habitats.any { it.id_habitat == id }) {
                println("Error: ID duplicado")
            } else {
                println("Asigna un nombre válido:")
                val nombre = readln()

                println("Asigna un clima:")
                val clima = readln()

                println("Asigna una altitud:")
                val altitud = readln().toInt()

                println("Asigna una temperatura:")
                val temperatura = readln().toDouble()

                habitats.add(Habitat(id, nombre, clima, altitud, temperatura))

                csvWriter { delimiter = ';' }.writeAll(
                    habitats.map { listOf(it.id_habitat.toString(), it.nombre, it.clima, it.altitud_media.toString(), it.temperatura_media.toString()) },
                    fichero
                )
                println("--- Información guardada con éxito en: $fichero")
                bandera = false
            }
        } catch (e: Exception) {
            println("Error al escribir el fichero CSV: ${e.message}")
        }
    }
}

fun modificarCSV(ruta: Path) {
    val habitats = leerDatosCSV(ruta).toMutableList()
    if (habitats.isEmpty()) {
        println("No hay datos en el fichero para modificar")
        return
    }

    println("\n--- MODIFICAR HABITAT EXISTENTE ---")
    var habitatSeleccionado: Habitat? = null
    while (habitatSeleccionado == null) {
        println("Introduce el ID del hábitat a modificar (0 para cancelar):")
        val idAModificar = readln().trim().toIntOrNull() ?: continue
        if (idAModificar == 0) return
        habitatSeleccionado = habitats.find { it.id_habitat == idAModificar }
        if (habitatSeleccionado == null) println("Error: No se encontró el ID")
    }

    val habitatIndex = habitats.indexOf(habitatSeleccionado)
    println("Modificando hábitat: $habitatSeleccionado\nIntroduce los nuevos datos:")

    println("Nuevo nombre:")
    val nuevoNombre = readln().trim()
    println("Nuevo clima:")
    val nuevoClima = readln().trim()

    var nuevaAltitud = 0
    while (true) {
        println("Nueva altitud:")
        val alt = readln().trim().toIntOrNull()
        if (alt != null) { nuevaAltitud = alt; break }
        println("Error: Debe ser un número entero.")
    }

    var nuevaTemperatura = 0.0
    while (true) {
        println("Nueva temperatura:")
        val temp = readln().trim().toDoubleOrNull()
        if (temp != null) { nuevaTemperatura = temp; break }
        println("Error: Debe ser un número válido (Double).")
    }

    habitats[habitatIndex] = Habitat(habitatSeleccionado.id_habitat, nuevoNombre, nuevoClima, nuevaAltitud, nuevaTemperatura)

    try {
        csvWriter { delimiter = ';' }.writeAll(
            habitats.map { listOf(it.id_habitat.toString(), it.nombre, it.clima, it.altitud_media.toString(), it.temperatura_media.toString()) },
            ruta.toFile()
        )
        println("Registro modificado correctamente.")
    } catch (e: Exception) {
        println("Error al guardar: ${e.message}")
    }
}

fun eliminarCSV(ruta: Path) {
    val habitats = leerDatosCSV(ruta).toMutableList()
    if (habitats.isEmpty()) {
        println("No hay datos para eliminar.")
        return
    }

    println("\n--- ELIMINAR HABITAT EXISTENTE ---")
    var habitatSeleccionado: Habitat? = null
    while (habitatSeleccionado == null) {
        println("Introduce el ID del hábitat a eliminar (0 para cancelar):")
        val idAEliminar = readln().trim().toIntOrNull() ?: continue
        if (idAEliminar == 0) return
        habitatSeleccionado = habitats.find { it.id_habitat == idAEliminar }
        if (habitatSeleccionado == null) println("Error: No se encontró el ID")
    }

    habitats.remove(habitatSeleccionado)
    try {
        csvWriter { delimiter = ';' }.writeAll(
            habitats.map { listOf(it.id_habitat.toString(), it.nombre, it.clima, it.altitud_media.toString(), it.temperatura_media.toString()) },
            ruta.toFile()
        )
        println("Registro eliminado con éxito.")
    } catch (e: Exception) {
        println("Error al guardar: ${e.message}")
    }
}

// FUNCIONES XML Y JSON
fun leerDatosXML(ruta: Path): List<habitatXML> {
    var contenedor = HabitatsWrapper(emptyList())
    if (Files.isReadable(ruta)) {
        val xmlMapper = XmlMapper().registerKotlinModule()
        contenedor = xmlMapper.readValue(ruta.toFile())
    }
    return contenedor.listaHabitats
}

fun leerJSON(ruta: Path): List<HabitatJSON> {
    var habitat: List<HabitatJSON> = emptyList()
    if (Files.isReadable(ruta)) {
        val jsonString = Files.readString(ruta)
        habitat = Json.decodeFromString<List<HabitatJSON>>(jsonString)
    }
    return habitat
}

// ==========================================
// FUNCIONES FICHERO BINARIO (Acceso Aleatorio)
// ==========================================

const val TAM_NOMBRE = 30
const val TAM_CLIMA = 20
// Cálculo de tamaño de registro fijo:
// id_habitat (Int = 4 bytes)
// nombre (30 chars * 2 bytes = 60 bytes)
// clima (20 chars * 2 bytes = 40 bytes)
// altitud_media (Int = 4 bytes)
// temperatura_media (Double = 8 bytes)
const val TAM_REGISTRO = 4 + (TAM_NOMBRE * 2) + (TAM_CLIMA * 2) + 4 + 8

fun escribirStringFijo(raf: RandomAccessFile, texto: String, longitud: Int) {
    val padded = texto.padEnd(longitud).take(longitud)
    for (char in padded) {
        raf.writeChar(char.code)
    }
}

fun leerStringFijo(raf: RandomAccessFile, longitud: Int): String {
    val sb = StringBuilder()
    for (i in 0 until longitud) {
        sb.append(raf.readChar())
    }
    return sb.toString().trim()
}

fun importarBinario(origen: Path, destino: Path) {
    val lista = leerDatosCSV(origen)
    if (lista.isEmpty()) {
        println("No hay datos en el fichero origen para importar.")
        return
    }

    try {
        // Borrar o recrear el binario
        val archivo = destino.toFile()
        if (archivo.exists()) archivo.delete()

        RandomAccessFile(archivo, "rw").use { raf ->
            for (h in lista) {
                raf.writeInt(h.id_habitat)
                escribirStringFijo(raf, h.nombre, TAM_NOMBRE)
                escribirStringFijo(raf, h.clima, TAM_CLIMA)
                raf.writeInt(h.altitud_media)
                raf.writeDouble(h.temperatura_media)
            }
        }
        println("--- Datos importados correctamente al fichero binario ($destino).")
    } catch (e: Exception) {
        println("Error al importar en el fichero binario: ${e.message}")
    }
}

fun leerBinario(ruta: Path) {
    val archivo = ruta.toFile()
    if (!archivo.exists() || archivo.length() == 0L) {
        println("El fichero binario está vacío o no existe.")
        return
    }

    println("\n--- CONTENIDO DEL FICHERO BINARIO ---")
    try {
        RandomAccessFile(archivo, "r").use { raf ->
            while (raf.filePointer < raf.length()) {
                val id = raf.readInt()
                val nombre = leerStringFijo(raf, TAM_NOMBRE)
                val clima = leerStringFijo(raf, TAM_CLIMA)
                val altitud = raf.readInt()
                val temperatura = raf.readDouble()

                println("ID: $id | Nombre: $nombre | Clima: $clima | Altitud: $altid | Temperatura: $temperatura ºC")
            }
        }
    } catch (e: Exception) {
        println("Error al leer el fichero binario: ${e.message}")
    }
}

fun agregarBinario(ruta: Path) {
    val archivo = ruta.toFile()

    var idValido: Int
    while (true) {
        println("Introduce un ID válido (número entero y que no exista):")
        val input = readln().trim().toIntOrNull()
        if (input == null) {
            println("Error: Debe ser un número entero.")
            continue
        }

        // Comprobar si existe en el binario
        var existe = false
        if (archivo.exists() && archivo.length() > 0L) {
            RandomAccessFile(archivo, "r").use { raf ->
                while (raf.filePointer < raf.length()) {
                    val idActual = raf.readInt()
                    if (idActual == input) {
                        existe = true
                        break
                    }
                    raf.seek(raf.filePointer + TAM_REGISTRO - 4)
                }
            }
        }

        if (existe) {
            println("Error: El ID ya existe en el fichero binario.")
        } else {
            idValido = input
            break
        }
    }

    println("Introduce el nombre:")
    val nombre = readln().trim()

    println("Introduce el clima:")
    val clima = readln().trim()

    var altitudValida = 0
    while (true) {
        println("Introduce la altitud (número entero):")
        val alt = readln().trim().toIntOrNull()
        if (alt != null) { altitudValida = alt; break }
        println("Error: Altitud inválida.")
    }

    var temperaturaValida = 0.0
    while (true) {
        println("Introduce la temperatura (número decimal):")
        val temp = readln().trim().toDoubleOrNull()
        if (temp != null) { temperaturaValida = temp; break }
        println("Error: Temperatura inválida.")
    }

    try {
        RandomAccessFile(archivo, "rw").use { raf ->
            raf.seek(raf.length()) // Ir al final
            raf.writeInt(idValido)
            escribirStringFijo(raf, nombre, TAM_NOMBRE)
            escribirStringFijo(raf, clima, TAM_CLIMA)
            raf.writeInt(altitudValida)
            raf.writeDouble(temperaturaValida)
        }
        println("Registro añadido correctamente al final del fichero binario.")
    } catch (e: Exception) {
        println("Error al añadir registro: ${e.message}")
    }
}

fun modificarBinario(ruta: Path) {
    val archivo = ruta.toFile()
    if (!archivo.exists() || archivo.length() == 0L) {
        println("El fichero binario está vacío o no existe.")
        return
    }

    var idAModificar: Int
    while (true) {
        println("Introduce el ID del hábitat a modificar:")
        val input = readln().trim().toIntOrNull()
        if (input != null) {
            idAModificar = input
            break
        }
        println("Error: Debe introducir un número entero válido.")
    }

    try {
        RandomAccessFile(archivo, "rw").use { raf ->
            var encontrado = false
            var posicionRegistro = 0L

            while (raf.filePointer < raf.length()) {
                posicionRegistro = raf.filePointer
                val id = raf.readInt()
                val nombreActual = leerStringFijo(raf, TAM_NOMBRE)

                if (id == idAModificar) {
                    encontrado = true
                    println("Encontrado hábitat: [ID: $id, Nombre: $nombreActual]")
                    break
                }
                raf.seek(raf.filePointer + TAM_REGISTRO - 4)
            }

            if (!encontrado) {
                println("No se encontró ningún registro con el ID $idAModificar.")
                return
            }

            println("Introduce el nuevo nombre:")
            val nuevoNombre = readln().trim()
            println("Introduce el nuevo clima:")
            val nuevoClima = readln().trim()

            var nuevaAltitud = 0
            while (true) {
                println("Introduce la nueva altitud (entero):")
                val alt = readln().trim().toIntOrNull()
                if (alt != null) { nuevaAltitud = alt; break }
                println("Error: Valor inválido.")
            }

            var nuevaTemperatura = 0.0
            while (true) {
                println("Introduce la nueva temperatura (decimal):")
                val temp = readln().trim().toDoubleOrNull()
                if (temp != null) { nuevaTemperatura = temp; break }
                println("Error: Valor inválido.")
            }

            // Sobrescribir en la posición exacta
            raf.seek(posicionRegistro)
            raf.writeInt(idAModificar)
            escribirStringFijo(raf, nuevoNombre, TAM_NOMBRE)
            escribirStringFijo(raf, nuevoClima, TAM_CLIMA)
            raf.writeInt(nuevaAltitud)
            raf.writeDouble(nuevaTemperatura)

            println("Registro con ID $idAModificar modificado con éxito.")
        }
    } catch (e: Exception) {
        println("Error al modificar el registro: ${e.message}")
    }
}

fun eliminarBinario(ruta: Path) {
    val archivo = ruta.toFile()
    if (!archivo.exists() || archivo.length() == 0L) {
        println("El fichero binario está vacío o no existe.")
        return
    }

    var idAEliminar: Int
    while (true) {
        println("Introduce el ID del hábitat a eliminar:")
        val input = readln().trim().toIntOrNull()
        if (input != null) {
            idAEliminar = input
            break
        }
        println("Error: Debe introducir un número entero válido.")
    }

    try {
        var encontrado = false
        var nombreEncontrado = ""
        val registrosRestantes = mutableListOf<ByteArray>()

        // Leer todos y filtrar el que se quiere eliminar
        RandomAccessFile(archivo, "r").use { raf ->
            while (raf.filePointer < raf.length()) {
                val buffer = ByteArray(TAM_REGISTRO)
                raf.readFully(buffer)

                // Extraer ID para comprobar
                val id = java.nio.ByteBuffer.wrap(buffer, 0, 4).int
                if (id == idAEliminar) {
                    encontrado = true
                    // Extraer nombre para mostrarlo (offset 4, longitud 30 chars * 2 = 60 bytes)
                    val charBuffer = java.nio.CharBuffer.allocate(TAM_NOMBRE)
                    val decoder = java.nio.charset.StandardCharsets.UTF_16.newDecoder()
                    // Usar un stream de bytes para leer el string fijo
                    val bis = java.io.ByteArrayInputStream(buffer, 4, TAM_NOMBRE * 2)
                    val dis = java.io.DataInputStream(bis)
                    val sb = StringBuilder()
                    for (i in 0 until TAM_NOMBRE) {
                        sb.append(dis.readChar())
                    }
                    nombreEncontrado = sb.toString().trim()
                } else {
                    registrosRestantes.add(buffer)
                }
            }
        }

        if (!encontrado) {
            println("No se encontró ningún registro con el ID $idAEliminar.")
            return
        }

        println("Se va a eliminar el hábitat con ID $idAEliminar y Nombre '$nombreEncontrado'. ¿Estás seguro? (s/n):")
        val confirmacion = readln().trim().lowercase()

        if (confirmacion == "s" || confirmacion == "si" || confirmacion == "sí") {
            // Reescribir el fichero sin el registro eliminado
            RandomAccessFile(archivo, "rw").use { raf ->
                raf.setLength(0) // Truncar el archivo
                for (reg in registrosRestantes) {
                    raf.write(reg)
                }
            }
            println("Registro eliminado con éxito.")
        } else {
            println("Operación de borrado cancelada.")
        }
    } catch (e: Exception) {
        println("Error al eliminar el registro: ${e.message}")
    }
}