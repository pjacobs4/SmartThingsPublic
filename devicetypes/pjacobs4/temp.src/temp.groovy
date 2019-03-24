/**
 *  Temp
 *
 *  Copyright 2016 Peter Jacobsen
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
	definition (name: "Temp", namespace: "pjacobs4", author: "Peter Jacobsen") {
    
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"
        capability "Refresh"
        
        command "get_status"
	}


	simulator {
		// TODO: define status and reply messages here
	}
    preferences {
        
        input name: "t_path", type: "text", title: "Temp Path", description: "", required: true, displayDuringSetup: true
        input name: "h_path", type: "text", title: "Humid Path", description: "", required: true, displayDuringSetup: true
    }
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


// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'humidity' attribute
	// TODO: handle 'temperature' attribute

}
def poll(){
	get_status()
}
def refresh() {

    get_status()
}

def configure() {

}

def get_status() {
	def params = [
        uri: "http://pjacobs4:Sineped1*@www.jacobsensmi.com",
        path: "/rest/items/${t_path}/state"
    ]
    def aa
	try 
    {
    	httpGet(params) { resp ->
        	aa =  resp
            aa=aa.data.text
            log.debug "${aa}"
      		sendEvent(name: "temperature", value: aa)
        }	
	}
    catch (e)
    {
    	log.error "something went wrong: $e"
	}
    
    params = [
        uri: "http://pjacobs4:Sineped1*@www.jacobsensmi.com",
        path: "/rest/items/${h_path}/state"
    ]

	try 
    {
    	httpGet(params) { resp ->
        	aa =  resp
            aa=aa.data.text
            log.debug "${aa}"
      		sendEvent(name: "humidity", value: aa)
        }	
	}
    catch (e)
    {
    	log.error "something went wrong: $e"
	}
}