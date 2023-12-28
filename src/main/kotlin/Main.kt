import com.marioreis.configuration.Configuration
import com.marioreis.configuration.RetrofitConfig
import com.marioreis.domain.dto.InsertCustomerRequestDTO
import com.marioreis.domain.dto.InsertCustomerResponseDTO
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.nio.file.Files

fun main() {
    val customers = ArrayList<String>()
    customers.add("69505713215;MARIO AUGUSTO REIS")
    val insertCustomer = InsertCustomerRequestDTO()
    insertCustomer.customers = customers
    insertCustomer.fileName = "teste.txt"

    var call = RetrofitConfig.getCustomerService().insertCustomers(insertCustomer)
    call.enqueue(object : Callback<InsertCustomerResponseDTO> {
        override fun onResponse(call: Call<InsertCustomerResponseDTO>, response: Response<InsertCustomerResponseDTO>) {
            val body = response.body()
            println(body?.fileName)
        }

        override fun onFailure(call: Call<InsertCustomerResponseDTO>, t: Throwable) {
            // tratar algum erro
            println(t.message)
        }
    })

}

fun callService(){
    val customers = ArrayList<String>()
    customers.add("69505713215;MARIO AUGUSTO REIS")
    val insertCustomer = InsertCustomerRequestDTO()
    insertCustomer.customers = customers
    insertCustomer.fileName = "teste.txt"

    var call = RetrofitConfig.getCustomerService().insertCustomers(insertCustomer)
    call.enqueue(object : Callback<InsertCustomerResponseDTO> {
        override fun onResponse(call: Call<InsertCustomerResponseDTO>, response: Response<InsertCustomerResponseDTO>) {
            val body = response.body()
            println(body?.fileName)
        }

        override fun onFailure(call: Call<InsertCustomerResponseDTO>, t: Throwable) {
            // tratar algum erro
            println(t.message)
        }
    })
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