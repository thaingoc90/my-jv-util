package my.library.google.youtube;

import java.io.File;
import java.net.URL;

import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.media.mediarss.MediaCategory;
import com.google.gdata.data.media.mediarss.MediaDescription;
import com.google.gdata.data.media.mediarss.MediaKeywords;
import com.google.gdata.data.media.mediarss.MediaTitle;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.data.youtube.YouTubeNamespace;
import com.google.gdata.util.ServiceException;

/**
 * 
 * @author ngoctdn
 * 
 *         lib: gdata-client-1.0.jar, gdata-youtube-2.0.jar,
 *         gdata-media-1.0.jar, gdata-core-1.0.jar
 */

public class YouTubeLib {

	public final static String FILE_PATH = "D:\\Music.flv";
	public final static String FILE_TYPE = "video/x-flv";
	public final static String UPLOAD_URL = "http://uploads.gdata.youtube.com/feeds/api/users/default/uploads";

	public static void main(String[] args) throws Exception {
		YouTubeService service = new YouTubeService(YouTubeConstant.CLIENT_ID,
				YouTubeConstant.DEVELOPER_KEY);
		service.setUserCredentials(YouTubeConstant.GOOGLE_USER,
				YouTubeConstant.GOOGLE_PASS);
		// uploadVideo(service, FILE_PATH, FILE_TYPE);

	}

	public static void uploadVideo(YouTubeService service, String fileLocation,
			String fileType) {
		File uploadFile = new File(fileLocation);
		if (!uploadFile.exists()) {
			System.out.println("file ko ton tai");
		} else {
			System.out.println("file ok");
		}

		VideoEntry newEntry = new VideoEntry();
		YouTubeMediaGroup mg = newEntry.getOrCreateMediaGroup();
		mg.setTitle(new MediaTitle());
		mg.getTitle().setPlainTextContent(uploadFile.getName());
		mg.addCategory(new MediaCategory(YouTubeNamespace.CATEGORY_SCHEME,
				"Music"));
		mg.setKeywords(new MediaKeywords());
		mg.setDescription(new MediaDescription());
		mg.getDescription().setPlainTextContent("Demo");
		mg.setPrivate(true);
		mg.addCategory(new MediaCategory(YouTubeNamespace.DEVELOPER_TAG_SCHEME,
				"music"));
		MediaFileSource ms = new MediaFileSource(uploadFile, fileType);
		newEntry.setMediaSource(ms);

		try {
			service.insert(new URL(UPLOAD_URL), newEntry);
			System.out.println("ok");
		} catch (ServiceException se) {
			System.out.println("Sorry, your upload was invalid:");
			System.out.println(se.getResponseBody());
		} catch (Exception e) {
			System.err.println("error");
			e.printStackTrace();
		}
	}
}
