import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Path
import java.io.File
import java.nio.file.Files
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.serialization.*
import kotlinx.serialization.json.*

//Data class CSV
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
   while(mostrarMenu) {
    println("--------------------------------------" +
            "\n----------- MENÚ PRINCIPAL -----------" +
            "\n--------------------------------------" +
            "\n1. Gestión CSV" +
            "\n2. Gestión XML" +
            "\n3. Gestión JSON" +
            "\n0. Salir")

       val eleccion: Int = readln().toInt()
       when (eleccion) {
           0 -> mostrarMenu = false
           1 -> menuCSV()
           2 -> menuXML()
           3 -> menuJSON()
           else -> println("Opción inválida. Intenta con un número del menú.")
       }
   }
}
//MENUS
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

        val eleccion: Int = readln().toInt()
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

        val eleccion: Int = readln().toInt()
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
                    "\n2. Leer datos desde JSON" +
                    "\n0. Volver al menú principal"
        )

        val eleccion: Int = readln().toInt()
        when (eleccion) {
            0 -> mostrarMenuJSON = false
            1 -> println(leerJSON(entradaJSON))
            else -> println("Opción inválida. Intenta con un número del menú.")
        }
    }
}

//FUNCIONES CSV
    fun importarCSV() {
        val entradaCSV = Path.of("datos", "habitat.csv")
        val salidaCSV = Path.of("datos", "habitat2.csv")

        // Leer los datos estructurados del CSV y guardarlos en una lista de objetos Habitat
        val datos: List<Habitat> = leerDatosCSV(entradaCSV)

        // Mostrar por consola la información deserializada
        println("--- Información de la lista de Habitats")
        for (dato in datos) {
            println("  - ID: ${dato.id_habitat}, Nombre común: ${dato.nombre}, Clima: ${dato.clima}, Altitud: ${dato.altitud_media}, Temperatura: ${dato.temperatura_media}º")
        }

        // Guardar una copia procesada en un nuevo fichero CSV
        escribirCSV(salidaCSV)

    }

    fun leerDatosCSV(ruta: Path): List<Habitat> {
        var habitats: List<Habitat> = emptyList()

        if (!Files.isReadable(ruta)) {
            println("Error: No se puede leer el fichero en la ruta: $ruta")
        } else {
            val reader = csvReader {
                delimiter = ';'
            }

            // Leemos todas las filas del CSV (devuelve List<List<String>>)
            val filas: List<List<String>> = reader.readAll(ruta.toFile())

            // Convertimos las filas de texto en objetos Planta válidos
            habitats = filas.mapNotNull { columnas ->
                if (columnas.size >= 5) {
                    try {
                        val id_habitat = columnas[0].toInt()
                        val nombre = columnas[1]
                        val clima = columnas[2]
                        val altitud_media = columnas[3].toInt()
                        val temperatura_media = columnas[4].toDouble()
                        Habitat(id_habitat, nombre, clima, altitud_media, temperatura_media)
                    } catch (e: Exception) {
                        println("Fila inválida ignorada: $columnas -> Error: ${e.message}")
                        null
                    }
                } else {
                    println("Fila con formato incompleto ignorada: $columnas")
                    null
                }
            }
        }
        println("--- Información leída con éxito de: $ruta")
        return habitats
    }

    fun escribirCSV(ruta: Path) {

            val habitats = leerDatosCSV(ruta).toMutableList()
            val fichero: File = ruta.toFile()
            //Comparar id existentes para evitar duplicados

            var  bandera: Boolean = true

            while (bandera) {

                try {
                    println("Asigna un id valido")
                        val id: Int = readln().toInt()

                    if (habitats.any{it.id_habitat == id}){
                        println("Error id duplicado")
                    } else {
                        println("Asigna un nombre valido")
                        val nombre: String = readln()

                        println("Asigna un clima")
                        val clima: String = readln()

                        println("Asigna un altitud")
                        val altitud: Int = readln().toInt()

                        println("Asigna un temperatura")
                        val temperatura: Double = readln().toDouble()

                        val nuevoHabitat = Habitat(id, nombre, clima, altitud, temperatura)
                        habitats.add(nuevoHabitat)


                        csvWriter {
                            delimiter = ';'
                        }.writeAll(
                            habitats.map { habitat ->
                                listOf(
                                    habitat.id_habitat.toString(),
                                    habitat.nombre,
                                    habitat.clima,
                                    habitat.altitud_media.toString(),
                                    habitat.temperatura_media.toString()
                                )
                            },
                            fichero
                        )
                        println("--- Información guardada con éxito en: $fichero")
                        bandera = false
                    }
                }catch (e: Exception) {
                    println("Error al escribir el fichero CSV: ${e.message}")

            }
    }
        }
    fun modificarCSV(ruta: Path) {
        val habitats = leerDatosCSV(ruta).toMutableList()


        if(habitats.isEmpty()) {
            println("No hay datos en el fichero para modificar")
            return

        }else{
        println("\n--- MODIFICAR HABITAT EXISTENTE ---")
        //Pedir id
        var habitatSelecionado: Habitat? = null
        while (habitatSelecionado == null) {
            println("Introduce el ID del hábitat a modificar (0 para cancelar):")
            val idAModificar = readln().trim().toIntOrNull()

            if (idAModificar == 0) {return
            }else if (idAModificar == null) {
                println("Error: Debes introducir un número entero válido")
            }else{
                habitatSelecionado = habitats.find{it.id_habitat == idAModificar}
                if(habitatSelecionado == null){
                    println("Error: No se encontró el ID")
                }
            }

        }
            //Guardamos el id del objeto a modificar
            val habitatIndex = habitats.indexOf(habitatSelecionado)
            println("Modificando el habitat: $habitatSelecionado\nIntroduce los nuevos datos:")

            println("Nuevo nombre: ")
            val nuevoNombre = readln().trim()

            println("Nuevo clima: ")
            val nuevoClima = readln().trim()

            var banderaClima: Boolean = true
            var nuevaAltitud: Int = 0
            while (banderaClima) {
                println("Nueva altitud: ")
                val altitudA = readln().trim()
                val altitudB = altitudA.toIntOrNull()

                if (altitudB != null) {
                    nuevaAltitud = altitudB
                    banderaClima = false
                } else {
                    println("Error: la altitud tiene que ser un numero entero")
                }
            }
            var banderaTemperatura: Boolean = true
            var nuevaTemperatura: Double = 0.0
            while (banderaTemperatura) {
                println("Nuevo temperatura: ")
                val temperaturaA = readln().trim()
                val temperaturaB = temperaturaA.toDoubleOrNull()

                if (temperaturaB != null) {
                    nuevaTemperatura = temperaturaB
                    banderaTemperatura = false
                } else {
                    println("Error: la temperatura tiene que ser un double")
                }
            }

        val habitatModificado = Habitat(
            id_habitat = habitatSelecionado.id_habitat,
            nombre = nuevoNombre,
            clima = nuevoClima,
            altitud_media = nuevaAltitud,
            temperatura_media = nuevaTemperatura
        )
            habitats[habitatIndex] = habitatModificado

            //Reescribimos el fichero CSV
            try{
                val fichero: File = ruta.toFile()
                csvWriter {
                    delimiter = ';'
                }.writeAll(
                    habitats.map { habitat ->
                        listOf(
                            habitat.id_habitat.toString(),
                            habitat.nombre,
                            habitat.clima,
                            habitat.altitud_media.toString(),
                            habitat.temperatura_media.toString(),
                        )
                    },
                    fichero
                )
                println("Registro con ID ${habitatSelecionado.id_habitat} modificado correctamente")
            }catch (e: Exception){
                println("Error al guardar las modificaciones del habitat con ID ${habitatSelecionado.id_habitat}: ${e.message}")
            }
        }


    }

