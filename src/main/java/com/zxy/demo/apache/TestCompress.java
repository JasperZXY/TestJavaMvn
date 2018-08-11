package com.zxy.demo.apache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class TestCompress {
	public static void main(String[] args) throws IOException {
		// 要压缩的文件
		File f = new File("F:\\tmp\\pic.gif");
		// 创建压缩对象
		ZipArchiveEntry entry = new ZipArchiveEntry(f.getName());
		FileInputStream fis = new FileInputStream(f);
		// 输出的对象 压缩的文件
		ZipArchiveOutputStream zipOutput = new ZipArchiveOutputStream(new File(
				"F:\\tmp\\pic.zip"));
		zipOutput.putArchiveEntry(entry);
		int i = 0, j;
		while ((j = fis.read()) != -1) {
			zipOutput.write(j);
			i++;
			System.out.println(i);
		}
		zipOutput.closeArchiveEntry();
		zipOutput.close();
		fis.close();
	}

}
