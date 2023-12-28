package com.marioreis.utils

import com.marioreis.configuration.Configuration
import java.io.File

class GenerateFile {
    fun generateFile(): File{
        val fileContent = StringBuilder();
        val cpfGen = GeraCpfCnpj();

        for ( i in 0..1000 ){
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
}