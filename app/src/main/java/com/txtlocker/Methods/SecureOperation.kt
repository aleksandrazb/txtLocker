import android.content.Context
import android.util.Base64
import com.txtlocker.Methods.StorageOperation
import com.txtlocker.R
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class SecureOperation(private var applicationContext: Context) {

    private val cipherTransformation = "AES/GCM/NoPadding"
    private val cipherAlgorithm = "AES"
    private val keySizeBits = 512
    private val ivSizeBytes = 12 // 96 bits
    private var iv: ByteArray = ByteArray(ivSizeBytes)

    fun generateIV(directoryName: String) {
        iv = ByteArray(ivSizeBytes)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(iv)
        saveIV("$directoryName.iv.txt")
    }

    fun encrypt(directoryName: String, decryptedData: String, encryptionKey: String) {
        loadIV(directoryName)
        executeEncryption(decryptedData, encryptionKey)
    }

    fun decrypt(directoryName: String, encryptedData: String, decryptionKey: String) {
        loadIV(directoryName)
        executeDecryption(encryptedData, decryptionKey)
    }

    private fun saveIV(fileName: String) {
        val storage = StorageOperation(applicationContext, applicationContext.getString(R.string.main_note_storage))
        storage.saveByteArrayToFile(fileName, iv)
    }

    private fun loadIV(directoryName: String) {
        val storage = StorageOperation(applicationContext, applicationContext.getString(R.string.main_note_storage))
        iv = storage.getByteArrayFromFile("$directoryName.iv.txt", ivSizeBytes)
    }

    private fun executeEncryption(decryptedData: String, encryptionKey: String): String {
        val cipher = Cipher.getInstance(cipherTransformation)
        val keySpec = SecretKeySpec(encryptionKey.toByteArray(Charsets.UTF_8), cipherAlgorithm)
        val gcmSpec = GCMParameterSpec(keySizeBits, iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)
        val encryptedBytes = cipher.doFinal(decryptedData.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun executeDecryption(encryptedData: String, decryptionKey: String): String {
        val cipher = Cipher.getInstance(cipherTransformation)
        val keySpec = SecretKeySpec(decryptionKey.toByteArray(Charsets.UTF_8), cipherAlgorithm)
        val gcmSpec = GCMParameterSpec(keySizeBits, iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
        val encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
