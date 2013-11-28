#ifndef PACKT_LOG_HPP
#define PACKT_LOG_HPP

class Log {
public:
	static void error(const char* pMessage, ...);
	static void warn(const char* pMessage, ...);
	static void info(const char* pMessage, ...);
	static void debug(const char* pMessage, ...);
};

#endif
