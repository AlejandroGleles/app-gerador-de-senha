package br.gonzaga.app_geradorsenha

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import java.security.SecureRandom

class MainActivity : AppCompatActivity() {

    private lateinit var passwordLengthSeekBar: SeekBar
    private lateinit var passwordLengthValue: TextView
    private lateinit var includeUppercase: CheckBox
    private lateinit var includeLowercase: CheckBox
    private lateinit var includeNumbers: CheckBox
    private lateinit var excludeSimilarChars: CheckBox
    private lateinit var generatedPassword: TextView

    private val secureRandom = SecureRandom()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        passwordLengthSeekBar = findViewById(R.id.password_length_seekbar)
        passwordLengthValue = findViewById(R.id.password_length_value)
        includeUppercase = findViewById(R.id.include_uppercase)
        includeLowercase = findViewById(R.id.include_lowercase)
        includeNumbers = findViewById(R.id.include_numbers)
        excludeSimilarChars = findViewById(R.id.exclude_similar_chars)
        generatedPassword = findViewById(R.id.generated_password)

        passwordLengthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                passwordLengthValue.text = "$progress caracteres"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        findViewById<Button>(R.id.generate_password_button).setOnClickListener {
            val passwordLength = passwordLengthSeekBar.progress
            generatedPassword.text = generatePassword(passwordLength)
        }

        findViewById<Button>(R.id.copy_password_button).setOnClickListener {
            copyToClipboard(generatedPassword.text.toString())
        }

        findViewById<Button>(R.id.regenerate_password_button).setOnClickListener {
            val passwordLength = passwordLengthSeekBar.progress
            generatedPassword.text = generatePassword(passwordLength)
        }
    }

    private fun generatePassword(length: Int): String {
        val upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowerCaseChars = "abcdefghijklmnopqrstuvwxyz"
        val numberChars = "0123456789"
        val similarChars = "l1o0"
        val allChars = StringBuilder()

        if (includeUppercase.isChecked) allChars.append(upperCaseChars)
        if (includeLowercase.isChecked) allChars.append(lowerCaseChars)
        if (includeNumbers.isChecked) allChars.append(numberChars)
        if (excludeSimilarChars.isChecked) {
            val filteredChars = allChars.filter { !similarChars.contains(it) }
            allChars.clear()
            allChars.append(filteredChars)
        }

        if (allChars.isEmpty()) return ""

        val password = StringBuilder(length)
        for (i in 0 until length) {
            val randomIndex = secureRandom.nextInt(allChars.length)
            password.append(allChars[randomIndex])
        }
        return password.toString()
    }

    private fun copyToClipboard(password: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Senha Copiada", password)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Senha copiada para a área de transferência!", Toast.LENGTH_SHORT).show()
    }
}
