import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.txtlocker.Methods.StorageOperation
import com.txtlocker.Models.Directory
import com.txtlocker.Models.Note
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

import java.io.Serializable


class SecureOperation(private var pin: String): Serializable {

    @Transient
    private var applicationContext: Context? = null
    fun setContext(context: Context) {
        this.applicationContext = context
        storage.setContext(applicationContext!!)
    }

    private val salt: ByteArray
    init {
        val secureRandom = SecureRandom()
        val saltBytes = ByteArray(16)
        secureRandom.nextBytes(saltBytes)
        salt = saltBytes
    }

    private val mainStorageName = "Main storage"
    private val storage: StorageOperation = StorageOperation(this.mainStorageName)
    private val cipherTransformation = "AES/GCM/NoPadding"
    private val cipherAlgorithm = "AES"
    private val keySizeBits = 128
    private val ivSizeBytes = 12
    private var iv: ByteArray = ByteArray(this.ivSizeBytes)
    private var directories = ArrayList<Directory>()


    fun runAppFirstEncryption(secureOperation: SecureOperation) {
        val decryptedData = storage.runResetStorageUnencrypted()
        generateAndSaveNewIVForNewDirectory(mainStorageName)

        val encryptionKey = secureOperation.getExtendedPin(pin)
        val encryptedStorage = secureOperation.encrypt(decryptedData, encryptionKey)
        storage.saveByteArrayToFile("$mainStorageName.txt", encryptedStorage)
    }

    //Generate new random IV for the new directory and save it as "$directoryName.iv.txt"
    private fun generateAndSaveNewIVForNewDirectory(directoryName: String) {
        iv = ByteArray(ivSizeBytes)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(iv)
        storage.saveByteArrayToFile("$directoryName.iv.txt", iv)
    }

    private fun generateAndSaveNewIVForExportedDirectory(path: String, directoryName: String) {
        iv = ByteArray(ivSizeBytes)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(iv)
        storage.saveByteArrayToExternalFile("$path/$directoryName.export.iv.txt", iv)
    }

    //Extend the pin to 256 ByteArray needed for AES
    private fun getExtendedPin(pin: String): ByteArray {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val pinBytes = pin.toByteArray(Charsets.UTF_8)
        val pom = messageDigest.digest(pinBytes)
        return messageDigest.digest(pom)
    }

    fun encrypt(decryptedData: ByteArray, encryptionKey: ByteArray): ByteArray {
        return executeEncryption(decryptedData, encryptionKey)
    }

    private fun executeEncryption(decryptedData: ByteArray, encryptionKey: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(cipherTransformation)
        val keySpec = SecretKeySpec(encryptionKey, cipherAlgorithm)
        val ivParameterSpec = GCMParameterSpec(keySizeBits, iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec)
        return cipher.doFinal(decryptedData)
    }

    //Decrypting all directories content from app's main json file
    fun runAppDecryption(): Boolean {
        //loadIV(mainStorageName)
        val decryptionKey = getExtendedPin(this.pin)
        val encryptedData = storage.getByteArrayFromFile("$mainStorageName.txt")
        val decryptedData = decrypt(this.mainStorageName, encryptedData, decryptionKey)

        this.directories = storage.getDirectoriesFromByteArray(decryptedData)
        return this.directories.size > 0
    }

    private fun loadIV(directoryName: String) {
        val storage = StorageOperation(mainStorageName)
        storage.setContext(applicationContext!!)
        iv = storage.getByteArrayFromFile("$directoryName.iv.txt")
    }

    private fun decrypt(directoryName: String, encryptedData: ByteArray, decryptionKey: ByteArray): ByteArray {
        loadIV(directoryName)
        return executeDecryption(encryptedData, decryptionKey)
    }

    private fun executeDecryption(encryptedData: ByteArray, decryptionKey: ByteArray): ByteArray {
        try {
            val cipher = Cipher.getInstance(cipherTransformation)
            val keySpec = SecretKeySpec(decryptionKey, cipherAlgorithm)
            val ivParameterSpec = GCMParameterSpec(keySizeBits, iv)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec)
            return cipher.doFinal(encryptedData)
        } catch (ex: Exception) {
            ex.printStackTrace()
            //throw RuntimeException("Decryption failed: ${ex.message}")
            return ByteArray(0)
        }
    }

    fun getAllDirectories(): MutableList<String> {
        var directoriesList = mutableListOf<String>()

        for (directory: Directory in directories) {
            directoriesList.add(directory.name)
        }
        return directoriesList
    }

    fun addDirectory(newDirectoryName: String, isEncrypted: Boolean) {
        val exampleNotes = arrayListOf<Note>(
            Note("ExampleTitle1", "ExampleNote1"),
            Note("ExampleTitle2", "ExampleNote2"),
            Note("ExampleTitle3", "ExampleNote3"),
            Note("ExampleTitle4", "ExampleNote4")
        )
        directories.add(Directory(newDirectoryName, isEncrypted, exampleNotes))
    }

    //TODO:Encrypting all directories to one json file
    fun runSaveAllDirectories() {
        val encryptionKey = getExtendedPin(pin)
        val decryptedStorage = getByteArrayOfAllDirectories()
        val encryptedStorage = encrypt(decryptedStorage, encryptionKey)
        storage.saveByteArrayToFile("$mainStorageName.txt", encryptedStorage)
    }

    private fun getByteArrayOfAllDirectories(): ByteArray {
        val gson = Gson()
        return gson.toJson(directories).toByteArray()
    }

    fun deleteDirectory(unwantedDirectoryName: String): Boolean {
        return if (directories.find { it.name == unwantedDirectoryName } != null) {
            directories.remove(directories.find { it.name == unwantedDirectoryName })
            runSaveAllDirectories()
            true
        } else {
            false
        }
    }

    fun getNotes(directoryName: String): ArrayList<Note> {
        val directory = directories.find { it.name == directoryName }
        return if (directory != null) {
            val notes = directory.notes
            notes
        } else {
            arrayListOf()
        }
    }

    fun saveChangedNotes(directoryName: String, notes: ArrayList<Note>) {
        val directory = directories.find { it.name == directoryName }
        if (directory != null) {
            directory.notes = notes
            runSaveAllDirectories()
        }
    }

    fun saveExport(directoryName: String, notes: ArrayList<Note>): Boolean {
        val directories = arrayListOf<Directory>(
            Directory(directoryName, false, notes)
        )
        val documentsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath

        try {
            val gson = Gson()
            var exportedData = gson.toJson(directories).toByteArray()

            if (pin != "") {
                val encryptionKey = getExtendedPin(pin)
                generateAndSaveNewIVForExportedDirectory(documentsPath, "$directoryName.export")
                exportedData = encrypt(exportedData, encryptionKey)
            }

            return storage.saveByteArrayToExternalFile("$documentsPath/$directoryName.export.txt", exportedData)
        }
        catch (e: IOException) {
            return false
        }

    }

    //----------------------------------------------------------------------------------------------


    //Getting data of chosen directory loaded after running runAppDecryption()
    fun getDirectory(directoryName: String): Directory? {
        return this.directories.find { it.name == directoryName }
    }

}
