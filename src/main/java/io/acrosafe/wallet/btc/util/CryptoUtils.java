/**
 * MIT License
 *
 * Copyright (c) 2020 acrosafe technologies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.acrosafe.wallet.btc.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils
{
    // AES algorithm name
    private static final String ALGORITHM_AES = "AES";

    // PBKDF algorithm name
    private static final String ALGORITHM_PBKDF2WITHHMACSHA512 = "PBKDF2WithHmacSHA512";

    // AES cipher algorithm name
    private static final String ALGORITHM_AES_CIPHER = "AES/CBC/PKCS5Padding";

    // AES block size
    private static final int DEFAULT_AES_BLOCK_SIZE = 16;

    // key length
    private static final int KEY_LENGTH = 256;

    // Iteration count
    private static final int ITERATION_COUNT = 65536;

    /**
     * Encrypts the given content by given password and iv bytes.
     *
     * @param password
     * @param content
     * @param iv
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     * @throws InvalidParameterSpecException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     */
    public static String encrypt(String password, byte[] content, byte[] iv, byte[] salt) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, InvalidParameterSpecException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException
    {
        Cipher cipher = Cipher.getInstance(ALGORITHM_AES_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password, salt), new IvParameterSpec(iv));
        byte[] cipherText = cipher.doFinal(content);
        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     * Decrypts the content based on given password and iv bytes.
     *
     * @param password
     * @param content
     * @param iv
     * @param salt
     * @return
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decrypt(String password, String content, byte[] iv, byte[] salt)
            throws UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
    {
        byte[] origin = Base64.getDecoder().decode(content.getBytes());
        Cipher cipher = Cipher.getInstance(ALGORITHM_AES_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password, salt), new IvParameterSpec(iv));

        return cipher.doFinal(origin);
    }

    /**
     * Generates IvParameterSpec byte array.
     *
     * @return
     */
    public static byte[] generateIVParameterSpecBytes()
    {
        SecureRandom randomSecureRandom = new SecureRandom();
        byte[] bytes = new byte[DEFAULT_AES_BLOCK_SIZE];
        randomSecureRandom.nextBytes(bytes);

        return bytes;
    }

    /**
     * Generates 16 bytes salt byte array.
     *
     * @return
     */
    public static byte[] generateSaltBytes()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return salt;
    }

    /**
     * Generates the secrets key based on given password.
     *
     * @param password
     * @param salt
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    private static SecretKey getSecretKey(String password, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException
    {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM_PBKDF2WITHHMACSHA512);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);

        SecretKey key = factory.generateSecret(spec);
        SecretKey keySpec = new SecretKeySpec(key.getEncoded(), ALGORITHM_AES);

        return keySpec;
    }
}
