package com.marioreis.utils

import com.marioreis.configuration.Configuration
import java.io.*
import java.util.*


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
                    name = name.substring(0, name.lastIndexOf("."))
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

    fun split(filePath: String, splitLen: Long) {
        var leninfile: Long = 0
        var leng: Long = 0
        var count = 1
        var data: Int
        var filename = File(filePath)
        val infile: InputStream = BufferedInputStream(FileInputStream(filename))
        data = infile.read()
        val outputFile = Configuration.TEMP_DIRECTORY
        var name: String = filePath.substring(filePath.lastIndexOf("/") + 1)
        name = name.substring(0, name.lastIndexOf("."))

        while (data != -1) {
            filename = File("$outputFile/$name$count.txt")
            val outfile: OutputStream = BufferedOutputStream(FileOutputStream(filename))

            while (data != -1 && leng < splitLen) {
                outfile.write(data)
                leng++
                data = infile.read()
            }

            leninfile += leng
            leng = 0
            outfile.close()
            count++
        }

    }
    fun getFileNameFromPath(path: String): String {
        return path.substring(path.lastIndexOf("/") + 1)
    }
}