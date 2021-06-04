import builtins as __builtin__
import os, sys
import json
import pickle
import time
from datetime import datetime
import logging

# Basic logging setup
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger()
logger.addHandler(logging.StreamHandler())  # pass logging to console

# include module (in path)
sys.path.append("/GMVehicleSim")
from GMVehicleSim import *

_gmVehicleSim = GMVehicleSim()

microsecond_now = lambda: int(round(datetime.now().microsecond))

def close(sig, frame):
  """
  Close (ctrl-c)
  Args:
      N/A
  Returns:
      None
  """  
  _gmVehicleSim.close() # Note: will finally close when log queue is empty (since is slower and has to catch-up)
  sys.exit(0)
signal.signal(signal.SIGINT, close) # register OS signal

# Template as starting point to build off of...
def main():
  # Example:
  payload_Tx = []
 
  # v1 style
  #entry  = {'msgType': 'lscan', 'signalName': 'FrtCmrBlckdIO', 'value': '1'}
  #payload_Tx.append(entry)
  # v2 style
  
#IndTmpStngStRL value=1 (Temperature State)

  

  #LFSCDS_DispReq=0(For Off)=1(For On)
  entry = {'Type': 'Raw', 'Mode': 'RAW', 'Id': '4CC', 'Value': '5003904760208100'}
  payload_Tx.append(entry)
  entry = {'Type': 'Raw', 'Mode': 'RAW', 'Id': '4E2', 'Value': 'A192DBC45E3678FE'}
  payload_Tx.append(entry)
  
  _gmVehicleSim.open() # defaults to localhost and correct port, also waits until socket is ready

  try:
    # Tx          
    if _gmVehicleSim.send(  payload_Tx): # send
      pass

    time.sleep(0.125) # do not want to kill CPU, so play nice, but also go FAST...
  except Exception as ex:
    # Todo: better with reconnect and timeout and ....
    logger.error(ex)
    close(None, None)

""" Main   entry """
if __name__== "__main__":
  main()
  close(None,None)