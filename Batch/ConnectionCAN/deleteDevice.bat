:: Click on paired device settings icon--- Another batch
adb -s 324a533443443498 shell input tap 950 980
:: Click on unpaired text
TIMEOUT 3
adb -s 324a533443443498 shell input tap 700 580
:: Click on bluetooth toogle button
TIMEOUT 3
adb -s 324a533443443498 shell input tap 920 380
:: Go to Home screen
adb -s 324a533443443498 shell input keyevent 3
exit
