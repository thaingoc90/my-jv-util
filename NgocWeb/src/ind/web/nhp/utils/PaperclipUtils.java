package ind.web.nhp.utils;

import ind.web.nhp.base.Constants;
import ind.web.nhp.base.manager.IPaperclip;
import ind.web.nhp.base.manager.IPaperclipManager;
import ind.web.nhp.base.manager.PaperclipBo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PaperclipUtils {

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private IPaperclipManager pcManager;

	/**
	 * Upload file.
	 * 
	 * File's content is saved in storage folder (with subfolder: prefixFolder) & information is
	 * stored in db.
	 * 
	 * @param prefixFolder
	 * @param file
	 * @param maxSize
	 * @param allowedExtension
	 * @return
	 * @throws Exception
	 */
	public IPaperclip uploadFile(String prefixFolder, MultipartFile file, long maxSize,
			String allowedExtension) throws Exception {
		prefixFolder = Utils.toString(prefixFolder, "");
		// GET INFORMATION OF FILE
		long size = file.getSize();
		String mimeType = file.getContentType();
		String id = Utils.generateId64();
		String orgName = new String(file.getOriginalFilename().getBytes("ISO-8859-1"), "UTF-8");
		orgName = decompose(orgName);
		String extension = getExtensionFile(orgName);
		String diskName = id + extension;

		if (maxSize > 0 && size > maxSize) {
			throw new Exception("Error: File is too large");
		}

		if (!StringUtils.isEmpty(allowedExtension)) {
			String[] allowedExts = allowedExtension.split(",");
			boolean valid = false;
			for (String ext : allowedExts) {
				ext = ext.trim();
				if (ext.equals("*") || ext.equals(".*")) {
					valid = true;
					break;
				}
				if (ext.equalsIgnoreCase(extension)) {
					valid = true;
					break;
				}
			}
			if (valid == false) {
				throw new Exception("Error: File's extension is invalid!");
			}
		}

		Calendar nowCal = Calendar.getInstance();
		int year = nowCal.get(Calendar.YEAR);
		int month = nowCal.get(Calendar.MONTH) + 1;
		int day = nowCal.get(Calendar.DAY_OF_MONTH);
		Date nowDate = nowCal.getTime();

		String extraPath = "/" + prefixFolder + "/" + year + "/" + month + "/" + day;
		File folder = new File(servletContext.getRealPath("/" + Constants.PATH_STORAGE + extraPath));
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File outFile = new File(folder.getAbsolutePath() + "/" + diskName);
		InputStream is = null;
		OutputStream os = null;
		PaperclipBo pc = null;
		try {
			is = file.getInputStream();
			os = new FileOutputStream(outFile);
			IOUtils.copy(is, os);
			pc = new PaperclipBo();
			pc.setCreateTime(nowDate);
			pc.setId(id);
			pc.setSize(size);
			pc.setType(mimeType);
			pc.setDir(extraPath);
			pc.setOriginalName(orgName);
			pc.setDiskName(diskName);
			pc.setExternalStorage(true);
			pc.setOwner("");
			pc.setStatus(PaperclipBo.STATUS_FINAL);
			pcManager.createAttachment(pc);
		} catch (Exception e) {
			throw new Exception("Error: Cannot upload file!");
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}
		return pc;
	}

	/**
	 * Checks if id existed, return. Else, upload file.
	 * 
	 * @param id
	 * @param prefixFolder
	 * @param file
	 * @param maxSize
	 * @param allowedExtension
	 * @return
	 * @throws Exception
	 */
	public IPaperclip uploadFileCheckExist(String id, String prefixFolder, MultipartFile file,
			long maxSize, String allowedExtension) throws Exception {
		if (!StringUtils.isEmpty(id)) {
			IPaperclip pc = pcManager.getAttachment(id);
			if (pc != null) {
				return pc;
			}
		}
		return uploadFile(prefixFolder, file, maxSize, allowedExtension);
	}

	/**
	 * Gets file's extension.
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getExtensionFile(String fileName) {
		String extension = "";
		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
		if (i > p) {
			extension = fileName.substring(i + 1);
		}
		return extension == "" ? "" : "." + extension;
	}

	/**
	 * Replaces VietNam character to English character. (Exp: ế -> e)
	 * 
	 * @param s
	 * @return
	 */
	private static String decompose(String s) {
		return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD).replaceAll(
				"\\p{InCombiningDiacriticalMarks}+", "");
	}
}
