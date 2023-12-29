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
import java.nio.file.Files
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
        val result = coroutineScope {
            files.map { f ->
                async {
                    insertCustomer(f.path, f.readLines())
                }
            }.awaitAll()
        }
    }

    exitProcess(0)
}

fun insertCustomer(completeFileName: String, lines: List<String>) {
    val customers = ArrayList<String>()
    customers.addAll(lines)
    val insertCustomer = InsertCustomerRequestDTO()
    insertCustomer.customers = customers
    val fileName = FileUtils.getFileNameFromPath(completeFileName)
    insertCustomer.fileName = fileName

    var call = RetrofitConfig.getCustomerService().insertCustomers(insertCustomer)
    call.enqueue(object : Callback<InsertCustomerResponseDTO> {
        override fun onResponse(call: Call<InsertCustomerResponseDTO>, response: Response<InsertCustomerResponseDTO>) {

            if (response.code() == 201) {
                FileUtils.moveFileToSentFolder(fileName)
            } else {
                FileUtils.moveFileToErrorFolder(fileName)
            }
        }

        override fun onFailure(call: Call<InsertCustomerResponseDTO>, t: Throwable) {
            println(t.message)
        }
    })
}