try:
    import asyncio
except ImportError:
    # Trollius >= 0.3 was renamed
    import trollius as asyncio

from os import environ
from autobahn.asyncio.wamp import ApplicationSession, ApplicationRunner


class Component(ApplicationSession):
    
    @asyncio.coroutine
    def onJoin(self, details):

        def ping(str1):
            print("got msg"+str1)
            return "Hi Frankie, You msg:"+str1

        def add2(a, b):
            return a + b

        def stars(nick="somebody", stars=0):
            return u"{} starred {}x".format(nick, stars)

        # noinspection PyUnusedLocal
        def orders(product, limit=5):
            return [u"Product {}".format(i) for i in range(50)][:limit]

        def arglen(*args, **kwargs):
            return [len(args), len(kwargs)]

        print("regeister")
        yield from self.register(ping, u'com.arguments.ping')
        yield from self.register(add2, u'com.arguments.add2')
        yield from self.register(stars, u'com.arguments.stars')
        yield from self.register(orders, u'com.arguments.orders')
        yield from self.register(arglen, u'com.arguments.arglen')
        print("Registered methods; ready for frontend.")

        def onhello(msg):
           print("Got event: {}".format(msg))

        yield from self.subscribe(onhello, 'com.myapp.topic1')


if __name__ == '__main__':
    runner = ApplicationRunner(
        environ.get("AUTOBAHN_DEMO_ROUTER", "ws://112.74.76.96:8088/ws"),
        u"realm1",
        debug_wamp=False,  # optional; log many WAMP details
        debug=False,  # optional; log even more details
    )

    print("regeister2")
    runner.run(Component)
