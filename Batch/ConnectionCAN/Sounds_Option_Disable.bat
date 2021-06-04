cd Batch/SettingsCan
adb root
adb remount
adb disable-verity
adb push Audio_Cue_False.calovride /update_cache/calibrations
adb reboot
python AdbCmd.py
adb devices
exit
