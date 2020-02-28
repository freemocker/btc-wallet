package io.acrosafe.wallet.btc.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.Destroyable;

public class Passphrase implements Destroyable
{

    private static final String ALGORITHM_AES = "AES";
    private static final String ALGORITHM_AES_CIPHER = "AES/CBC/PKCS5Padding";
    private static final String UTF8_ENCODING = "UTF-8";

    private static final int DEFAULT_AES_KEY_SIZE_BITS = 256;
    private static final int DEFAULT_AES_BLOCK_SIZE = 16;

    private static final char EMPTY_VALUE = '\0';

    private static final SecretKey KEY;
    private static final IvParameterSpec PARAMETER_SPEC;

    static
    {
        KEY = generateAESEncryptionKey();
        PARAMETER_SPEC = generateAESParameterSpec();
    }

    private int dataLength;
    private char[] dataValue;
    private byte[] content;
    private byte[] encryptedDataValue;

    public Passphrase(final String value)
    {
        this(value.toCharArray());
    }

    public Passphrase(final char[] value)
    {
        setValue(value);
    }

    public static SecretKey generateAESEncryptionKey()
    {
        SecretKey key;
        try
        {
            final SecureRandom secureRandom = new SecureRandom();
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_AES);
            keyGenerator.init(DEFAULT_AES_KEY_SIZE_BITS, secureRandom);
            key = keyGenerator.generateKey();
        }
        catch (NoSuchAlgorithmException e)
        {
            key = new SecretKeySpec(UUID.randomUUID().toString().getBytes(), ALGORITHM_AES);
        }

        return key;
    }

    public static IvParameterSpec generateAESParameterSpec()
    {
        SecureRandom randomSecureRandom = new SecureRandom();
        byte[] bytes = new byte[DEFAULT_AES_BLOCK_SIZE];
        randomSecureRandom.nextBytes(bytes);

        return new IvParameterSpec(bytes);
    }

    private static byte[] encrypt(final char[] data)
    {
        byte[] encryptedData;

        try
        {
            final Cipher cipher = getAESCipher(Cipher.ENCRYPT_MODE, KEY, PARAMETER_SPEC);
            final CharBuffer charBuffer = CharBuffer.wrap(data);
            final ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);

            final byte[] dataBytes = new byte[byteBuffer.limit()];
            byteBuffer.get(dataBytes);
            Arrays.fill(byteBuffer.array(), (byte) 0);
            readAndVerifyByteBuffer(byteBuffer.array());

            encryptedData = cipher.doFinal(dataBytes);
            Arrays.fill(dataBytes, (byte) 0);
            readAndVerifyByteBuffer(dataBytes);
        }
        catch (GeneralSecurityException e)
        {
            encryptedData = null;
        }

        return encryptedData;
    }

    private static char[] decrypt(final byte[] data, final int charLength)
    {
        char[] decryptedData;

        try
        {
            final Cipher cipher = getAESCipher(Cipher.DECRYPT_MODE, KEY, PARAMETER_SPEC);
            final ByteBuffer byteBuffer = ByteBuffer.wrap(cipher.doFinal(data));
            final CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
            decryptedData = Arrays.copyOfRange(charBuffer.array(), 0, charLength);
            Arrays.fill(byteBuffer.array(), (byte) 0);
            Arrays.fill(charBuffer.array(), EMPTY_VALUE);
            readAndVerifyByteBuffer(byteBuffer.array());
            readAndVerifyCharBuffer(charBuffer.array());
        }
        catch (GeneralSecurityException e)
        {
            decryptedData = null;
        }

        return decryptedData;
    }

    private static Cipher getAESCipher(int cipherMode, Key key, AlgorithmParameterSpec spec)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException
    {
        final Cipher cipher = Cipher.getInstance(ALGORITHM_AES_CIPHER);
        cipher.init(cipherMode, key, spec);

        return cipher;
    }

    private static void readAndVerifyCharBuffer(final char[] charData)
    {
        final int charBufferLength = charData != null ? charData.length : 0;

        for (int i = 0; i < charBufferLength; ++i)
        {
            if (charData[i] != EMPTY_VALUE)
            {
                throw new IllegalStateException("Fails to destroy secret data");
            }
        }
    }

    private static void readAndVerifyByteBuffer(final byte[] byteData)
    {
        final int byteBufferLength = byteData != null ? byteData.length : 0;

        for (int i = 0; i < byteBufferLength; ++i)
        {
            if (byteData[i] != 0)
            {
                throw new IllegalStateException("Fails to destroy secret data");
            }
        }
    }

    public void destroy()
    {
        byte[] byteData = null;
        char[] charData = null;

        if (dataValue != null)
        {
            charData = dataValue;
            Arrays.fill(dataValue, EMPTY_VALUE);
            dataValue = null;
        }

        if (encryptedDataValue != null)
        {
            Arrays.fill(encryptedDataValue, (byte) 0);
            encryptedDataValue = null;
        }

        if (content != null)
        {
            byteData = content;
            Arrays.fill(content, (byte) 0);
            content = null;
        }

        dataLength = 0;
        readAndVerify(charData, byteData);
    }

    public char[] getValue()
    {
        if ((dataValue == null) && (encryptedDataValue != null))
        {
            dataValue = decrypt(encryptedDataValue, dataLength);
        }

        return dataValue;
    }

    public void setValue(final char[] value)
    {
        destroy();

        if (value != null)
        {
            dataValue = value;
            dataLength = value.length;
            encryptedDataValue = encrypt(value);
        }
    }

    public byte[] getBytes()
    {
        final char[] value = getValue();

        if ((content == null) && (value != null))
        {
            final CharBuffer charBuffer = CharBuffer.wrap(value);
            final ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);

            content = new byte[byteBuffer.limit()];
            byteBuffer.get(content);
            Arrays.fill(byteBuffer.array(), (byte) 0);
            readAndVerifyByteBuffer(byteBuffer.array());
        }

        return content;
    }

    public String getStringValue()
    {
        final char[] dataValue = getValue();

        return (dataValue != null) ? new String(dataValue) : null;
    }

    private boolean isEmpty()
    {
        final char[] value = getValue();

        return (value == null) || (dataLength == 0);
    }

    private void readAndVerify(final char[] charData, final byte[] byteData)
    {
        readAndVerifyCharBuffer(charData);
        readAndVerifyByteBuffer(byteData);

        if (getBytes() != null || getStringValue() != null || getValue() != null || !isEmpty())
        {
            throw new IllegalStateException("Fails to destroy secret data");
        }
    }

}
