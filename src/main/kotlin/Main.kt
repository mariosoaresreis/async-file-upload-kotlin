import com.fasterxml.jackson.databind.ObjectMapper
import com.marioreis.configuration.Configuration
import com.marioreis.configuration.RetrofitConfig
import com.marioreis.configuration.await
import com.marioreis.domain.dto.InsertCustomerRequestDTO
import com.marioreis.utils.FileUtils
import kotlinx.coroutines.*
import java.io.File
import kotlin.system.exitProcess

fun main() {
    FileUtils.generateFile()
    FileUtils.splitFileByNumber(Configuration.BIG_FILE_NAME, 10)
    val files = ArrayList<File>()
    File(Configuration.TEMP_DIRECTORY).walkBottomUp()
        .onFail { file, ex -> println("ERROR: $file caused $ex") }
        .forEach {
            if (it.isFile) {
                files.add(it)
            }
        }

    runBlocking {
        try {
            val results = coroutineScope {
                files.map { f ->
                    async {
                        insertCustomer(f.path)
                    }
                }.awaitAll()
            }
            println("Processed ${results.count { it }} files successfully")
        } catch (e: Exception) {
            println("ERROR: ${e.message}")
            e.printStackTrace()
        }
    }

    exitProcess(0)
}

suspend fun insertCustomer(completeFileName: String): Boolean = try {
    // Read file lines asynchronously with buffered reader
    val lines = FileUtils.readFileLinesChunked(completeFileName)

    if (lines.isEmpty()) {
        println("WARN: No lines read from $completeFileName")
        false
    } else {
        val fileName = FileUtils.getFileNameFromPath(completeFileName)
        val insertCustomerRequest = InsertCustomerRequestDTO().apply {
            customers = ArrayList(lines)
            this.fileName = fileName
        }

        // Make the API call using suspend function
        try {
            RetrofitConfig.getCustomerService().insertCustomers(insertCustomerRequest).await()
            // Handle successful response
            println("SUCCESS: Inserted customers from $fileName")
            FileUtils.moveFileToSentFolder(completeFileName)
            true
        } catch (e: Exception) {
            println("ERROR: Failed to insert customers from $fileName: ${e.message}")
            FileUtils.moveFileToErrorFolder(completeFileName)
            val mapper = ObjectMapper()
            val errorContent = mapper.writeValueAsString(mapOf("error" to e.message))
            FileUtils.createErrorFile(fileName, errorContent)
            false
        }
    }
} catch (e: Exception) {
    println("ERROR: Exception processing $completeFileName: ${e.message}")
    e.printStackTrace()
    false
}