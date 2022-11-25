package com.wise23.chariteed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import com.google.zxing.WriterException;

@SpringBootApplication
public class ChariteEdApplication {

	public static void main(String[] args) throws WriterException, IOException {
		// Example for creating a QR code
		String url = "https://www.youtube.de";
		String fileName = "QR_code.png";
		int size = 125;
		String fileType = "png";
		File qrFile = new File(fileName);
		QRCodeGenerator.createQRImage(qrFile, url, size, fileType);
		SpringApplication.run(ChariteEdApplication.class, args);
	}
}
