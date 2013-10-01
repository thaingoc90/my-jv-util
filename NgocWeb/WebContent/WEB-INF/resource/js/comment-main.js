var CommentPlugin = {
	constants : {
		commentBoxKey : ".commentNhp",
		commentFrame : "/comment",
		staticResource : "/static/",
		baseUrl : "14.0.21.51",
		listLibs : ["js/jquery.base64"],
		jQueryPath: ["js/jquery-2.0.2.min"],
	},

	init : function() {
		var url = document.location.href;
		var target_url = $.base64.encode(this.getReducedUrl(url));
		var commentField = this.constants.commentBoxKey;
		var width = this.getAttribute(commentField, 'data-width', '');
		var height = this.getAttribute(commentField, 'data-height', '');
		var target = this.getAttribute(commentField, 'data-target', '');
		var token = this.getAttribute(commentField, 'data-token', '');
		var limit = this.getAttribute(commentField, 'data-nums', 0);
		var href = "//" + this.constants.baseUrl + this.constants.commentFrame
				+ "/?token=" + token + "&target=" + target + "&target_url="
				+ target_url + "&limit=" + limit;
		CommentPlugin.createIFrame($(commentField), token + "_" + target, href,
				width, height);
	},

	createIFrame : function(element, id, href, width, height) {
		id = this.createId(id);
		var iframe = document.createElement('iframe');
		iframe.setAttribute('src', href);
		iframe.setAttribute('id', id);
		iframe.setAttribute('name', id);
		iframe.setAttribute('data-height', height);
		iframe.setAttribute('style','overflow-x: hidden; overflow-y: auto; border-top-style: none; border-right-style: none; border-bottom-style: none; border-left-style: none; border-width: initial; border-color: initial; border-image: initial;'
								+ 'width:' + width + ';height:' + height + ';min-width: 350px; min-height: 100px;');
		element.append(iframe);
	},
	
	loadLibrary: function() {
		var baseUrl =  "//" + this.constants.baseUrl + this.constants.staticResource;
		if (typeof jQuery == 'undefined') {
			for ( var i = 0; i < this.constants.jQueryPath.length; i++) {
				var sUrl = baseUrl + this.constants.jQueryPath[i] + ".js";
				this.addScript(sUrl);
			}
		}
		for ( var i = 0; i < this.constants.listLibs.length; i++) {
			var sUrl = baseUrl + this.constants.listLibs[i] + ".js";
			this.addScript(sUrl);
		}
    },
    
    addScript : function(url) {
		var e = document.createElement("script");
		e.setAttribute("type", "text/javascript");
		e.setAttribute("src", url);
		e.setAttribute("charset", "utf-8");
		e.setAttribute("defer", "defer");
		e.setAttribute("async", "async");
		document.getElementsByTagName("HEAD")[0].appendChild(e);
	}, 
    
	/**
	 * Creates iframe's id
	 */
	createId : function(idTemp) {
		return idTemp.replace(/[^a-zA-Z0-9_]/g, "");
	},
	
	/**
	 * Reduces url's protocol (http, https, ftp...) 
	 */
	getReducedUrl : function(url) {
		if (url.indexOf("://") > -1) {
			url = url.substr(url.indexOf('://') + 3);
		}
		return url;
	},
	
	/**
	 * Gets an attribute of element, return defaultValue if not exist.
	 */
	getAttribute : function(ele, attr, dv) {
		if ($(ele).attr(attr) !== undefined) {
			return $(ele).attr(attr);
		} else {
			return dv;
		}
	},
	
	/**
	 * Gets message from iframe & setHeight auto. 
	 */
	getMessage: function(event) {
		content = jQuery.parseJSON(event.data);
		if (content.action == "setHeight") {
			var commentField = CommentPlugin.constants.commentBoxKey;
			var iframe = $(commentField + " iframe");
			var dataHeight = iframe.attr("data-height");
			if (dataHeight == 'auto') {
				iframe.css('height', content.height);
			}
		}
    },
};

/**
 * Init comment plugin.
 */
var initCommentPlugin = function() {
	CommentPlugin.loadLibrary();
	$(document).ready(function() {
		checkLibAndInit();
	});
};

/**
 * Check the libs & initiate if having
 */
var checkLibAndInit = function() {
	setTimeout(function() {
		if ($.base64 != undefined) {
			CommentPlugin.init();
		} else {
			checkLibAndInit();
		}
	}, 100);
};

/**
 * Event Listener For Listener from Iframe
 */
var initEventListener = function() {
    /** all browsers except IE before version 9 **/
    if(typeof window.addEventListener == 'function') {
    	window.addEventListener('message', CommentPlugin.getMessage, false);
    } /** IE before version 9 **/ 
    else if(typeof document.attachEvent != 'undefined') {
    	document.attachEvent ("onmessage", CommentPlugin.getMessage);
    }
};

initCommentPlugin();
initEventListener();