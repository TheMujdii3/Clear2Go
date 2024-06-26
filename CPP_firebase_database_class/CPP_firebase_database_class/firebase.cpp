#include "firebase.h"

//Esential Private Function:

size_t firebase::WriteCallback(void* contents, size_t size, size_t nmemb, void* userp)
{
    ((std::string*)userp)->append((char*)contents, size * nmemb);
    return size * nmemb;
}

//Esential Private Function:
std::string firebase::httpRequest(const std::string& url, const std::string& data, const std::string& method)
{
    CURL* curl;
    CURLcode res;
    std::string readBuffer;

    curl_global_init(CURL_GLOBAL_DEFAULT);
    curl = curl_easy_init();
    if (curl) {
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());

        if (method == "POST" || method == "PUT") {
            curl_easy_setopt(curl, CURLOPT_POSTFIELDS, data.c_str());
        }

        if (method == "PUT") {
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "PUT");
        }

        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, &firebase::WriteCallback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &readBuffer);

        res = curl_easy_perform(curl);
        if (res != CURLE_OK)
            fprintf(stderr, "curl_easy_perform() failed: %s\n", curl_easy_strerror(res));

        curl_easy_cleanup(curl);
    }
    curl_global_cleanup();
    return readBuffer;
}

/// Constructor:
firebase::firebase(std::string URL)
{
    baseURL = URL;
};

/// Destructor:
firebase::~firebase() {};

/// POST function.
/// Assigns a new string value from the postDATA input to the endpoint location within the database.
void firebase::POST(std::string endpoint, std::string postDATA)
{
    postResponse = httpRequest(baseURL + endpoint, postDATA, "POST");
    std::cout << "POST Response: " << postResponse << std::endl;
}

/// PUT function.
/// Adds a new string value from the postDATA input to the endpoint location within the database.
void firebase::PUT(std::string endpoint, std::string putDATA)
{
    putResponse = httpRequest(baseURL + endpoint, putDATA, "PUT");
    std::cout << "PUT Response: " << putResponse << std::endl;
}

/// GET(1) function.
/// Retrives data from the database and prints it to the console
//void firebase::GET(std::string endpoint)
//{
//    //std::string baseUrl = "https://<your-database-name>.firebaseio.com/";
//    //std::string endpoint = "example.json";
//    fullURL = baseURL + endpoint;
//
//    // Perform GET request
//    std::string getResponse = httpRequest(fullURL, "", "GET");
//    std::cout << "GET Response: " << getResponse << std::endl;
//
//    // Parse JSON response
//    try {
//        nlohmann::json jsonResponse = nlohmann::json::parse(getResponse);
//
//        // Access and use the retrieved data
//        if (jsonResponse.is_object()) {
//            for (auto& [key, value] : jsonResponse.items()) {
//                std::cout << "Key: " << key << ", Value: " << value << std::endl;
//            }
//        }
//        else {
//            std::cout << "Retrieved data is not a JSON object." << std::endl;
//        }
//    }
//    catch (nlohmann::json::parse_error& e) {
//        std::cerr << "JSON parsing error: " << e.what() << std::endl;
//    }
//    return;
//}

/// GET(2) function.
/// Retrives data(stirngs,int,float and null) from the location within the database specified in the endpoint(also prints it to the console). 
/// The data is retrived in the form of a map that represent the JSON message. 
std::unordered_map<std::string, std::string> firebase::GET(std::string endpoint)
{
    std::unordered_map<std::string, std::string> JSONmp;

    // Perform GET request
    getResponse = httpRequest(baseURL + endpoint, "", "GET");
    std::cout << "GET Response: " << getResponse << std::endl;

    // Parse JSON response
    try {
        nlohmann::json jsonResponse = nlohmann::json::parse(getResponse);
            
        // Access and use the retrieved data
        if (jsonResponse.is_object()) {
            for (auto& [key, value] : jsonResponse.items()) {
                if (value.is_string())
                    JSONmp[key] = value;
                else
                    if (value.is_number_float())
                        JSONmp[key] = std::to_string(float(value));
                    else
                        if (value.is_number_integer())
                            JSONmp[key] = std::to_string(int(value));
                        else
                            if (value.is_null())
                                JSONmp[key] = "NULL";
                std::cout << "Key: " << key << ", Value: " << value << std::endl;
            }
        }
        else {
            std::cout << "Retrieved data is not a JSON object." << std::endl;
        }
    }
    catch (nlohmann::json::parse_error& e) {
        std::cerr << "JSON parsing error: " << e.what() << std::endl;
    }
    return JSONmp;
}

/// Delete function.
/// Deletes the data found at the location specified by the endpoint.
void firebase::Delete(std::string endpoint)
{
    deleteResponse = httpRequest(baseURL + endpoint, "", "DELETE");
    std::cout << "DELETE Response: " << deleteResponse << std::endl;
}

