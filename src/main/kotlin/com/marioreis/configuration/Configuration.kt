package com.marioreis.configuration

import java.nio.file.FileSystems

object Configuration {
    val BIG_FILE_NAME = "/home/bat/work/temp/bigFile.txt"
    val TEMP_DIRECTORY = "/home/bat/work/temp/output"
    val BASE_URL = "http://localhost:8080/v1/"
    val SENT_FILES_DIRECTORY = "$TEMP_DIRECTORY${FileSystems.getDefault().getSeparator()}sent"
    val ERROR_FILES_DIRECTORY = "$TEMP_DIRECTORY${FileSystems.getDefault().getSeparator()}error"
}