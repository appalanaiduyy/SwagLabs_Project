::TIMEOUT 5
:: Click on Bluetooth pairing request
adb -s 44553630374f3098 shell input tap 783 1954
TIMEOUT 3
:: Click on Allow access to contacts?
adb -s 44553630374f3098 shell input tap 777 1928
TIMEOUT 3
:: Click on Allow access to messages?
adb -s 44553630374f3098 shell input tap 777 1928
exit