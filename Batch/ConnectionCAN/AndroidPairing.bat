::Open System settings
adb -s 44553630374f3098 shell am start -n com.android.settings/com.android.settings.Settings$BluetoothSettingsActivity
:: Click on Connections text to navigate to bluetooth page
adb -s 44553630374f3098 shell input tap 600 950
::Click on Bluetooth option
adb -s 44553630374f3098 shell input tap 392 571
:: Click on bluetooth toogle button
adb -s 44553630374f3098 shell input tap 960 320
:: Click on our device name
TIMEOUT 11
adb -s 44553630374f3098 shell input tap 312 880
exit
