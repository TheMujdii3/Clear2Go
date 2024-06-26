    Firebase Database Class C++ Requierments:
1.  First Introduce the Header file, CPP file and the include folder in the project.
2.  Go to the project propreties C\C++ section and add the 
    PATH to the include directory at Additional Include Libraries.
3.  Still in the C\C++ section go to Language and change the C++ 
    default language to ISO C++17 Standard.
4.  Download Cmake and add the bath to the bin directory to
	Edit the System Enviroment Variables.
    Go to Enviroment Variables, then to System variables
    PATH, double clik and the add the PATH to the bin folder.
5.  Run the following commands in order to install vcpkg and use it to
    install the libcurl library and all other necesarry assets:
    git clone https://github.com/microsoft/vcpkg.git
    cd vcpkg
    .\bootstrap-vcpkg.bat  # On Windows
    ./bootstrap-vcpkg.sh   # On macOS/Linux
    .\vcpkg integrate install
    .\vcpkg install curl
