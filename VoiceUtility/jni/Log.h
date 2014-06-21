#ifndef _LOG_H_
#define _LOG_H_

class Log {
public:
	static void error(const char* pMessage, ...);
	static void warn(const char* pMessage, ...);
	static void info(const char* pMessage, ...);
	static void debug(const char* pMessage, ...);
};

#endif
