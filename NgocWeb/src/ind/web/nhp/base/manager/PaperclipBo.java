package ind.web.nhp.base.manager;

import ind.web.nhp.base.BaseBo;
import ind.web.nhp.utils.JsonUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class PaperclipBo extends BaseBo implements IPaperclip {

	public static final String COL_ID = "pc_id";
	public static final String COL_ORIGINAL_NAME = "pc_original_name";
	public static final String COL_CREATE_TIME = "pc_create_time";
	public static final String COL_FILESIZE = "pc_filesize";
	public static final String COL_FILESTATUS = "pc_filestatus";
	public static final String COL_MIMETYPE = "pc_mimetype";
	public static final String COL_OWNER = "pc_owner";
	public static final String COL_METADATA = "pc_metadata";

	public static final String META_FILE_DISK_NAME = "file_disk_name";
	public static final String META_FILE_DIR = "file_dir";
	public static final String META_EXTERNAL_STORAGE = "external_storage";
	
	public static final String STATUS_FINAL = "final";

	private String id, originalName, status, type, owner, meta;
	private Date createTime;
	private long size;
	private Map<String, Object> objMetadata;

	@Override
	public Map<String, Object[]> getFieldMap() {
		Map<String, Object[]> result = new HashMap<String, Object[]>();
		result.put(COL_ID, new Object[] { "id", int.class });
		result.put(COL_ORIGINAL_NAME, new Object[] { "originalName", String.class });
		result.put(COL_CREATE_TIME, new Object[] { "createTime", Date.class });
		result.put(COL_FILESIZE, new Object[] { "size", long.class });
		result.put(COL_FILESTATUS, new Object[] { "status", String.class });
		result.put(COL_MIMETYPE, new Object[] { "type", String.class });
		result.put(COL_OWNER, new Object[] { "owner", String.class });
		result.put(COL_METADATA, new Object[] { "meta", String.class });
		return result;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	@Override
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String getMeta() {
		return meta;
	}

	@SuppressWarnings("unchecked")
	public void setMeta(String meta) {
		this.meta = meta;
		Map<String, Object> metaData = null;
		try {
			metaData = (Map<String, Object>) JsonUtils.fromJson(this.meta, Map.class);
		} catch (Exception e) {}
		objMetadata = metaData == null ? new HashMap<String, Object>() : metaData;
	}

	public Object getMetadataEntry(String name) {
		return objMetadata != null ? objMetadata.get(name) : null;
	}

	public void setMetadataEntry(String name, Object value) {
		if (objMetadata == null) {
			objMetadata = new HashMap<String, Object>();
		}
		objMetadata.put(name, value);
		meta = JsonUtils.toJson(objMetadata);
	}

	public void removeMetadataEntry(String name) {
		if (objMetadata != null) {
			objMetadata.remove(name);
			meta = JsonUtils.toJson(objMetadata);
		}
	}

	@Override
	public String getDiskName() {
		Object obj = getMetadataEntry(META_FILE_DISK_NAME);
		return obj == null ? "" : obj.toString();
	}

	public void setDiskName(String diskName) {
		setMetadataEntry(META_FILE_DISK_NAME, diskName);
	}

	@Override
	public String getDir() {
		Object obj = getMetadataEntry(META_FILE_DIR);
		return obj == null ? "" : obj.toString();
	}

	public void setDir(String dir) {
		setMetadataEntry(META_FILE_DIR, dir);
	}

	/**
	 * ExternalStorage is used when store all contents in db.
	 * 
	 * Not use now.
	 * 
	 * @return
	 */
	@Override
	public boolean isExternalStorage() {
		Object obj = getMetadataEntry(META_EXTERNAL_STORAGE);
		return obj != null && !StringUtils.isEmpty(obj.toString()) ? true : false;
	}

	public void setExternalStorage(boolean exStorage) {
		setMetadataEntry(META_EXTERNAL_STORAGE, exStorage ? "true" : "");
	}

	@Override
	public String getUrl(String staticUrlPrefix) {
		String url = staticUrlPrefix == null ? "" : staticUrlPrefix;
		String fileDir = getDir();
		if (StringUtils.isEmpty(fileDir)) {
			url += "/" + fileDir;
		}
		String fileDiskName = getDiskName();
		if (StringUtils.isEmpty(fileDiskName)) {
			url += "/" + fileDiskName;
		}
		return url;
	}
}
