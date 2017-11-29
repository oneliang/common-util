package com.oneliang;

public abstract interface Constant {

    public static abstract interface Base {
        public static final String EXCEPTION = "exception";
    }

    public static abstract interface Symbol {
        /**
         * dot "."
         */
        public static final String DOT = ".";
        public static final char DOT_CHAR = '.';
        /**
         * comma ","
         */
        public static final String COMMA = ",";
        /**
         * colon ":"
         */
        public static final String COLON = ":";
        /**
         * semicolon ";"
         */
        public static final String SEMICOLON = ";";
        /**
         * equal "="
         */
        public static final String EQUAL = "=";
        /**
         * and "&"
         */
        public static final String AND = "&";
        /**
         * question mark "?"
         */
        public static final String QUESTION_MARK = "?";
        /**
         * wildcard "*"
         */
        public static final String WILDCARD = "*";
        /**
         * underline "_"
         */
        public static final String UNDERLINE = "_";
        /**
         * at "@"
         */
        public static final String AT = "@";
        /**
         * minus "-"
         */
        public static final String MINUS = "-";
        /**
         * logic and "&&"
         */
        public static final String LOGIC_AND = "&&";
        /**
         * logic or "||"
         */
        public static final String LOGIC_OR = "||";
        /**
         * brackets begin "("
         */
        public static final String BRACKET_LEFT = "(";
        /**
         * brackets end ")"
         */
        public static final String BRACKET_RIGHT = ")";
        /**
         * middle bracket left "["
         */
        public static final String MIDDLE_BRACKET_LEFT = "[";
        /**
         * middle bracket right "]"
         */
        public static final String MIDDLE_BRACKET_RIGHT = "]";
        /**
         * big bracket "{"
         */
        public static final String BIG_BRACKET_LEFT = "{";
        /**
         * big bracket "}"
         */
        public static final String BIG_BRACKET_RIGHT = "}";
        /**
         * slash "/"
         */
        public static final String SLASH_LEFT = "/";
        /**
         * slash "\"
         */
        public static final String SLASH_RIGHT = "\\";
        /**
         * xor or regex begin "^"
         */
        public static final String XOR = "^";
        /**
         * dollar or regex end "$"
         */
        public static final String DOLLAR = "$";
        /**
         * single quotes "'"
         */
        public static final String SINGLE_QUOTES = "'";
        /**
         * double quotes "\""
         */
        public static final String DOUBLE_QUOTES = "\"";
        /**
         * less then "<"
         */
        public static final String LESS_THEN = "<";
        /**
         * greater then ">"
         */
        public static final String GREATER_THEN = ">";
    }

    public static abstract interface Encoding {
        /**
         * encoding
         */
        public static final String ISO88591 = "ISO-8859-1";
        public static final String GB2312 = "GB2312";
        public static final String GBK = "GBK";
        public static final String UTF8 = "UTF-8";
    }

    public static abstract interface Timezone {
        public static final String ASIA_SHANGHAI = "Asia/Shanghai";
    }

    public static abstract interface Http {

        public static abstract interface RequestMethod {
            /**
             * for request method
             */
            public static final String PUT = "PUT";
            public static final String DELETE = "DELETE";
            public static final String GET = "GET";
            public static final String POST = "POST";
            public static final String HEAD = "HEAD";
            public static final String OPTIONS = "OPTIONS";
            public static final String TRACE = "TRACE";
        }

        public static abstract interface HeaderKey {
            /**
             * for request,response header
             */
            public static final String CONTENT_TYPE = "Content-Type";
            public static final String CONTENT_DISPOSITION = "Content-Disposition";
            public static final String ACCEPT_CHARSET = "Accept-Charset";
            public static final String CONTENT_ENCODING = "Content-Encoding";
        }

        public static abstract interface ContentType {
            /**
             * for request,response content type
             */
            public static final String TEXT_PLAIN = "text/plain";
            public static final String APPLICATION_X_DOWNLOAD = "application/x-download";
            public static final String APPLICATION_ANDROID_PACKAGE = "application/vnd.android.package-archive";
            public static final String MULTIPART_FORM_DATA = "multipart/form-data";
            public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
            public static final String BINARY_OCTET_STREAM = "binary/octet-stream";
            public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
        }

        public static abstract interface StatusCode {

            public static final int CONTINUE = 100;
            public static final int SWITCHING_PROTOCOLS = 101;
            public static final int PROCESSING = 102;

