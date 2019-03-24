preferences {
		input name: "t_path", type: "text", title: "Temp Path", description: "Path of Temp", required: true, displayDuringSetup: true
        input name: "h_path", type: "text", title: "Humid Path", description: "Path of Humidity", required: true, displayDuringSetup: true
        input name: "ip", type:"string", title:"IP", description: "IP of server", defaultValue: "111.222.333.444" , required: true, displayDuringSetup: true
		input name: "port", type:"string", title:"Port", description: "Openhab Port", defaultValue: "8080" , required: true, displayDuringSetup: true
}
metadata {

	definition (name: "Temp2", namespace: "pjacobs4", author: "Peter Jacobsen") {
    
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"
        capability "Refresh"
        
        command "get_status"

		fingerprint deviceId: "0x3103", inClusters: "0x32"
		fingerprint inClusters: "0x32"
	}

    attribute "power", "number"
    
	// tile definitions
	tiles
    {
		valueTile("temperature", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "temperature", label:'${currentValue}°',icon: "http://cdn.device-icons.smartthings.com/Weather/weather2-icn@2x.png",
            backgroundColors:[
				[value: 31, color: "#153591"],
				[value: 44, color: "#1e9cbb"],
				[value: 59, color: "#90d2a7"],
				[value: 74, color: "#44b621"],
				[value: 84, color: "#f1d801"],
				[value: 95, color: "#d04e00"],
				[value: 96, color: "#bc2323"]
			]
		}
		valueTile("humidity", "device.humidity", inactiveLabel: false, decoration: "flat") {
			state "humidity", label:'${currentValue}% humidity', unit:""
		}
        standardTile("refresh", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:'refresh', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
		main "temperature"
		details(["temperature", "humidity", "refresh"])
	}
}

def installed() {
	logger.debug "dte energy: installed"
    refresh()
}

def parse(response) {

}

void tparse(physicalgraph.device.HubResponse response) {
	logger.debug response.body
	sendEvent(name: "temperature", value: response.body)
}

void hparse(physicalgraph.device.HubResponse response) {
	logger.debug response.body
	sendEvent(name: "humidity", value: response.body)
}

def tpath() {
	return "/rest/items/${t_path}/state"
}
def hpath() {
	return "/rest/items/${h_path}/state"
}

def port() {
	return "80"
}

def refresh() {
  //setDeviceNetworkId()
  logger.debug "Openhab Connector: refresh"

  try
  {
  	//def t_str = """GET ${tpath()} HTTP/1.1\r\nHOST: pjacobs4:Sineped1*@$ip\r\n\r\n"""
    def t_str = """GET ${tpath()} HTTP/1.1\r\nHOST: $ip:${port()}\r\n\r\n"""
    //logger.debug(t_str)
  	sendHubCommand(new physicalgraph.device.HubAction(t_str, physicalgraph.device.Protocol.LAN,"$ip:${port()}",[callback: tparse]))
  }
  catch (e)
  {
    	log.error "something went wrong: $e"
  }
    try
  {
  	def h_str = """GET ${hpath()} HTTP/1.1\r\nHOST: $ip:${port()}\r\n\r\n"""
    //def h_str = """GET ${hpath()} HTTP/1.1\r\nHOST: pjacobs4:Sineped1*@$ip\r\n\r\n"""
    //logger.debug(h_str)
  	sendHubCommand(new physicalgraph.device.HubAction(h_str, physicalgraph.device.Protocol.LAN,"$ip:${port()}",[callback: hparse]))
  }
  catch (e)
  {
    	log.error "something went wrong: $e"
  }
}

def poll() {
	logger.debug("dte energy: polled")
	refresh()
}

private setDeviceNetworkId(){
  	def iphex = convertIPtoHex(ip)
  	def porthex = convertPortToHex(port())
  	device.deviceNetworkId = "$iphex:$porthex"
  	log.debug "Device Network Id set to ${iphex}:${porthex}"
}

private String convertIPtoHex(ipAddress) {
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
}