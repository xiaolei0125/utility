# -*- coding: utf-8 -*-

import pythoncom, pyHook 
from ctypes import *
import os
import sys
import time  
import win32api
import win32con
try:
    import thread
except ImportError:  #TODO use Threading instead of _thread in python3
    import _thread as thread

global lockFlag
lockFlag=0

def MouseMove():
    while True:
        global lockFlag
        #win32api.mouse_event(win32con.MOUSEEVENTF_RIGHTDOWN, 0, 0)
        lockFlag=0
        win32api.mouse_event(win32con.MOUSEEVENTF_MOVE, -30, -30, 0, 0)
        time.sleep(1)
        win32api.mouse_event(win32con.MOUSEEVENTF_MOVE, 30, 30, 0, 0)
        lockFlag=1
        #win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN, 0, 0)
        time.sleep(300)

def TimerExit():
    time.sleep(3600)
    print("Time:Exit")
    a=windll.LoadLibrary('user32.dll') 
    a.LockWorkStation()
    sys.exit(0)  #这种方式无法在子线程中将整个程序退出
    
def OnMyEvent(event):
    print("MessageName:"+event.MessageName)
    #print("Message:"+event.Message)
    
    now = time.strftime("%Y-%m-%d %H:%M:%S")
    print("Time:"+now)

    global lockFlag
    if lockFlag == 1 :
        a=windll.LoadLibrary('user32.dll') 
        a.LockWorkStation()   
        sys.exit(0)
    
    # 返回 True 可将事件传给其它处理程序，否则停止传播事件
    #return True

time.sleep(5)
thread.start_new_thread(TimerExit, ())

# 创建钩子管理对象
hm = pyHook.HookManager()
 
# 监听所有键盘事件
hm.KeyDown = OnMyEvent
# 设置键盘“钩子”
hm.HookKeyboard()
 

# 监听所有鼠标事件
#hm.MouseWheel = OnMyEvent
hm.MouseAll = OnMyEvent # 等效于hm.SubscribeMouseAll(OnMyEvent)

# 设置鼠标“钩子”开始监听鼠标事件
hm.HookMouse()

thread.start_new_thread(MouseMove, ())


# 一直监听，直到手动退出程序
pythoncom.PumpMessages()
