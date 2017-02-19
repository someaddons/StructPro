./gradlew clean
./gradlew build
ZP=$(zipgrep "\"version\":" build/libs/modid-1.0.jar)
VER=$(echo $ZP | grep -o '[+-]\?[0-9]\+\([.][0-9]\+\)\?' | head -1)
mkdir bin
cp build/libs/modid-1.0.jar bin/placemod[$VER].jar