### Windows Dependencies
* JDK 15+
* wix3:https://github.com/wixtoolset/wix3 
### MacOS Dependencies
* JDK 15+
#### Note
* Ensure that the JDK you are using in IntelliJ is not higher than the system JDK.  
This can cause issues when the runtime created with `jlink` not being able to process the bytecode of the generated jar.
* **Running the App through IntelliJ does not work on MacOS.**  This is due to MacOS security preventing a webview from
accessing a non HTTPS URL.  This is migtigated in a full build with `macos/Info.plist`. Instead, for development on MacOS access
the application via a browser at `http://localhost:7001/game-sets/`
#### Running the App
1. Import the project into IntelliJ
1. Set the following JVM arguments in your run configuration:
   * `-DdatabasePath="database/"`
   * `-DiconFile="resources/public/favicon.ico"`  
   * MacOS Specific
      * `-XstartOnFirstThread` SWT requires starting on the first thread on MacOS.

#### Building a distribution
1. In IntelliJ select the menu `Build | Build Artifacts...`
1. Hover over the artifact with your operating system and choose `build`
1. Open the IntelliJ terminal `View | Tool Windows | Terminal`  
1. Run one of the package scripts:
    * `package.bat` generates a `.msi` for Windows
       * The `.msi` is an installer users can use to install the application. 
    * `package.sh` generates a `.app` for MacOS
       * The `.app` file can be dragged into the `Application` folder in finder to add to Launchpad.
1. After the package script completes a `dist` directory will contain the resulting file. 

![Sample Image of Analysis Screen]("./sample.png" "Analysis Screen")