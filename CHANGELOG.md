* Implemented automatic parsing of modpack information from supported launchers.
    * CurseForge App ✔
    * Prism Launcher ✔
    * ATLauncher ✔
    * GDLauncher ✔
* Fixed startup crash relating to the refmap not being set correctly in some build artifacts at random.
    * This didn't occur when launching the game from my IDE, which is why i didn't catch it earlier. (Kind of an embarrassing mistake, as the fix was just to turn off parallelization in Gradle)
