cd Batch
adb root
adb remount
adb disable-verity
adb push wcp_disable.calovride/update_cache/calibrations
adb reboot
python AdbCmd.py
adb devices
exit





