import com.marioreis.configuration.Configuration
import com.marioreis.configuration.RetrofitConfig
import com.marioreis.domain.dto.InsertCustomerRequestDTO
import com.marioreis.domain.dto.InsertCustomerResponseDTO
import com.marioreis.utils.FileUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path

fun main() {
    FileUtils.generateFile()
    FileUtils.splitFileByNumber(Configuration.BIG_FILE_NAME, 10)
    val files = ArrayList<File>()
    File(Configuration.TEMP_DIRECTORY).walkBottomUp().forEach {

        if (it.isFile){
            files.add(it)
        }
    }

    runBlocking {
        val result = coroutineScope {
            files.map { f ->
                async {
                    insertCustomer(f.path, f.readLines())
                }
            }.awaitAll()
        }
    }
}

fun insertCustomer(completeFileName: String, lines: List<String>){
    val customers = ArrayList<String>()
    customers.addAll(lines)
    val insertCustomer = InsertCustomerRequestDTO()
    insertCustomer.customers = customers
    insertCustomer.fileName = FileUtils.getFileNameFromPath(completeFileName)

    var call = RetrofitConfig.getCustomerService().insertCustomers(insertCustomer)
    call.enqueue(object : Callback<InsertCustomerResponseDTO> {
        override fun onResponse(call: Call<InsertCustomerResponseDTO>, response: Response<InsertCustomerResponseDTO>) {

            if (response.code() == 201){
                val fileName = insertCustomer.fileName
                val sentFileDirectory =Configuration.SENT_FILES_DIRECTORY
                val sentFile = "${sentFileDirectory}${FileSystems.getDefault().getSeparator()}${fileName}"
                Files.move(Path(completeFileName), Path(sentFile), StandardCopyOption.REPLACE_EXISTING)
            }
        }

        override fun onFailure(call: Call<InsertCustomerResponseDTO>, t: Throwable) {
            println(t.message)
        }
    })
}

@Throws(IOException::class)
fun splitBySize(largeFile: File, maxChunkSize: Int): List<File> {
    val list: MutableList<File> = ArrayList()
    val path = largeFile.toPath()
    Files.newInputStream(path).use { `in` ->
        val buffer = ByteArray(maxChunkSize)
        var dataRead = `in`.read(buffer)

        while (dataRead > -1) {
            val fileChunk = stageFile(buffer, dataRead)
            list.add(fileChunk)
            dataRead = `in`.read(buffer)
        }
    }
    return list
}

@Throws(IOException::class)
private fun stageFile(buffer: ByteArray, length: Int): File {
    val tempDirectory = Configuration.TEMP_DIRECTORY
    val outPutFile = File.createTempFile("temp-", "-split", File(tempDirectory))
    FileOutputStream(outPutFile).use { fos ->
        fos.write(buffer, 0, length)
    }
    return outPutFile
}