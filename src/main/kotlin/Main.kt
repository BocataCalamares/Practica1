import java.nio.file.Path
import java.io.File
import java.nio.file.Files
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.util.Scanner


data class Habitat(
    val id_habitat: Int,
    val nombre: String,
    val clima: String,
    val altitud_media: Int,
    val temperatura_media: Double
)

fun main() {
    var mostrarMenu = true
   while(mostrarMenu) {
    println("--------------------------------------" +
            "\n----------- MENÚ PRINCIPAL -----------" +
            "\n--------------------------------------" +
            "\n1. Gestión CSV" +
            "\n0. Salir")

       val eleccion: Int = readln().toInt()
       when (eleccion) {
           0 -> mostrarMenu = false
           1 -> menuCSV()
           else -> println("Opción inválida. Intenta con un número del menú.")
       }
   }
}
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
            3 -> println("TO DO")
            4 -> println("TO DO")
            else -> println("Opción inválida. Intenta con un número del menú.")
        }
    }
}
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
        }


    }




