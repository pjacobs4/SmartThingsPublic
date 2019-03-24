/**
 *  Openhab Switch
 *
 *  Copyright 2017 Peter Jacobsen
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Openhab Switch", namespace: "pjacobs4", author: "Peter Jacobsen") {
		capability "Actuator"
		capability "Switch"
		capability "Configuration"
		capability "Refresh"
		capability "Sensor"
		capability "Outlet"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
    multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: 'On', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState: "Off"
				attributeState "off", label: 'Off', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "On"
            }
        }
        standardTile("refresh", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:'refresh', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
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

def on() {
	log.debug "Executing 'on'"
    send_on()
    get_status()
	// TODO: handle 'on' command
}

def off() {
	log.debug "Executing 'off'"
    send_off()
    get_status()
	// TODO: handle 'off' command
}


def parse(String description) {
    log.debug "parse(${description})"
}

def refresh() {
    get_status()
    log.debug "Refreshing Status"
}
def initialize(){
	 get_status()
}

void gparse(physicalgraph.device.HubResponse response) {
	log.debug response.body
	def aa=response.body
    if(aa=='ON'){
		sendEvent(name: "switch", value: "on")
    }
    if(aa=='OFF'){
      	sendEvent(name: "switch", value: "off")
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

def on_path() {
    return "/rest/items/${btn_name}"
	//return "/CMD?${btn_name}=ON"
}
def off_path() {
	return "/rest/items/${btn_name}"
	//return "/CMD?${btn_name}=OFF"
}

def get_status() {
	def t_str = """GET ${path()} HTTP/1.1\r\nHOST: $ip:$port\r\n\r\n"""
  	sendHubCommand(new physicalgraph.device.HubAction(t_str, physicalgraph.device.Protocol.LAN,"$ip:$port",[callback: gparse]))
}

def send_off() {
	def request = [
    method: "POST",
    path: off_path(),
    headers: [
   		HOST: "$ip:$port",
        'CONTENT-TYPE': "text/plain",
        'ACCEPT': "application/json"
		],
    body: "OFF"]
  	sendHubCommand(new physicalgraph.device.HubAction(request))
    log.debug "Button Pushed: Off"
}

def send_on() {
	def request = [
    method: "POST",
    path: on_path(),
    headers: [
   		HOST: "$ip:$port",
        'CONTENT-TYPE': "text/plain",
        'ACCEPT': "application/json"
		],
    body: "ON"]
  	sendHubCommand(new physicalgraph.device.HubAction(request))
    log.debug "Button Pushed: On"
}