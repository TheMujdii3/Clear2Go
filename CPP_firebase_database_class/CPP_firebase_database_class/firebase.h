#pragma once
#include <string>
#include <unordered_map>
#include <iostream>
#include <curl.h>
#include <string>
#include <nlohmann/json.hpp>

/// <summary>
/// In order to utilize this class it is requierd to use C++17, 
/// and to add the include folder to Additional Include Directories.
/// </summary>
class firebase
{
//Constructor:
public:
	firebase(std::string URL);
	~firebase();
private:
	std::string postResponse;
	std::string putResponse;
	std::string deleteResponse;
	std::string getResponse;
	std::string fullURL;
	std::string baseURL;
	size_t WriteCallback(void* contents, size_t size, size_t nmemb, void* userp);
	std::string httpRequest(const std::string& url, const std::string& data, const std::string& method);
public:
	void POST(std::string endpoint,std::string postDATA);
	//void GET(std::string endpoint);
	std::unordered_map<std::string, std::string> GET(std::string endpoint);
	void PUT(std::string endpoint,std::string putDATA);
	void Delete(std::string endpoint);
};
