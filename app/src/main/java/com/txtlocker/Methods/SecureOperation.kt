import android.content.Context
import android.util.Base64
import com.txtlocker.Methods.StorageOperation
import com.txtlocker.Models.Directory
import com.txtlocker.R
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class SecureOperation(private var applicationContext: Context, private var pin: String) {

    private val salt: ByteArray

    init {
        val secureRandom = SecureRandom()
        val saltBytes = ByteArray(16) // 16 bytes for salt
        secureRandom.nextBytes(saltBytes)
        salt = saltBytes
    }

    private val mainStorageName = this.applicationContext.getString(R.string.main_note_storage)
    private val storage = StorageOperation(this.applicationContext, this.mainStorageName)
    private val cipherTransformation = "AES/GCM/NoPadding"
    private val cipherAlgorithm = "AES"
    private val keySizeBits = 512
    private val ivSizeBytes = 12 // 96 bits
    private var iv: ByteArray = ByteArray(this.ivSizeBytes)
    private var directories = ArrayList<Directory>()

    //Decrypting all directories content from app's main json file
    fun runAppDecryption() {
        val decryptionKey = generateAESKeyFromPin(this.pin)
        val encryptedData = this.storage.getStringFromFile(this.mainStorageName)
        val decryptedData = decrypt(this.mainStorageName, encryptedData, decryptionKey)

        this.directories = storage.getDirectoriesFromString(decryptedData)
    }

    //Getting data of chosen directory loaded after running runAppDecryption()
    fun getDirectory(directoryName: String): Directory? {
        return this.directories.find { it.name == directoryName }
    }

    //TODO:Encrypting all directories to one json file
    fun runAppEncryption() {
        val encryptionKey = generateAESKeyFromPin(pin)
        val decryptedData = storage.getStringFromFile(mainStorageName)
        val encrypted = encrypt(mainStorageName, decryptedData, encryptionKey)
    }

    fun generateIV(directoryName: String) {
        iv = ByteArray(ivSizeBytes)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(iv)
        saveIV("$directoryName.iv.txt")
    }

    private fun generateAESKeyFromPin(pin: String): ByteArray {
        val iterationCount = 10000 // Number of iterations for key derivation
        val keyLengthBits = 512 // Desired key length in bits

        val keySpec: KeySpec = PBEKeySpec(pin.toCharArray(), salt, iterationCount, keyLengthBits)
        val secretKeyFactory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val secretKey = secretKeyFactory.generateSecret(keySpec)

        return secretKey.encoded
    }

    fun encrypt(directoryName: String, decryptedData: String, encryptionKey: ByteArray) {
        loadIV(directoryName)
        executeEncryption(decryptedData, encryptionKey)
    }

    private fun decrypt(directoryName: String, encryptedData: String, decryptionKey: ByteArray): String {
        loadIV(directoryName)
        return executeDecryption(encryptedData, decryptionKey)
    }

    private fun saveIV(fileName: String) {
        storage.saveByteArrayToFile(fileName, iv)
    }

    private fun loadIV(directoryName: String) {
        val storage = StorageOperation(applicationContext, mainStorageName)
        iv = storage.getByteArrayFromFile("$directoryName.iv.txt", ivSizeBytes)
    }

    private fun executeEncryption(decryptedData: String, encryptionKey: ByteArray): String {
        val cipher = Cipher.getInstance(cipherTransformation)
        val keySpec = SecretKeySpec(encryptionKey, cipherAlgorithm)
        val gcmSpec = GCMParameterSpec(keySizeBits, iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)
        val encryptedBytes = cipher.doFinal(decryptedData.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun executeDecryption(encryptedData: String, decryptionKey: ByteArray): String {
        val cipher = Cipher.getInstance(cipherTransformation)
        val keySpec = SecretKeySpec(decryptionKey, cipherAlgorithm)
        val gcmSpec = GCMParameterSpec(keySizeBits, iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
        val encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
