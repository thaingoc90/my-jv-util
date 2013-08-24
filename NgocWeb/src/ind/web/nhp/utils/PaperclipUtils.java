package ind.web.nhp.utils;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

public class PaperclipUtils implements ServletContextAware {

    private static ServletContext servletContext;

    public static void uploadFile(String prefixFolder, MultipartFile file) {
	String storage = servletContext.getRealPath(prefixFolder == null ? "" : prefixFolder);
	System.out.println(storage);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
	PaperclipUtils.servletContext = servletContext;
    }

}