fun eliminarCSV(ruta: Path) {
    val habitats = leerDatosCSV(ruta).toMutableList()


    if (habitats.isEmpty()) {
        println("No hay datos en el fichero a eliminar")
        return

    } else {
        println("\n--- ELIMINAR HABITAT EXISTENTE ---")
        //Pedir id
        var habitatSelecionado: Habitat? = null
        while (habitatSelecionado == null) {
            println("Introduce el ID del hábitat a eliminar (0 para cancelar):")
            val idAEliminar = readln().trim().toIntOrNull()

            if (idAEliminar == 0) {
                return
            } else if (idAEliminar == null) {
                println("Error: Debes introducir un número entero válido")
            } else {
                habitatSelecionado = habitats.find { it.id_habitat == idAEliminar }
                if (habitatSelecionado == null) {
                    println("Error: No se encontró el ID")
                }
            }

        }
        habitats.remove(habitatSelecionado)
        try {
            val fichero: File = ruta.toFile()
            csvWriter {
                delimiter = ';'
            }.writeAll(
                habitats.map { habitat ->
                    listOf(
                        habitat.id_habitat.toString(),
                        habitat.nombre,
                        habitat.clima,
                        habitat.altitud_media.toString(),
                        habitat.temperatura_media.toString()
                    )
                },
                fichero
            )
            println("--- Registro con ID ${habitatSelecionado.id_habitat} eliminado con éxito de: $fichero")
        } catch (e: Exception) {
            println("Error al guardar los cambios en el fichero CSV: ${e.message}")
        }
    }

}
//FUNCIONES XML
fun leerDatosXML(ruta: Path): List<habitatXML> {

    var contenedor = HabitatsWrapper(emptyList())

    if (!Files.isReadable(ruta)) {
        println("Error: No se puede leer el fichero en la ruta: $ruta")
    } else {
        val fichero = ruta.toFile()
        val xmlMapper = XmlMapper().registerKotlinModule()

        // Leemos el XML directamente sobre la clase contenedora wrapper
        contenedor = xmlMapper.readValue(fichero)
        println("--- Información leída con éxito de: $ruta")
    }
    return contenedor.listaHabitats
}

//FUNCIONES JSON

fun leerJSON(ruta: Path): List<HabitatJSON> {

    var habitat: List<HabitatJSON> = emptyList()

    if (!Files.isReadable(ruta)) {
        println("Error: No se puede leer el fichero en la ruta: $ruta")
    } else {

        // Leemos el contenido completo del JSON como String
        val jsonString = Files.readString(ruta)

        // Convertimos de texto JSON a una lista de objetos Habitat
        habitat = Json.decodeFromString<List<HabitatJSON>>(jsonString)
        println("--- Información leída con éxito de: $ruta")
    }
    return habitat
}



