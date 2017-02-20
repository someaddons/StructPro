./gradlew clean
./gradlew build
ZP=$(zipgrep "\"version\":" build/libs/modid-1.0.jar)
VER=$(echo $ZP | grep -o '[0-9]\+\([.][0-9]\+\)*' | head -1)
ZP2=$(zipgrep "\"modid\":" build/libs/modid-1.0.jar)
MODNAME=$(echo $ZP2 | grep -o '[a-zA-Z0-9\.\"]*,' | grep -o '[a-zA-Z]*' | head -1)
ZP3=$(zipgrep "\"mcversion\":" build/libs/modid-1.0.jar)
MCVER=$(echo $ZP3 | grep -o '[0-9]\+\([.][0-9]\+\)*' | head -1)
mkdir bin
cp build/libs/modid-1.0.jar bin/$MODNAME-$VER-$MCVER.jar
