function isEmpty(str) {
	return (!str || 0 === str.length);
}

function isBlank(str) {
	return (!str || /^\s*$/.test(str));
}

function textAreaAdjust(o) {
	o.style.height = "1px";
	var minHeight = 20;
    var height = o.scrollHeight < minHeight ? minHeight :o.scrollHeight;
    o.style.height = (height)+"px";
}