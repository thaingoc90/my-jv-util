/**
 * Checks conditions when uploading file.
 * 
 * Uses 2 attributes: data-maxsize, data-extensions while checking.
 */

$(document).on('submit', 'form[enctype="multipart/form-data"]', function() {
	return showFileSize(this);
});

function showFileSize(form) {
	var fileInputs = $(form).find('input[type=file]');
	if (fileInputs == null) {
		return true;
	}
	for ( var i = 0; i < fileInputs.length; i++) {
		var fileInput = fileInputs[i];
		var maxSize = $(fileInput).attr('data-maxsize');
		var extensions = $(fileInput).attr('data-extensions');
		maxSize = parseInt(maxSize);
		maxSize = isNaN(maxSize) || maxSize < 0 ? 0 : maxSize;

		var file = fileInput.files[0];
		if (file) {
			if (maxSize > 0 && file.size > maxSize) {
				alert("File's size is too large");
				return false;
			}
			var fileName = file.name;
			var _validFileExtensions = extensions.split(",");
			var checkExtension = _checkExtensions(fileName,
					_validFileExtensions);
			if (!checkExtension) {
				alert("File's extension is invalid!");
				return false;
			}
		}
	}
	return true;
}

function _checkExtensions(fileName, _validFileExtensions) {
	if (_validFileExtensions.length > 0) {
		var valid = false;
		for ( var j = 0; j < _validFileExtensions.length; j++) {
			var curExt = _validFileExtensions[j];
			curExt = $.trim(curExt);
			if (fileName.substr(fileName.length - curExt.length, curExt.length)
					.toLowerCase() == curExt.toLowerCase()) {
				valid = true;
				break;
			}
		}
		if (!valid) {
			return false;
		}
	}
	return true;
}