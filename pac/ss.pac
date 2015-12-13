var PROXY = 'SOCKS 192.168.2.28:1080';

var PROXY_HOSTS  = [
    /* google */
    'google.com', 'googleusercontent.com', 'gstatic.com', 'ggpht.com', 'googleapis.com',
    'googlecode.com', 'google.co.id', 'googleadservices.com',

    /* other */
    'godaddy.com', 'blogger.com', 'blogspot.jp', 'youtu.be', 'goo.gl', 'facebook.com', 
    'feedsportal.com', 'cloudfront.net', 'alexa.com', 's3.amazonaws.com', 'cl.ly', 'yfrog.com', 
    'fbcdn.net', 'posterous.com', 'feedburner.com', 'foursquare.com', 'slideshare.net', 'fb.me', 
    'wikimedia.org', 'twitter.com', 'twimg.com', 'pbworks.com', 'wordpress.com', 
    'blogspot.com', 'bullogger.com', 'baidu.jp', 'youtube.com', 'ytimg.com', 'dropbox.com', 
    'bit.ly', 'j.mp', 'ff.im', 'is.gd', 't.co', 'img.ly', 'twitpic.com', 'wp.com',
    'scribd.com', 'facebook.net', 'thepiratebay.org', 'tumblr.com', '!akamai.net', '!akamaihd.net', 
    'bbc.co.uk', 'nytimes.com', 'vimeo.com', 'imgchili.com', 'mimima.com', 'linkbucks.com', 'blogimg.jp', 
    'sublimetext.com', 'torproject.org', 'imgur.com', 'slidesharecdn.com', 'sourceforge.net',
    'wolframalpha.com', 'flickr.com', 'git-scm.com', 'digg.com', 'cloudflare.com', 'globalsign.com',
    'geotrust.com', 'blogcdn.com', 'lesscss.org', 'amazonaws.com', 'appspot.com', 'pttrns.com',
    'dochub.io', 'readability.com', 't66y.com', 'aisex.com', 'github.com', 'feedly.com', 'myfreshnet.com',
    'torrentcrazy.com', 'wsj.com', 'boxun.com', 'appledaily.com.tw', 'doubleclick.net', 'computingforgood.org',
    'ripple.com', 'staticflickr.com', 'sstatic.net', 'travis-ci.org', 'tmagazine.com', 'nytimes.com', 'nytlog.com',
    'pastebin.com', 'peacehall.com'
];

var PROXY_HOST_KEYWORDS = [
    'google', 'facebook', 'twitter', 'torrent'
];

var PROXY_URL_KEYWORDS = [
];

var PASS_HOSTS = [
    'taobao.com'
];

var PASS_HOST_KEYWORDS = [
    ':5000', ':8080', ':8000'
];

var PASS_URL_KEYWORDS = [
];

var BLOCK_HOSTS = [
];

/*=============================================*/

function cleanHosts(hosts) {
    var r = {};
    for (var i = 0; i < hosts.length; i++) {
        if (hosts[i][0] !== '!')
            r[hosts[i]] = true;
    }
    return r;
}

PROXY_HOSTS = cleanHosts(PROXY_HOSTS);
PASS_HOSTS = cleanHosts(PASS_HOSTS);
BLOCK_HOSTS = cleanHosts(BLOCK_HOSTS);

function inHosts(host, hosts) {
    var hostParts = host.split('.'), testHost = [];
    while (hostParts.length) {
        testHost.unshift(hostParts.pop());
        if (hosts[testHost.join('.')]) {
            return true;
        }
    }
}

function inKeywords(uri, keywords) {
    for (var i = 0; i < keywords.length; i++) {
        if (uri.indexOf(keywords[i]) >= 0) {
            return true;
        }
    }
}

function FindProxyForURL(url, host) {
    var isBlocked = inHosts(host, BLOCK_HOSTS);
    if (isBlocked)
        return 'SOCKS5 127.0.0.1:54321'

    var isUseProxy = (
            inHosts(host, PROXY_HOSTS) ||
            inKeywords(host, PROXY_HOST_KEYWORDS) ||
            inKeywords(url, PROXY_URL_KEYWORDS)
        ) && (
            !inHosts(host, PASS_HOSTS) &&
            !inKeywords(host, PASS_HOST_KEYWORDS) &&
            !inKeywords(url, PASS_URL_KEYWORDS)
        );
    return isUseProxy ? PROXY : 'DIRECT';
}
