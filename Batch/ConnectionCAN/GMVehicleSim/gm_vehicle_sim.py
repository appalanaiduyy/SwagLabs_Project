import builtins as __builtin__
import socket
import os, sys
import signal
import json
import pickle
import time
from queue import *
from threading import Thread
import threading
import logging
import traceback
from subprocess import check_output

class GMVehicleSim:

  def lag_factor(self):
    return self.__lag

  def __init__(self, server_address=None, rx_disabled=False, as_server=False, log_file=None):
    """
    Setup
    Args:
        server_address: defaults to localhost with correct port
        as_server: allow to be used as simple single client connection
        rx_disable: Do not store in rx queue is not being used for memory usage.
    Returns:
        None
    """
    self.logger = logging.getLogger(__name__)

    self.__lag = 0.85

    self._rx_disabled = rx_disabled
    self._as_server = as_server

    if not server_address:
      server_address = ('localhost', 55555)
    self._server_address = server_address

    self._shutdown = threading.Event()

    self._rx_queue = None
    self._tx_queue = None
    self._log_queue = None

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


  def db():
    """
    Finds and opens ARXML Database from parent application
    Returns:
        Return None or Database in json formate
    """
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
          if file.endswith(".json"):
               filepath = os.path.join(root, file)
               break

    data = None
    with open(filepath) as json_file:  
      data = json.load(json_file)

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


  def open(self, blocking=True):    
    """
    Connect to socket server
    Args:
        blocking: Keeps trying server until connected
    Returns:
        None
    """
    self.logger.info('connecting with: ' + str(self._server_address))

    self.close() # clear everything

    # reset?
    if not self._rx_disabled:
      self._rx_queue = Queue()
    self._tx_queue = Queue()
    self._log_queue = Queue()

    self._shutdown.clear()
    
    while(True):
      try:
        if self._as_server is True:
          self._server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
          self._server.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
          self._server.bind(self._server_address)          
          self._server.listen(1) # allow only one client (so far)
        else:
          self._connection = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
          self._connection.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)        
          self._connection.connect(self._server_address)    
      except Exception as ex:
        self.logger.warn(ex)
        time.sleep(1)
        pass # eat-it
      else:
        break
      finally:
        if not blocking:
          break

    self._connected = True

    self._threads = []  # reset?
    if self._as_server is True:
      self._threads.append(Thread(target=self._server_handler , args=[]))
    self._threads.append(Thread(target=self._tx_handler , args=[]))
    self._threads.append(Thread(target=self._rx_handler , args=[])) 
    self._threads.append(Thread(target=self._log_handler , args=[])) 

    for thread in self._threads:
      thread.start()

      pass

  def close(self):

    # wait for log to finish writing...
    if self._log_queue is not None:
      while not self._log_queue.empty() or self._log_queue.unfinished_tasks > 0:
        time.sleep(1)    

    self._tx_queue = None
    self._rx_queue = None

    self._connection = None
    self._connected = False

    self._shutdown.set()

    try:
      self._connection.shutdown(socket.SHUT_WR)
      self._connection.close()
    except:
      pass

  def receive(self):
    """
    Pulls from Rx Queu
    Returns:
        Return signal list, else None
    """
    result = None
    if self._rx_queue is not None and not self._rx_queue.empty():
      #with self._rx_queue.mutex:
      result = self._rx_queue.get(False) # no wait
      self._rx_queue.task_done() 
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

  def _log_handler(self):    
    while not self._shutdown.is_set():
      try:
        # keep open, less cycle time
        with open(self._log_file_path, 'a') as f:
          while not self._log_queue.empty():  
            try:
              item = self._log_queue.get()
              self._log_queue.task_done() 
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

      time.sleep(3)  # seconds to keep file from opening and closing like crazy, and time to see if more things show up...


  def _tx_handler (self):
    while not self._shutdown.is_set():    
      if self._tx_queue is not None and not self._tx_queue.empty() and self._connection is not None:
        item = None
        #with self._tx_queue.mutex:
        item = self._tx_queue.get()
        self._tx_queue.task_done()

        length = self._tx_queue.qsize()
        self.logger.debug("Tx Queue: " + str(length))
        
        if item:
          try:
            tx_buffer = json.dumps(item, indent=0).encode()      
            if tx_buffer:
              payload = tx_buffer
              try:
                self._connection.sendall(payload)  
              except socket.error:
               self._connected = False
               break                             
          except Exception as ex:
            if self._shutdown.is_set():
              break
            self.logger.error(traceback.format_exc())
            self.logger.error(ex) # Todo: handle better
          finally:
            pass #tx_queue.task_done()    
      else:
        time.sleep(0.05) # not completely lock up system!


  def _rx_handler (self):
    rx_buffer = bytearray() # buffer until can parse full list
    while not self._shutdown.is_set(): 
      try:
        if self._connection is not None:
          recv_peek = 0
          try:
            recv_peek = len(self._connection.recv(1, socket.MSG_PEEK))
          except socket.error:
            self._connected = False

          if recv_peek > 0:  # check if have data yet
            try:
              rx_buffer_temp = self._connection.recv(512000) # ~512kKB (matches server RX as well)
            except socket.error:
              self._connected = False
              continue

            for buff in rx_buffer_temp: # append to buffer
              rx_buffer.append(buff)

            # decode "list" message(s) at a time
            try:
              rx_buffer_temp = rx_buffer.decode()
            except:
              continue  # need more data cannot decode

            # parse signals array
            start = '['
            end = ']'
            start_index = rx_buffer_temp.find(start)
            end_index = rx_buffer_temp.find(end)

            if start_index < 0 or end_index < 0:
              # if for whatever reason buffer data is missed and goes whacky (should not happen)
              # happens under bad disconnect (un-plug USB) with partial data stream
              if end_index < start_index:
                rx_buffer = rx_buffer[:end_index]
              continue

            data = rx_buffer_temp[start_index:end_index+1]
            rx_buffer = rx_buffer[:rx_buffer_temp.find(start)] + rx_buffer[rx_buffer_temp.find(end)+1:]     

            payload = json.loads(data)       
            if self._rx_queue is not None:
              self._rx_queue.put(payload)

              length = self._rx_queue.qsize()
              self.logger.debug("Rx Queue: " + str(length))
          else:
            time.sleep(0.05) # not completely lock up system!
        else:
          time.sleep(0.125) # not completely lock up system!
      except Exception as ex:
        if self._shutdown.is_set():
          break
        self.logger.error(traceback.format_exc())
        self.logger.error(ex) # Todo: handle better
      finally:
        pass

  def _server_handler (self):
    while not self._shutdown.is_set(): 
      try:
          # waits for connection
          connection, address = self._server.accept() # NOTE:  single client connection support (currently)
          self._connection = connection
          #self._connection.setblocking(True)
          self.logger.info("Connected: " + str(address))
      except Exception as ex:
        if self._shutdown.is_set():
          break
        self.logger.error(traceback.format_exc())
        self.logger.error(ex) # Todo: handle better
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
   
