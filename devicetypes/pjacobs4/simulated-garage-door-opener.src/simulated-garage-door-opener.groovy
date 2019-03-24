metadata {
	definition (name: "Simulated Garage Door Opener", namespace: "pjacobs4", author: "pjacobs4") {
		capability "Actuator"
		capability "Door Control"
        capability "Garage Door Control"
		capability "Contact Sensor"
		capability "Refresh"
		capability "Sensor"
        }

	simulator {
		
	}

	tiles {
		standardTile("toggle", "device.door", width: 2, height: 2) {
			state("closed", label:'${name}', action:"door control.open", icon:"st.doors.garage.garage-closed", backgroundColor:"#79b821", nextState:"closed")
			state("open", label:'${name}', action:"door control.close", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e", nextState:"open")
			state("opening", label:'${name}', action:"door control.open", icon:"st.doors.garage.garage-closed", backgroundColor:"#ffe71e")
			state("closing", label:'${name}', action:"door control.open", icon:"st.doors.garage.garage-open", backgroundColor:"#ffe71e")	
		}
		standardTile("refresh", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:'refresh', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
		main "toggle"
		details(["toggle", "refresh"])
	}
}
preferences {
    section() {
        input "thedoor", "capability.doorControl"
		input name: "path", type: "text", title: "Path", description: "Path of Door", required: true, displayDuringSetup: true
		input name: "btn_name", type: "text", title: "Button", description: "Path of Button", required: true, displayDuringSetup: true
		input name: "ip", type:"string", title:"IP", description: "IP of server", defaultValue: "111.222.333.444" , required: true, displayDuringSetup: true
		input name: "port", type:"string", title:"Port", description: "Openhab Port", defaultValue: "8080" , required: true, displayDuringSetup: true
		
    }
}
def parse(String description) {
    log.debug "parse(${description})"
}

def refresh() {
    get_status()
}
def initialize(){
	 get_status()
}

def configure() {
}

void gparse(physicalgraph.device.HubResponse response) {
	logger.debug response.body
	def aa=response.body
	if(aa=='0')
	{
   		sendEvent(name: "door", value: "closed")
        sendEvent(name: "contact", value: "closed")
      	log.debug "closed"
    }
    if(aa=='1'){
       	sendEvent(name: "door", value: "open")
        sendEvent(name: "contact", value: "open")
       	log.debug "open"
    }
    if(aa=='2'){
       	sendEvent(name: "door", value: "opening")
    }
    if(aa=='3'){
      	sendEvent(name: "door", value: "closing")
    }

}

def open() {
	send_push()
}

def close() {
	send_push()
}

def path() {
	return "/rest/items/${path}/state"
}

def push_path() {
	return "/rest/items/${btn_name}"
	//return "/CMD?${btn_name}=TOGGLE"
}

def get_status() {
	def t_str = """GET ${path()} HTTP/1.1\r\nHOST: $ip:$port\r\n\r\n"""
  	sendHubCommand(new physicalgraph.device.HubAction(t_str, physicalgraph.device.Protocol.LAN,"$ip:$port",[callback: gparse]))
}

def send_push() {
	def request = [
    method: "POST",
    path: push_path(),
    headers: [
   		HOST: "$ip:$port",
        'CONTENT-TYPE': "text/plain",
        'ACCEPT': "application/json"
		],
    body: "ON"]
  	sendHubCommand(new physicalgraph.device.HubAction(request))
    log.debug "Button Pushed: Garage"
}