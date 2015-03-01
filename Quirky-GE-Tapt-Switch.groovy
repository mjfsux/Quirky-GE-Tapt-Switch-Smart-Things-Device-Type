/**
  *  Custom Device type for Quirky GE Tapt
  *
  *  Author Matt Frank using code from JohnR / John Rucker's Dual Relay Controller
  *
  *  Date Created: 1/11/2015
  *  Last Modified: 1/11/2015
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
   definition (name: "Quirky GE Tapt Switch", namespace: "mattjfrank", author: "Matt Frank") {
         capability "Refresh"
         capability "Polling"
         capability "Sensor"
         capability "Configuration"
         capability "Switch"

      fingerprint profileId: "0104", inClusters: "0000,0003,FC20", outClusters: "0019"


   }

   // simulator metadata
   simulator {
     }

   // UI tile definitions
   tiles {



     standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
       state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
       state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
     }

         standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
       state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
     }

     main (["switch"])
     details (["switch", "refresh"])
   }
 }

 // Parse incoming device messages to generate events
 def parse(String description) {
     log.debug "Parse description $description"
     def name = null
     def value = null

//     if (description?.startsWith("catchall: 0104 0006 01")) {
//        log.debug "On/Off command received"
//        if (description?.endsWith(" 01 0000 00 4F86 00 00 0000 0B 01 0081")){
//          name = "switch"
//            value = "off"}
//
//        else if (description?.endsWith(" 01 0000 00 4F86 00 00 0000 0B 01 0181")){
//          name = "switch"
//            value = "on"}
//
//    }

      if (description?.startsWith("catchall: 0104 0006 02")) {
         log.debug "On/Off command received"
         if (description ==~ /.*01 0000 00 \w{4} 00 00 0000 0B 01 0000/){
           name = "switch"
             value = "off"}

        else if (description ==~ /.*01 0000 00 \w{4} 00 00 0000 01 01 0000001000/){
          name = "switch"
            value = "off"}

         else if (description ==~ /.*01 0000 00 \w{4} 00 00 0000 0B 01 0100/){
           name = "switch"
             value = "on"}

         else if (description ==~ /.*01 0000 00 \w{4} 00 00 0000 01 01 0000001001/){
           name = "switch"
             value = "on"}
     }
   def result = createEvent(name: name, value: value)
     log.debug "Parse returned ${result?.descriptionText}"
     return result
 }

 // Commands to device


 def on() {
   log.debug "Switch on()"
   sendEvent(name: "switch", value: "on")
   "st cmd 0x${device.deviceNetworkId} 0x02 0x0006 0x1 {}"
 }

 def off() {
   log.debug "Switch off()"
   sendEvent(name: "switch", value: "off")
   "st cmd 0x${device.deviceNetworkId} 0x02 0x0006 0x0 {}"
 }

 def poll(){
   log.debug "Poll is calling refresh"
   refresh()
 }

 def refresh() {
   log.debug "sending refresh command"
     def cmd = []

//     cmd << "st rattr 0x${device.deviceNetworkId} 0x01 0x0006 0x0000"	//  not even sure what endpoint 1 is
     cmd << "st rattr 0x${device.deviceNetworkId} 0x02 0x0006 0x0000"	//  on / off value

     cmd
 }



 def configure() {
   log.debug "Binding SEP  0x02  Binding SEP 0x01"
     def cmd = []
     cmd << "delay 150"
//     cmd << "zdo bind 0x${device.deviceNetworkId} 0x01 0x01 0x0006 {${device.zigbeeId}} {}"
     cmd << "zdo bind 0x${device.deviceNetworkId} 0x02 0x01 0x0006 {${device.zigbeeId}} {}"

     cmd
 }
