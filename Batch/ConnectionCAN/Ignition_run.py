from pywinauto import application
import time
import sys

sys.path.append("/GMVehicleSim")
from GMVehicleSim import *

_gmVehicleSim = GMVehicleSim()

val=GMVehicleSim.getModelYear()
app = application.Application()

dlg_spec=None
if 'model:gminfo37' in val:

    app.connect(title='Global B')
    dlg_spec = app.window(title='Global B')
    dlg_spec.SetFocus()
    #place MY22 CSM signals/msgs
    print("MY22 CSM signals sent")
elif 'model:gminfo38' in val:
    app.connect(title='MY23CSM')
    dlg_spec = app.window(title='MY23CSM')
    dlg_spec.SetFocus()
    #place MY23 CSM signals/msgs
    
    print("MY23 CSM signals sent")
elif 'model:aegean' in val:
    app.connect(title='VCU')
    dlg_spec = app.window(title='VCU')
    dlg_spec.SetFocus()
    #place MY23 VCU signals/msgs
    
    print("MY23 VCU signals sent")
    
#dlg_spec.print_control_identifiers()
dlg_spec.child_window(best_match='Run').click()
time.sleep(2)
dlg_spec.minimize()
