package com.marioreis.utils

import com.marioreis.configuration.Configuration
import java.io.*
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.io.path.Path


object FileUtils {
    fun generateFile(): File{
        val fileContent = StringBuilder();
        val cpfGen = GeraCpfCnpj();

        for ( i in 1..100 ){
            val cpf = cpfGen.cpf()
            val name = cpfGen.randomIdentifier()
            val r = System.lineSeparator()
            val line: String = "$cpf;$name$r"
            fileContent.append(line)
        }

        File(Configuration.BIG_FILE_NAME).writeText(fileContent.toString())
        val file = File(Configuration.BIG_FILE_NAME)
        return file
    }
    fun splitFileByNumber(path: String, length: Long){
        var inputStream: FileInputStream? = null
        var sc: Scanner? = null
        var count: Long = 0L
        var numberOfFile: Long = 0L

        try {
            inputStream = FileInputStream(path)
            var sc = Scanner(inputStream, "UTF-8")
            val builder = StringBuilder()

            while (sc.hasNextLine()) {
                val line: String = sc.nextLine()
                builder.append("$line${System.lineSeparator()}")
                count++

                if (count == length){
                    numberOfFile++
                    count = 0;
                    val outputFile = Configuration.TEMP_DIRECTORY
                    var name: String = path.substring(path.lastIndexOf("/") + 1)
                    name = getFileNameWithoutExtension(name)
                    File("$outputFile/$name$numberOfFile.txt").writeText(builder.toString())
                    builder.clear()
                }
            }

            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException()
            }
        } finally {
            inputStream?.close()
            if (sc != null) {
                sc.close()
            }
        }
    }

    private fun getFileNameWithoutExtension(name: String): String{
        return name.substring(0, name.lastIndexOf("."))
    }
    fun getFileNameFromPath(path: String): String {
        return path.substring(path.lastIndexOf(FileSystems.getDefault().getSeparator()) + 1)
    }
    private fun moveFile(source: String, dest: String){
        Files.move(Path(source), Path(dest), StandardCopyOption.REPLACE_EXISTING)
    }
    private fun moveFileToDest(source: String, dest: String){
        val fileName = getFileNameFromPath(source)
        val sentFile = "$dest${FileSystems.getDefault().getSeparator()}${fileName}"
        moveFile(source, sentFile)
    }
    fun moveFileToErrorFolder(source: String){
        moveFileToDest(source, Configuration.ERROR_FILES_DIRECTORY)
    }
    fun moveFileToSentFolder(source: String){
        moveFileToDest(source, Configuration.SENT_FILES_DIRECTORY)
    }
    private fun getErrorFileName(fileName: String): String{
        val fileNameWithoutExt = getFileNameWithoutExtension(fileName)
        val sufix = Configuration.FILE_ERROR_SUFIX
        val extension = Configuration.FILE_EXTENSION
        val path = Configuration.ERROR_FILES_DIRECTORY
        val directorySeparator = FileSystems.getDefault().getSeparator()
        return "${path}${directorySeparator}${fileNameWithoutExt}${sufix}${extension}"
    }
    fun createErrorFile(originalFileName: String, fileContent: String){
        File(getErrorFileName(originalFileName)).writeText(fileContent)
    }
}