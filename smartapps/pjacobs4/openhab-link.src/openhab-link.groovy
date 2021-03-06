/**
 *  openhab_link
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
definition(
    name: "openhab_link",
    namespace: "pjacobs4",
    author: "Peter Jacobsen",
    description: "Openhab",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: true)


preferences {
    page(name: "selectDevices", install: false, uninstall: true, nextPage: "viewURL") {
    	section("Allow endpoint to control this thing...") {
			input "doors", "capability.doorControl", title: "Which Garage Door"
			label title: "Assign a name", required: false
			mode title: "Set for specific mode(s)", required: false
        }
    }
    page(name: "viewURL", title: "viewURL", install: true)
}

def installed() {
	log.debug "Installed with settings: ${settings}"
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()

}

mappings {
    path("/update") {
        action: [
            GET: "update_g"
        ]
    }
}

void update_g() {
	log.debug "Updated with settings: ${settings}"
    doors?.refresh()
}
def generateURL() {
	createAccessToken()
	["https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/active", "?access_token=${state.accessToken}"]
}
def viewURL() {
	dynamicPage(name: "viewURL", title: "HTTP Garage Endpoint", install:!resetOauth, nextPage: resetOauth ? "viewURL" : null) {
			section() {
                generateURL() 
                def pathstring = "/api/smartapps/installations/${app.id}/update?access_token=${state.accessToken}"
				paragraph "Update:  ${apiServerUrl(pathstring)}"
			}
	}
}