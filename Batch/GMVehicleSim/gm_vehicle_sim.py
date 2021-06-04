import builtins as __builtin__
import socket
import os, sys, signal
import json
import pickle
import time
#from multiprocessing import Queue
from queue import Queue
from collections import deque
import threading
import logging
import traceback
from enum import Enum
from threading import Thread
from multiprocessing.pool import ThreadPool
import copy
import subprocess
from subprocess import check_output

usleep = lambda x: time.sleep(x/1000000.0)

class Architecture(Enum):
  GA ="GA"
  GB= "GB"
  VCU="VCU"

class Visibility(Enum):
  true ="true"
  false= "false"


class Infotainment(Enum):
  PORT1 = 1
  PORT2 = 2

class GMVehicleSim:
    
  infotainment = Infotainment

  def lag_factor(self):
    return self.__lag

  def delay(self, ms):
    delay_ms = float(ms) * 0.001 * self.lag_factor()
    if delay_ms > 0:
      time.sleep(delay_ms)

  def __init__(self, server_address=None, as_server=False, log_file=None):
    """
    Setup
    Args:
        server_address: defaults to localhost with correct port
        as_server: allow to be used as simple single client connection
        rx_disable: Do not store in rx queue is not being used for memory usage.
    Returns:
        None
    """
    self._log = logging.getLogger(__name__)

    self.__lag = 0.95
    self._as_server = as_server

    if not server_address:
      server_address = ('127.0.0.1', 55555)
    self._server_address = server_address

    self._shutdown = threading.Event()
    self._lock = threading.Lock()

    self._rx_queue = None
    self._tx_queue = None
    self._log_queue = None

    self._on_rx_callback = []

    if log_file is None:
      self._log_file_path = os.path.basename(__file__)  +".log"
    else:
      self._log_file_path = log_file

    self._threads = []
    '''
    signal.signal(signal.SIGTERM, service_shutdown)
    signal.signal(signal.SIGINT, service_shutdown)
    '''

    self._connection = None
    self._connected = False

  def _find_file(self, contains,as_json=True):
    pathname = os.path.dirname(sys.argv[0])   
    fullpathname = os.path.abspath(pathname)

    # two possible locations ?
    find="GMVehicleSimulator"
    index = fullpathname.rfind(find)
    if index < 0:
      find = "gmvehiclesim"

    index = fullpathname.rfind(find)
    rootpath = fullpathname[:index] # Returns from the beginning to pos 3
    rootpath = os.path.join(rootpath, find)
    rootpath = os.path.normpath(rootpath)

    filepath = None
    for root, dirs, files in os.walk(rootpath):
      for file in files:
          if file.endswith(contains):
               filepath = os.path.join(root, file)
               break

    with open(filepath) as file: 
        if as_json:
            return json.load(file)
        else:
            return file

  def launch_simulator(self):
    path = self._find_file("GMVehicleSim.exe", False)
    executable = path.name 
    dirname = os.path.dirname(executable)
    dir = dirname
    cmdline = "GMVehicleSim.exe"
    p= subprocess.call(cmdline, cwd=dir,shell=True)

  def config(self, settings):
    arch=settings['arch'].value
    vis =settings['visible'].value
    lines = self._find_file("GMVehicleSim.exe.Config", False)
    path = lines.name

    str = ""

    with open(path, "r") as file:
         for line in file:
            if 'simulator' in line:
                text = line
                text = text[:32] + arch +  '\"/>\n'
                str += text
            elif 'visible' in line:
                txt_visble=line
                txt_visble=txt_visble[:30] + vis +  '\"/>\n'
                str += txt_visble
            else:
                str += line
                
    with open(path, "w") as afile:
        afile.write(str)

  def db(self, port = Infotainment.PORT1):
    hardware = self._find_file("hardware.json")
    filename = "arxml"+hardware['name']+str(port.value)+".json"

    data = self._find_file(filename)
    return data

  def history(self):
    """
    Read contents of log
    Returns:
        None
    """
    lines = []
    with open(self._log_file_path) as fp:        
      line = fp.readline()
      while line:
        lines.append(line)
    return lines

  def log(self, *args, **kwargs):
    """
    Send to logging and to file
    Args:
        same as "print"
    Returns:
        None
    """
    self._log_queue.put({'args': args, 'kwargs': kwargs})


  def is_connected(self):
    if self._as_server:
      return self._connected 
    return self._connected and self._connection is not None


  def open(self, blocking=False):    
    """
    Connect to socket server
    Args:
        blocking: Keeps trying server until connected
    Returns:
        None
    """
    self._log.info('connecting with: ' + str(self._server_address))

    # self.close() # clear everything

    # reset?
    self._rx_queue = deque([])
    self._tx_queue = Queue()
    self._log_queue = Queue()

    self._shutdown.clear()
    
    while(True):
      BUFFER_SIZE = 512000 * 2 * 5
      try:
        if self._as_server is True:
          self._server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
          self._server.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
          self._server.setsockopt(socket.SOL_SOCKET, socket.SO_SNDBUF, BUFFER_SIZE)
          self._server.setsockopt(socket.SOL_SOCKET, socket.SO_RCVBUF, BUFFER_SIZE)
          self._server.bind(self._server_address)          
          self._server.listen(1) # allow only one client (so far)
        else:
          self._connection = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
          self._connection.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)                
          self._connection.setsockopt(socket.SOL_SOCKET, socket.SO_SNDBUF, BUFFER_SIZE)
          self._connection.setsockopt(socket.SOL_SOCKET, socket.SO_RCVBUF, BUFFER_SIZE)
          self._connection.connect(self._server_address)    
          self._connection.settimeout(None)
          #self._connection.settimeout(0.1/128) # None=blocking, 0 nonblocking
      except Exception as ex:
        self._log.warn(ex)
        time.sleep(1)
        pass # eat-it
      else:
        self._connected = True
        break
      finally:
        if not blocking:
          break    

    self._pool = ThreadPool(600) # 1 Mhz (~1k messages/second)

    if len(self._threads) <= 0:
      if self._as_server is True:
        self._threads.append(Thread(target=self._server_handler , args=[]))
      self._threads.append(Thread(target=self._tx_handler , args=[]))
      self._threads.append(Thread(target=self._rx_handler , args=[])) 
      self._threads.append(Thread(target=self._rx_callback_handler , args=[])) 
      self._threads.append(Thread(target=self._log_handler , args=[])) 
      self._threads.append(Thread(target=self._keepalive_handler , args=[])) 

      for thread in self._threads:
        thread.start()
      
      
  def close(self):
    # wait for log to finish writing...
    if self._log_queue is not None:
      while not self._log_queue.empty(): # or self._log_queue.unfinished_tasks > 0:
        time.sleep(0.5)    

    self._tx_queue = None
    self._rx_queue = None

    self._connection = None
    self._connected = False

    self._shutdown.set()

    try:
      self._connection.shutdown()#socket.SHUT_WR)
      self._connection.close()
    except:
      pass

  def on_receive(self, callback):
    self._on_rx_callback.append(callback)

  def _receive(self, all=False):
    """
    Pulls from Rx Queu
    Returns:
        Return signal list, else None
    """
    result = None
    try:
      if all:
        result = []
        while len(self._rx_queue) > 0:
          temp = self._rx_queue.popleft()
          result.append(temp)         
      else:
        result = self._rx_queue.popleft()
        #length = len(self._rx_queue)    
    except Exception as ex:
      result = None
    
    #self._rx_queue.task_done() 
    return result


  def send(self, payload):
    """
    Adds to Tx Queue
    Args:
        signals: Arrary of signals.
    Returns:
        Return True or False if connected
    """
    #with self._tx_queue.mutex:
    if self._tx_queue is not None:
      if len(payload) > 0:
        # Important not know number, but if send to many errors on json parsing...
        chunks = self._divide_chunks(payload, 100)
        for chunk in chunks:   
          self._tx_queue.put(chunk)
    else:
      pass
    return self._connected

  
  def _keepalive_handler(self):    
    while not self._shutdown.is_set():
      try:
        if not self._connected:
          self.open(False)
      except:
        pass
      time.sleep(0.500)


  def _log_handler(self):    
    while not self._shutdown.is_set():
      try:
        # keep open, less cycle time
        with open(self._log_file_path, 'a') as f:
          while not self._log_queue.empty():  
            try:
              item = self._log_queue.get()
              #self._log_queue.task_done() 
              if item:      
                __builtin__.print(*item['args'],**item['kwargs'], file=f)    
                #self.logger.info(*item['args'],**item['kwargs'])                  
            except Exception as ex:
              print(traceback.format_exc())
              print(ex) # Todo: handle better
          f.flush() # force flush if not done automatically
      except Exception as ex:
        print(traceback.format_exc())
        print(ex) # Todo: handle better  


  def _tx_handler (self):
    while not self._shutdown.is_set():  
      if self._tx_queue is not None and not self._tx_queue.empty() and self._connected:
        item = None
        #with self._tx_queue.mutex:
        item = self._tx_queue.get()
        #self._tx_queue.task_done()

        #length = self._tx_queue.qsize()
        #self.logger.debug("Tx Queue: " + str(length))        
        if item:
          try:
            tx_buffer = json.dumps(item, indent=0).encode()      
            if tx_buffer:
              payload = tx_buffer
              while not self._shutdown.is_set():
                try:
                  self._connection.sendall(payload)  
                  break # good
                except socket.error:
                  self._connected = False
                  time.sleep(0.125)
                  continue 
          except Exception as ex:
            if self._shutdown.is_set():
              break
            self._log.error(traceback.format_exc())
            self._log.error(ex) # Todo: handle better
          finally:
            pass #tx_queue.task_done()              
      else:
        time.sleep(0.125)


  def _rx_handler (self):
    recv_buffer = 2048 # ? guess & check, Note: screwing with this kills timeing
    rx_buffer = "" #bytearray() # buffer until can parse full list
    rx_buffer_temp = None
    while not self._shutdown.is_set():
      try:        
        if self._connected and self._connection:
          try:
            rx_buffer_temp = self._connection.recv(recv_buffer)
            rx_buffer_temp_length = len(rx_buffer_temp)
            recv_buffer = max(recv_buffer, rx_buffer_temp_length)  # keep to the max needed/found
            if rx_buffer_temp_length <= 0:
              usleep(1)
              continue
            rx_buffer = rx_buffer + rx_buffer_temp.decode()
          except socket.error as ex:
            if 'timed out' in ex.args:
              time.sleep(0.125)
              continue
            else:
              self._connected = False                      

          # parse signals array
          start = '['
          end = ']'
          start_index = rx_buffer.find(start)
          end_index = rx_buffer.find(end)

          if start_index < 0 or end_index < 0:
            # if for whatever reason buffer data is missed and goes whacky (should not happen)
            # happens under bad disconnect (un-plug USB) with partial data stream
            if abs(end_index) < abs(start_index):
              rx_buffer = rx_buffer[:end_index]
            continue

          data = rx_buffer[start_index:end_index+1]
          rx_buffer = rx_buffer[:start_index] + rx_buffer[end_index+1:]     

          try:
            payload = json.loads(data)       
          except:
            continue # wait for more data

          self._rx_queue.append(payload)

          #length = len(self._rx_queue) #self._rx_queue.qsize()
          #self.logger.debug("Rx Queue: " + str(length))
        else:
          time.sleep(0.125) # not completely lock up system!
      except Exception as ex:
        if self._shutdown.is_set():
          break
        self._log.error(traceback.format_exc())
        self._log.error(ex) # Todo: handle better
      finally:
        pass

  def _rx_callback_handler(self):
    while not self._shutdown.is_set():       
      try:
        if not self._on_rx_callback:  # if empty, no one to send to
          self._rx_queue.clear()
          time.sleep(0.125)
        while True:
          payload = self._receive()
          if not payload:  # if no payload leave
            break
          for callback in self._on_rx_callback:                        
              #Thread(target = self._spawn_callback, args = (callback, payload,)).start()
              self._pool.apply_async(GMVehicleSim._spawn_callback, (callback,copy.copy(payload),))              
              #callback(item)  # Note: order of messages are processed matters, if not then thread away!
      except Exception as ex:
        if self._shutdown.is_set():
          break
        self._log.error(traceback.format_exc())
        self._log.error(ex) # Todo: handle better
      finally:
        pass
      usleep(1)


  def _spawn_callback(callback, payload):
    try:
      if not callback:
        return
      if not payload:
        return

      for item in payload:
        if item is not None:
          callback(item)
    except Exception as ex:
      pass # ????
      # print(ex)

  def _server_handler (self):
    while not self._shutdown.is_set(): 
      try:
          # waits for connection
          connection, address = self._server.accept() # NOTE:  single client connection support (currently)
          self._connection = connection
          #self._connection.setblocking(True)
          self._log.info("Connected: " + str(address))
      except Exception as ex:
        if self._shutdown.is_set():
          break
        self._log.error(traceback.format_exc())
        self._log.error(ex) # Todo: handle better
      finally:
        pass

  def _divide_chunks(self, l, n):      
    # looping till length l 
    for i in range(0, len(l), n):  
      yield l[i:i + n]

  def getModelYear():
    adb_output = check_output(["adb", "shell","getprop ro.build.display.id"])
    val=adb_output.decode("utf-8")
    if 'gminfo37' in val:
      print("My22 CSM is connected")
      return 'model:gminfo37'
    elif 'gminfo38' in val:
      print("My23 CSM is connected")
      return 'model:gminfo38'
    elif 'aegean' in val:
      print("My23 VCU is connected")
      return 'model:aegean'
