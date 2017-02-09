/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2017 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami.session;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

final class AES {

	public static final String KEY_ALGORITHM = "AES";
	public static final String CIPHER_ALGORITHM = "AES/CBC/NoPadding";

	// 生成密钥
	public static byte[] generateKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
		keyGenerator.init(128);
		SecretKey key = keyGenerator.generateKey();
		return key.getEncoded();
	}

	// 生成iv
	public static AlgorithmParameters generateIV() throws Exception {
		// iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
		byte[] iv = new byte[16];
		Arrays.fill(iv, (byte) 0x00);

		return generateIV(iv);
	}

	// 生成iv
	public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
		AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHM);
		params.init(new IvParameterSpec(iv));
		return params;
	}

	// 转化成JAVA的密钥格式
	public static Key convertToKey(byte[] keyBytes) throws Exception {
		SecretKey secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
		return secretKey;
	}

	// 加密
	public static byte[] encrypt(byte[] data, byte[] keyBytes, AlgorithmParameters iv) throws Exception {
		// 转化为密钥
		Key key = convertToKey(keyBytes);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		return cipher.doFinal(data);
	}

	// 解密
	public static byte[] decrypt(byte[] encryptedData, byte[] keyBytes, AlgorithmParameters iv) throws Exception {
		Key key = convertToKey(keyBytes);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		return cipher.doFinal(encryptedData);
	}

	public static void main(String[] args) {

		String sessionKey = "/Pwp6uoKRr7DJlukAa/9Cg==";
		String encryptedData = "bTkDrZYxBZ0eBZ1d/54O+8N6iKGWh+L4XkwRw0C41IxS4fsoFiB48/AV85bEAqn4Pwo0tfBOF5Tk7/gMr/hn1s3zKbDt2OPOzFrXw9P1deA1FYnzqWe7Jqa3W04rJ2RmD2Vm2XL613j5HBDP5KDGp2P2bTrVJXne5T2pGDT8gUUVmYERA4QtHs8n/EFpCxABAlej3uRyRIBPQJHp5VArbl/9OJ1vbWOBomKRcu1+p6pX0SYfvyXixwslwOI8IAQsbWmCehF2QxBMYjibjOqgNeKSCZ7ZbVcixaSRYKgGWGM0i7oUXgNUfUMbZxEd2M2w4WV+8G6VCD/n0AO3HXr3LqsR9TnqrrGzuxpfcvEHYcrd0mH1+rHEY4VIKxY3ynhcwpg7YRkNHzwbnxMWmMzLmtsAHlCnjXY8ck9BZ/NhugAbuQ1nO1OLGsT2iD71K1kxfRXzqCMWzZMIhC5cpXrGJ1bBlnTJW3ksQWuX7QVDGKY=";
		String iv = "LwG/jx2+Q5q1IvyeryT8Hg==";

		// 明文
		try {
			// 进行解密
			Decoder decoder = Base64.getDecoder();
			byte[] data = decrypt(decoder.decode(encryptedData), decoder.decode(sessionKey),
					generateIV(decoder.decode(iv)));
			System.out.println("解密得到的数据 : " + new String(data));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}