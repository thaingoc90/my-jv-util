package ind.web.nhp.model;

import java.util.LinkedList;
import java.util.List;

public class PaginationModel {

	public static final int DEFAULT_RANGE = 5;
	public static final String[] searchPage = new String[] { "\\{PAGE\\}", "\\{PAGE_NUM\\}", "\\{pageNum\\}" };

	private int pageSize, currentPage;
	private int totalRow;
	private String urlFormat;
	private int range;

	public PaginationModel(String urlFomat, int totalRow, int pageSize, int curPage) {
		this.urlFormat = urlFomat;
		this.totalRow = totalRow;
		this.pageSize = pageSize;
		this.currentPage = curPage;
		this.range = DEFAULT_RANGE;
	}

	public PaginationModel(String urlFomat, int totalRow, int pageSize, int curPage, int range) {
		this.urlFormat = urlFomat;
		this.totalRow = totalRow;
		this.pageSize = pageSize;
		this.currentPage = curPage;
		this.range = range;
	}

	public int getTotalPage() {
		if (pageSize == 0) {
			return 0;
		}
		int temp = totalRow / pageSize;
		return totalRow % pageSize == 0 ? temp : temp + 1;
	}

	public List<Integer> getVisiblePages() {
		List<Integer> listPages = new LinkedList<Integer>();
		int numPages = getTotalPage();
		if (numPages <= range) {
			for (int i = 1; i <= numPages; i++) {
				listPages.add(i);
			}
		} else {
			// The leftSize & rightSize of currentPage
			int rightRange = range / 2;
			int leftRange = range - rightRange - 1;

			// if the leftSize < leftRangeSize
			if (leftRange >= currentPage) {
				for (int i = 1; i <= range; i++) {
					listPages.add(i);
				}
			} else if (rightRange >= (numPages - currentPage)) {
				// if the rightSize < rightRangeSize
				for (int i = numPages - range + 1; i <= numPages; i++) {
					listPages.add(i);
				}
			} else {
				for (int i = currentPage - leftRange; i <= currentPage + rightRange; i++) {
					listPages.add(i);
				}
			}
		}
		return listPages;
	}

	public int getPreviousPage() {
		return (currentPage <= 1) ? 1 : currentPage - 1;
	}

	public int getNextPage() {
		int numPages = getTotalPage();
		return (currentPage < numPages) ? currentPage + 1 : numPages;
	}

	public String getUrlForPage(int page) {
		String result = urlFormat;
		for (String target : searchPage) {
			result = result.replaceAll("(?i)" + target, String.valueOf(page));
		}
		return result;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public String getUrlFormat() {
		return urlFormat;
	}

	public void setUrlFormat(String urlFormat) {
		this.urlFormat = urlFormat;
	}
}
