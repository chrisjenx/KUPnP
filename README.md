# KUPnP
"Kew-pin" is a lighter weight Kotlin based UPnP client, based on modern reactive principles.


# Work in Progress

- There will be no distribution or support until I hit a stable point. 
- API will change regularly. 
- Only use for reference until I start hitting versions.


# Modules

## Discovery

This module handles SSDP (Simple Search Discovery Protocol). At the moment we only support Multicast control point.

**Steps**
 - Finds Active Non-local network interfaces
 - Grabs their Addresses (Currently IPv4)
 - Creates a MulticastSocket for each Address and binds too it.
 - Once bound, sends out 3 SSDP Messages at 200..MX intervals to allow for packet loss and network congestion.


### Searching for Devices:

Using `SSDPMessage.search(searchString, mx)` you can define optional parameters.

- `searchString` This is the Search String that identifiers, lookup ST in the UPnP spec.
- `mx` How long a device can wait to respond. This is capped between 1-5 seconds as per spec. (Default 3)


# References

- [Google Physical Web](https://github.com/google/physical-web/blob/master/android/PhysicalWeb/app/src/main/java/org/physical_web/physicalweb/ssdp/Ssdp.java)
- [Cling UPnP Client](https://github.com/4thline/cling/blob/master/core/src/main/java/org/fourthline/cling/transport/impl/DatagramIOImpl.java)