package ind.web.nhp.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class DownloadUtils {

	public static void downloadFile(String url, String pathFile) throws Exception {
		long start = System.currentTimeMillis();
		URL website = new URL(url);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(pathFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} finally {
			IOUtils.closeQuietly(fos);
			System.out.println(System.currentTimeMillis() - start);
		}
	}

	public static void downloadFile2(String url, String pathFile) throws Exception {
		long start = System.currentTimeMillis();
		URL page = new URL(url);
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(page.openStream());
			fout = new FileOutputStream(pathFile);

			byte[] buffer = new byte[4092];
			int count;
			while ((count = in.read(buffer)) != -1) {
				fout.write(buffer, 0, count);
			}
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(fout);
			System.out.println(System.currentTimeMillis() - start);
		}
	}

	public static void downloadFile3(String url, String pathFile) throws Exception {
		long start = System.currentTimeMillis();
		URL page = new URL(url);
		try {
			File outFile = new File(pathFile);
			FileUtils.copyURLToFile(page, outFile);
		} finally {
			System.out.println(System.currentTimeMillis() - start);
		}
	}

	public static void downloadMultiThreads(String url, String pathFile) throws Exception {
		URL page = new URL(url);
		HttpDownloader downloader = new HttpDownloader(page, pathFile,
				HttpDownloader.DEFAULT_NUM_OF_THREADS);
		downloader.download();
	}

	private static String removeQueryParam(String url) {
		int pos = url.indexOf("?");
		return pos != -1 ? url.substring(0, pos) : url;
	}

	public static void main(String[] args) {
		String url = "http://dl03.sopcast.mobi/downloads/20130827/45521c5cb7e3bb4/ISPro_1.5.apk";
		String pathFile = "D:\\\\Test\\1.apk";
		String pathFile2 = "D:\\\\Test\\2.apk";
		String pathFile3 = "D:\\\\Test\\3.apk";

		String reducedUrl = removeQueryParam(url);
		String fileName = FilenameUtils.getBaseName(reducedUrl);
		String extension = FilenameUtils.getExtension(reducedUrl);
		try {
			System.out.println(fileName + "." + extension);
			downloadFile(url, pathFile);
			downloadFile2(url, pathFile2);
			downloadFile3(url, pathFile3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("ok");
	}
}
