import serial
import time
import serial.tools.list_ports as port_list

ports = list(port_list.comports())
ports.sort()
port=(ports[len(ports)-2])
portstr=str(port)
comport=portstr.split()
connected_port=comport[0]
print(connected_port)
ser = serial.Serial(connected_port)
ser.baudrate = 115200
print('started')
#ser.open()Global B
ser.write(bytes(b'reboot\n'))
time.sleep(80)
ser.write(bytes(b'grep | Global B\n'))
ser.write(bytes(b'su\n'))
print('su')
ser.write(bytes(b'setprop sys.usb.config adb\n'))
ser.write(bytes(b'echo "device" > /sys/class/usb_role/intel_xhci_usb_sw-role-switch/role\n'))
#ser.write(b'echo \"device\" > /sys/class/usb_role/intel_xhci_usb_sw-role-switch/role\n')
ser.write(bytes(b'stop adbd\n'))
ser.write(bytes(b'stop usbd\n'))
ser.write(bytes(b'start usbd\n'))
ser.write(bytes(b'start adbd\n'))
print('Adb')
time.sleep(10)
print('Done')
ser.close()

