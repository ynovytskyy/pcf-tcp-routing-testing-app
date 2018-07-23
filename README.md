
### PCF TCP Routing Testing App

Handy app to test behaviour of TCP routing in PCF.
e.g. When one TCP port mapped to more than one app, etc.

```
cf push tcp-routing-test-1
cf push tcp-routing-test-2

# given that 'cf-tcpapps.io' is your TCP router domain
cf map-route tcp-routing-test-1 cf-tcpapps.io --random-port
cf map-route tcp-routing-test-2 cf-tcpapps.io --port <reuse same port assigned by previous command>


# from multiple terminals to test concurrent connections to 'tcp-routing-test-1' and 'tcp-routing-test-2':
nc cf-tcpapps.io <use assigned TCP port>>
```