            public static final int OK = 200;
            public static final int CREATED = 201;
            public static final int ACCEPTED = 202;
            public static final int NON_AUTHORITATIVE_INFORMATION = 203;
            public static final int NO_CONTENT = 204;
            public static final int RESET_CONTENT = 205;
            public static final int PARTIAL_CONTENT = 206;
            public static final int MULTI_STATUS = 207;

            public static final int MULTIPLE_CHOICES = 300;
            public static final int MOVED_PERMANENTLY = 301;
            public static final int FOUND = 302;
            public static final int SEE_OTHER = 303;
            public static final int NOT_MODIFIED = 304;
            public static final int USE_PROXY = 305;
            public static final int SWITCH_PROXY = 306;
            public static final int TEMPORARY_REDIRECT = 307;

            public static final int BAD_REQUEST = 400;
            public static final int UNAUTHORIZED = 401;
            public static final int PAYMENT_REQUIRED = 402;
            public static final int FORBIDDEN = 403;
            public static final int NOT_FOUND = 404;
            public static final int METHOD_NOT_ALLOWED = 405;
            public static final int NOT_ACCEPTABLE = 406;
            public static final int REQUEST_TIMEOUT = 408;
            public static final int CONFLICT = 409;
            public static final int GONE = 410;
            public static final int LENGTH_REQUIRED = 411;
            public static final int PRECONDITION_FAILED = 412;
            public static final int REQUEST_URI_TOO_LONG = 414;
            public static final int EXPECTATION_FAILED = 417;
            public static final int TOO_MANY_CONNECTIONS = 421;
            public static final int UNPROCESSABLE_ENTITY = 422;
            public static final int LOCKED = 423;
            public static final int FAILED_DEPENDENCY = 424;
            public static final int UNORDERED_COLLECTION = 425;
            public static final int UPGRADE_REQUIRED = 426;
            public static final int RETRY_WITH = 449;

            public static final int INTERNAL_SERVER_ERROR = 500;
            public static final int NOT_IMPLEMENTED = 501;
            public static final int BAD_GATEWAY = 502;
            public static final int SERVICE_UNAVAILABLE = 503;
            public static final int GATEWAY_TIMEOUT = 504;
            public static final int HTTP_VERSION_NOT_SUPPORTED = 505;
            public static final int VARIANT_ALSO_NEGOTIATES = 506;
            public static final int INSUFFICIENT_STORAGE = 507;
            public static final int LOOP_DETECTED = 508;
            public static final int BANDWIDTH_LIMIT_EXCEEDED = 509;
            public static final int NOT_EXTENDED = 510;
            public static final int UNPARSEABLE_RESPONSE_HEADERS = 600;
        }
    }

    public static abstract interface RequestScope {
        public static final String SESSION = "session";
    }

    public static abstract interface RequestParameter {
        public static final String RETURN_URL = "returnUrl";
    }

    public static abstract interface Database {
        public static final String COLUMN_NAME_TOTAL = "TOTAL";

        public static final String MYSQL = "mysql";
        public static final String SQLITE = "sqlite";
        public static final String ORACLE = "oracle";

        public static abstract interface MySql {
            /**
             * pagination
             */
            public static final String PAGINATION = "LIMIT";
        }
    }

    public static abstract interface Capacity {
        /**
         * bytes per kilobytes
         */
        public static final int BYTES_PER_KB = 1024;

        /**
         * bytes per millionbytes
         */
        public static final int BYTES_PER_MB = BYTES_PER_KB * BYTES_PER_KB;
    }

    public static abstract interface Method {
        public static final String PREFIX_SET = "set";
        public static final String PREFIX_GET = "get";
        public static final String PREFIX_IS = "is";
        public static final String GET_CLASS = "getClass";
    }

    public static abstract interface File {
        public static final String CLASS = "class";
        public static final String JPEG = "jpeg";
        public static final String JPG = "jpg";
        public static final String GIF = "gif";
        public static final String JAR = "jar";
        public static final String JAVA = "java";
        public static final String EXE = "exe";
        public static final String DEX = "dex";
        public static final String AIDL = "aidl";
        public static final String SO = "so";
        public static final String XML = "xml";
        public static final String CSV = "csv";
        public static final String TXT = "txt";
        public static final String APK = "apk";
    }

    public static abstract interface Protocol {
        public static final String FILE = "file://";
        public static final String HTTP = "http://";
        public static final String FTP = "ftp://";
    }
}
