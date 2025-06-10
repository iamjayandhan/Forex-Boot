package gomobi.io.forex.service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.KeySpec;

@Service
public class EncryptionService {

    private static final int CIPHER_KEY_LEN = 256;
    private static final int ITERATION_COUNT = 65536;
    private static final int IV_SIZE = 16;

    public String encryptPayload(String minifiedString, String param1, String param2) {
        try {
            // Generate random 16-byte IV
            byte[] ivBytes = new byte[IV_SIZE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(ivBytes);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            // Derive AES key using PBKDF2WithHmacSHA256
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(param1.toCharArray(), param2.getBytes(StandardCharsets.UTF_8), ITERATION_COUNT, CIPHER_KEY_LEN);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            // Encrypt using AES/CBC/PKCS5Padding
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret, iv);
            byte[] encryptedBytes = cipher.doFinal(minifiedString.getBytes(StandardCharsets.UTF_8));

            // Combine IV + ciphertext
            byte[] combined = new byte[ivBytes.length + encryptedBytes.length];
            System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
            System.arraycopy(encryptedBytes, 0, combined, ivBytes.length, encryptedBytes.length);

            // Return as Base64
            // return Base64.encodeBase64String(combined);
            return java.util.Base64.getEncoder().encodeToString(combined);


        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
