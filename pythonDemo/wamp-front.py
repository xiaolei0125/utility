try:
    import asyncio
except ImportError:
    # Trollius >= 0.3 was renamed
    import trollius as asyncio

from os import environ
from autobahn.asyncio.wamp import ApplicationSession, ApplicationRunner
import time

try:
    import thread
except ImportError:  #TODO use Threading instead of _thread in python3
    import _thread as thread
import threading

from asyncio import Queue
from asyncio import Lock


mutex = threading.Lock()
flag="hi"


class Component(ApplicationSession):
    global selfd
    
    @asyncio.coroutine
    def onJoin(self, details):
        print("onJoin");

        global flag
        global mutex

        print("onJoin2");
        #str1 = yield from self.call(u'com.arguments.ping')
        #print("Pinged!"+str1)

        res = yield from self.call(u'com.arguments.add2', 2, 3)
        print("Add2: {}".format(res))

        starred = yield from self.call(u'com.arguments.stars')
        print("Starred 1: {}".format(starred))

        orders = yield from self.call(u'com.arguments.orders', u'coffee')
        print("Orders 1: {}".format(orders))

        orders = yield from self.call(u'com.arguments.orders', u'coffee', limit=10)
        print("Orders 2: {}".format(orders))

        arglengths = yield from self.call(u'com.arguments.arglen')
        print("Arglen 1: {}".format(arglengths))

        while True:
            print("Try to acquire lock, msg:"+flag)
            #yield from asyncio.sleep(3)
            #yield from lock
            mutex.acquire()
            arglengths = yield from self.call(u'com.arguments.ping',flag)
            print("Arglen 1: {}".format(arglengths))
            #yield from q.get()
            print("Task is finished!")
            
        #self.leave()

    def onDisconnect(self):
        print("onDisconnect");
        asyncio.get_event_loop().stop()

    def call2(self):
        print("call2");
        #Str1 = ApplicationSession.call(selfd,procedure=u'com.arguments.arglen')
        #print("Arglen 1: {}".format(arglengths))
        return "heee,c"


def func2():
    print("MouseMove call211")
    time.sleep(3)
    global flag
    global mutex
    while True:
     
        content=input("input:")
        flag=content
        mutex.release()
        print("input is finished, lock is release!")
        time.sleep(1)
        #lock.locked()
        
    #selfd.call(u'com.arguments.arglen')
    #cg.call2()   
    #Str1 = yield from selfd.call(u'com.arguments.arglen')
    #print("MouseMove Arglen 1: {}".format(arglengths))
        
if __name__ == '__main__':
    print("start")
    thread.start_new_thread(func2, ())
    #func2()
    
    runner = ApplicationRunner(
        environ.get("AUTOBAHN_DEMO_ROUTER", "ws://hz2.byodwork.cn:8088/ws"),
        u"realm1",
        debug_wamp=False,  # optional; log many WAMP details
        debug=False  # optional; log even more details
    )

    runner.run(Component)
    
    #str1 = Component.call2()
    #str1 = selfd.call(procedure=u'com.arguments.ping')
    #str1 = selfd.call(u'com.arguments.ping')
    #print("Pinged22!"+str1)
    print("End")
