function removeParameterFromCurUrl(parameter) {
	var url = window.location.href;
	url = removeParameterFromUrl(url, parameter);
	return url;
}

function removeParameterFromUrl(url, parameter) {
	// prefer to use l.search if you have a location/link object
	var urlparts = url.split('?');
	if (urlparts.length >= 2) {
		var prefix = encodeURIComponent(parameter) + '=';
		var pars = urlparts[1].split(/[&;]/g);
		for ( var i = pars.length; i-- > 0;)
			// idiom for string.startsWith
			if (pars[i].lastIndexOf(prefix, 0) !== -1)
				pars.splice(i, 1);
		url = urlparts[0] + '?' + pars.join('&');
	}
	return url;
}