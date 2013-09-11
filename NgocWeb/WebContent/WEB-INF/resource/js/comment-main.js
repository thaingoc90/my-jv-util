var CommentPlugin = {
	constants : {
		commentBoxKey : ".commentNhp",
		commentFrame : "/comment",
		staticResource : "/static/",
		baseUrl : "127.0.0.1:8888",
		listLibs : ["js/jquery.base64"],
		jQueryPath: ["js/jquery-2.0.2.min"],
	},

	init : function() {
		var target_url = $.base64.encode(this.getReducedUrl(document.location.href));
		var commentField = $(this.constants.commentBoxKey);
		var width = $(commentField).attr('data-width');
		var height = $(commentField).attr('data-height');
		var target = $(commentField).attr('data-target');
		var token = $(commentField).attr('data-token');
		var limit = $(commentField).attr('data-nums');
		var href = "//" + this.constants.baseUrl + this.constants.commentFrame
				+ "/?token=" + token + "&target=" + target + "&target_url="
				+ target_url + "&limit=" + limit;
		CommentPlugin.createIFrame($(commentField), token + "_" + target,
				href, width, height);
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
    
	createId : function(idTemp) {
		return idTemp.replace(/[^a-zA-Z0-9_]/g, "");
	},
	
	getReducedUrl : function(url) {
		if (url.indexOf("?") > -1) {
			url = url.substr(0, url.indexOf("?"));
		}
		return url;
	},
	
	getMessage: function(event) {
		var content = jQuery.parseJSON(event.data);
		if (content.action == "setHeight") {
			var keyIframe = CommentPlugin.createId(content.target);
			var iframe = $("iframe#" + keyIframe);
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
		// TODO: CHECK LIB
		setTimeout(function() {
			CommentPlugin.init();
		}, 200);
	});
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