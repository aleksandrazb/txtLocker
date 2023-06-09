import android.content.Context
import com.txtlocker.Methods.StorageOperation
import com.txtlocker.Models.Directory
import com.txtlocker.Models.Note
import com.txtlocker.R
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

import java.io.Serializable

//class SecureOperation(@Transient private var applicationContext: Context, private var pin: String): Serializable {
class SecureOperation(private var pin: String): Serializable {

    private val salt: ByteArray

    @Transient
    private var applicationContext: Context? = null


    fun setContext(context: Context) {
        this.applicationContext = context
        storage.setContext(applicationContext!!)
    }

    init {
        val secureRandom = SecureRandom()
        val saltBytes = ByteArray(16) // 16 bytes for salt
        secureRandom.nextBytes(saltBytes)
        salt = saltBytes
    }

    private val mainStorageName = "Main storage"
    private val storage: StorageOperation = StorageOperation(this.mainStorageName)
    private val cipherTransformation = "AES/GCM/NoPadding"
    private val cipherAlgorithm = "AES"
    private val keySizeBits = 128
    private val ivSizeBytes = 12 // 96 bits
    private var iv: ByteArray = ByteArray(this.ivSizeBytes)
    private var directories = ArrayList<Directory>()

    //Decrypting all directories content from app's main json file
    fun runAppDecryption(): Boolean {
        loadIV(mainStorageName)
        val decryptionKey = generateAESKeyFromPin(this.pin)
        val encryptedData = storage.getByteArrayFromFile("$mainStorageName.json")
        val decryptedData = decrypt(this.mainStorageName, encryptedData, decryptionKey)

        this.directories = storage.getDirectoriesFromString(decryptedData) //TODO:Fix decryptedData which doesn't contain directories
        return this.directories.size != 0

    }

    //Getting data of chosen directory loaded after running runAppDecryption()
    fun getDirectory(directoryName: String): Directory? {
        return this.directories.find { it.name == directoryName }
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
        }
    }

    //TODO:Encrypting all directories to one json file
    fun runAppCloseAction() {
        val encryptionKey = generateAESKeyFromPin(pin)
        val decryptedData = storage.getStringFromFile(mainStorageName)
        encrypt(mainStorageName, decryptedData, encryptionKey)
    }

    //TODO:Perform correct app reset encryption
    fun runAppFirstEncryption(secureOperation: SecureOperation) {
        storage.deleteAllData()
        //TODO:Replace runCheckIfNotesStorageExist() in StorageOperation with _NEW version
        storage.runCheckIfNotesStorageExist_NEW()
        generateIV(mainStorageName)

        val encryptionKey = secureOperation.generateAESKeyFromPin(pin)
        val decryptedData = storage.getStringFromFile(mainStorageName)
        secureOperation.encrypt(mainStorageName, decryptedData, encryptionKey) // keep encrypt function public!
    }

    fun getAllDirectories(): MutableList<String> {
        var directoriesList = mutableListOf<String>()
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

    fun deleteDirectory(unwantedDirectoryName: String) {
        directories.remove(directories.find { it.name == unwantedDirectoryName })
    }

    fun generateIV(directoryName: String) {
        iv = ByteArray(ivSizeBytes)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(iv)
        saveIV(directoryName)
    }

    private fun generateAESKeyFromPin(pin: String): ByteArray {
        /* val iterationCount = 10000 // Number of iterations for key derivation
        val keyLengthBits = 256 // Desired key length in bits

        val keySpec: KeySpec = PBEKeySpec(pin.toCharArray(), salt, iterationCount, keyLengthBits)
        val secretKeyFactory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val secretKey = secretKeyFactory.generateSecret(keySpec)
        */
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val pinBytes = pin.toByteArray(Charsets.UTF_8)
        val pom = messageDigest.digest(pinBytes)
        return messageDigest.digest(pom)
    }

    fun encrypt(directoryName: String, decryptedData: String, encryptionKey: ByteArray) {
        //loadIV(directoryName)
        executeEncryption(directoryName, decryptedData, encryptionKey)
    }

    private fun decrypt(directoryName: String, encryptedData: ByteArray, decryptionKey: ByteArray): String {
        loadIV(directoryName)
        return executeDecryption(encryptedData, decryptionKey)
    }

    private fun saveIV(fileName: String) {
        storage.saveByteArrayToFile("$fileName.iv.txt", iv)
    }

    private fun loadIV(directoryName: String) {
        val storage = StorageOperation(mainStorageName)
        storage.setContext(applicationContext!!)
        iv = storage.getByteArrayFromFile("$directoryName.iv.txt")
    }

    private fun executeEncryption(directoryName: String, decryptedData: String, encryptionKey: ByteArray) {
        val cipher = Cipher.getInstance(cipherTransformation)
        val keySpec = SecretKeySpec(encryptionKey, cipherAlgorithm)
        val ivParameterSpec = GCMParameterSpec(keySizeBits, iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec)
        val encryptedBytes = cipher.doFinal(decryptedData.toByteArray(Charsets.UTF_8))
        storage.saveByteArrayToFile("$directoryName.json", encryptedBytes)
    }

    private fun executeDecryption(encryptedData: ByteArray, decryptionKey: ByteArray): String {
        try {
            val cipher = Cipher.getInstance(cipherTransformation)
            val keySpec = SecretKeySpec(decryptionKey, cipherAlgorithm)
            val ivParameterSpec = GCMParameterSpec(keySizeBits, iv)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec)
            //val encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedData)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw RuntimeException("Decryption failed: ${ex.message}")
        }
    }
}
