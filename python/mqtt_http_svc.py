#!/usr/bin/env python
# -*- coding: utf-8 -*-

#pip install paho-mqtt
#pip install bottle
#curl -d "topic=/mytopic" -d "message=fuck88444" http://192.168.0.15:8100/pub


import sys
import bottle
from bottle import response, request
import paho.mqtt.client as mqtt
try:
    import thread
except ImportError:  #TODO use Threading instead of _thread in python3
    import _thread as thread


client=None


# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, rc):
    print("Connected with result code "+str(rc))
    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("/mytopic")
	
# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic+" "+str(msg.payload))

def mqtt_connect():
    global client
    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_message = on_message
    #client.username_pw_set
    client.connect("wh.xiaolei.site", 1883, 60)
    client.loop_forever()

#--------------------------------------------
app = application = bottle.Bottle()

@app.route('/pub', method='POST')
def auth():
    response.content_type = 'text/plain'
    response.status = 403

    # data = bottle.request.body.read()

    topic = request.forms.get('topic')
    message = request.forms.get('message')
    print("topic: "+topic)
    print("message: "+message)

    global client
    client.publish(topic, payload=message, qos=0, retain=False)
  
    if topic == 'frankie' and message == 'xl':
        print("Got it")
        response.status = 200
        
    response.status = 200
    return "ok"


if __name__ == '__main__':

    thread.start_new_thread(mqtt_connect, ())

    bottle.debug(True)
    bottle.run(app,
        # server='python_server',
        host= "0.0.0.0",
        port= 8100)


